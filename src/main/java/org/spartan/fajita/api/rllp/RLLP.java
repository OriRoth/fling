package org.spartan.fajita.api.rllp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.ll.LLParser;

public class RLLP {
  public final BNF bnf;
  public final BNFAnalyzer analyzer;
  public final List<Item> items;
  private final Map<NonTerminal, Map<Verb, DerivationRule>> llPredictionTable;
  private Map<Item, Map<Verb, Deque<Item>>> consolidationTable;
  public final Map<Item, Map<Verb, Deque<Item>>> jumpsTable;
  private Map<Item, Map<Verb, Action>> rllPredictionTable;

  public RLLP(BNF bnf) {
    this.bnf = bnf;
    this.analyzer = new BNFAnalyzer(bnf, true);
    items = calculateItems();
    llPredictionTable = createLLPredictionTable();
    consolidationTable = calculateConsolidateTable();
    jumpsTable = calculateJumpsTable();
    rllPredictionTable = calculateRLLPredictionTable();
  }
  private List<Item> calculateItems() {
    List<Item> $ = new ArrayList<>();
    for (DerivationRule r : bnf.getRules())
      for (int i = 0; i <= r.getChildren().size(); i++)
        $.add(new Item(r, i));
    return $;
  }
  private Map<Item, Map<Verb, Action>> calculateRLLPredictionTable() {
    Map<Item, Map<Verb, Action>> $ = new HashMap<>();
    for (Item i : items) {
      if ((!i.readyToReduce()) && i.afterDot().isVerb())
        continue;
      Map<Verb, Action> currentLine = new HashMap<>();
      $.put(i, currentLine);
      for (Verb v : bnf.getVerbs()) {
        if (analyzer.firstSetOf(i.rule.getChildren().subList(i.dotIndex, i.rule.getChildren().size())).contains(v))
          currentLine.put(v, new Action.Push(i, v, consolidate(i, v)));
        else if (analyzer.followSetOf(i.rule.lhs).contains(v)) {
          if (v == SpecialSymbols.$)
            currentLine.put(v, new Action.Accept());
          else
            currentLine.put(v, new Action.Jump(v));
        }
      }
    }
    return $;
  }
  /* This is a very nice code. unfortunately buggy because it was not written as the algorithm said.
   * * In other words, RTFM!;
   @formatter=off
  private Map<Item, Map<Verb, Deque<Item>>> calculateJumpsTable() {
    Map<Item, Map<Verb, Deque<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      $.put(i, new HashMap<>());
      if (i.readyToReduce())
        continue;
      final List<Symbol> suffix = i.rule.getChildren().subList(i.dotIndex + 1, i.rule.getChildren().size());
      for (Verb v : analyzer.firstSetOf(suffix)) {
        for (int j = 0; j < suffix.size(); j++) {
          Symbol s = suffix.get(j);
          if (analyzer.firstSetOf(s).contains(v)) {
            Item jumpLocation = new Item(i.rule, j + i.dotIndex + 1);
            $.get(i).put(v, consolidate(jumpLocation, v));
          }
        }
        if ($.get(i).get(v) == null)
          throw new InternalError("Algorithm fault. for some reason, jumps(" + i + ")[" + v + "] is in first, but wasn't updated");
      }
    }
    return $;
  }
  */
  private Map<Item, Map<Verb, Deque<Item>>> calculateJumpsTable() {
    Map<Item, Map<Verb, Deque<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      Map<Verb, Deque<Item>> jumps = calculateJumps(i);
      $.put(i, jumps);
    }
    return $;
  }
  private Map<Verb, Deque<Item>> calculateJumps(Item i) {
    Map<Verb, Deque<Item>> $ = new HashMap<>();
    for (int j = i.dotIndex + 1; j < i.rule.getChildren().size(); j++) {
      Item jumpLocation = new Item(i.rule, j);
      if (j != i.dotIndex + 1)
        if (!analyzer.isNullable(i.rule.getChildren().subList(i.dotIndex + 1, j - 1)))
          break;
      for (Verb v : analyzer.firstSetOf(i.rule.getChildren().get(j))) {
        if ($.containsKey(v))
          continue;
      $.put(v, consolidate(jumpLocation, v));
      }
    }
    return $;
  }
  private Map<Item, Map<Verb, Deque<Item>>> calculateConsolidateTable() {
    Map<Item, Map<Verb, Deque<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      Map<Verb, Deque<Item>> itemEntry = new HashMap<>();
      for (Verb v : analyzer.firstSetOf(BNFAnalyzer.ruleSuffix(i.rule, i.dotIndex))) {
        Deque<Item> result = calculateConsolidate(i, v);
        itemEntry.put(v, result);
      }
      $.put(i, itemEntry);
    }
    return $;
  }
  private static void addToPredictionTable(Map<NonTerminal, Map<Verb, DerivationRule>> $, Verb v, DerivationRule d) {
    DerivationRule result = $.get(d.lhs).put(v, d);
    if (result != null)
      throw new LLParser.NotLLGrammar(
          "predict[" + d.lhs + "," + v.name() + "] has two conflicting rules : <" + result + "> , <" + d + ">");
  }
  private Map<NonTerminal, Map<Verb, DerivationRule>> createLLPredictionTable() {
    Map<NonTerminal, Map<Verb, DerivationRule>> $ = new HashMap<>();
    for (Symbol nt : bnf.getNonTerminals())
      $.put((NonTerminal) nt, new HashMap<>());
    for (DerivationRule d : bnf.getRules()) {
      for (Verb v : analyzer.firstSetOf(d.getChildren()))
        addToPredictionTable($, v, d);
      if (analyzer.isNullable(d.getChildren()))
        for (Verb v : analyzer.followSetOf(d.lhs))
          addToPredictionTable($, v, d);
    }
    return $;
  }
  private Deque<Item> calculateConsolidate(Item i, Verb v) {
    Deque<Item> $ = new ArrayDeque<>();
    Item current_i = i;
    Symbol Y = current_i.afterDot();
    while (!Y.isVerb()) {
      $.addFirst(current_i);
      DerivationRule r = llPredict((NonTerminal) Y, v);
      if (r.getChildren().size() == 0) { // r is an epsilon move?
        while ($.peekFirst().advance().readyToReduce())
          /* || $.peekFirst().readyToReduce() */
          $.removeFirst();
        $.addFirst($.removeFirst().advance());
      } else
        $.push(new Item(r, 0));
      current_i = $.removeFirst();
      Y = current_i.afterDot();
    }
    $.addFirst(current_i.advance());
    return $;
  }
  public DerivationRule llPredict(NonTerminal Y, Verb v) {
    final Map<Verb, DerivationRule> row = llPredictionTable.get(Y);
    if (row == null)
      throw new IllegalStateException("llPredict( " + Y + " , _ ) does not exist.");
    final DerivationRule $ = row.get(v);
    if ($ == null)
      throw new IllegalStateException("llPredict( " + Y + " , " + v + " ) does not exist.");
    return $;
  }
  public Deque<Item> consolidate(Item i, Verb v) {
    final Map<Verb, Deque<Item>> row = consolidationTable.get(i);
    if (row == null)
      throw new IllegalStateException("consolidate( " + i + " , _ ) does not exist.");
    final Deque<Item> $ = row.get(v);
    if ($ == null)
      throw new IllegalStateException("consolidate( " + i + " , " + v + " ) does not exist.");
    return $;
  }
  public Deque<Item> jumps(Item i, Verb v) {
    final Map<Verb, Deque<Item>> row = jumpsTable.get(i);
    if (row == null)
      throw new IllegalStateException("jumps( " + i + " , _ ) does not exist.");
    final Deque<Item> value = row.get(v);
    if (value == null)
      throw new IllegalStateException("jumps( " + i + " , " + v + " ) does not exist.");
    return value;
  }
  public Action predict(Item i, Verb v) {
    final Map<Verb, Action> row = rllPredictionTable.get(i);
    if (row == null)
      throw new IllegalStateException("predict( " + i + " , _ ) does not exist.");
    final Action $ = row.get(v);
    if ($ == null)
      throw new IllegalStateException("predict( " + i + " , " + v + " ) does not exist.");
    return $;
  }
  public Item getStartItem(Verb initialInput) {
    return items.stream().filter(i -> bnf.getStartSymbols().contains(i.rule.lhs) && i.dotIndex == 0)
        .filter(i -> analyzer.firstSetOf(i.rule.getChildren()).contains(initialInput)).findAny().get();
  }

  public static abstract class Action {
    public static enum ActionType {
      ACCEPT, PUSH, JUMP;
    }

    public static class Accept extends Action {
      @Override public ActionType type() {
        return ActionType.ACCEPT;
      }
      @Override public String toString() {
        return "Accept";
      }
    }

    public abstract ActionType type();

    public static class Jump extends Action {
      public final Verb v;

      public Jump(Verb v) {
        this.v = v;
      }
      @Override public String toString() {
        return "jump(" + v + ")";
      }
      @Override public ActionType type() {
        return ActionType.JUMP;
      }
    }

    public static class Push extends Action {
      public final Deque<Item> itemsToPush;
      private Item i;
      private Verb v;

      public Push(Item i, Verb v, Deque<Item> deque) {
        this.i = i;
        this.v = v;
        this.itemsToPush = deque;
      }
      @Override public String toString() {
        return "push( cons(" + i + "," + v + ")";
      }
      @Override public ActionType type() {
        return ActionType.PUSH;
      }
    }
  }
}
