package org.spartan.fajita.revision.rllp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.BNFAnalyzer;
import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class RLLP {
  static final List<Item> JUMP_ERROR = null;
  public final BNF bnf;
  public final BNFAnalyzer analyzer;
  public final List<Item> items;
  private final Map<NonTerminal, Map<Verb, DerivationRule>> llPredictionTable;
  private final Map<Item, Map<Verb, List<Item>>> consolidationTable;
  private final Map<Item, Map<Verb, List<Item>>> jumpsTable;
  private final Map<Item, Map<Verb, Action>> rllPredictionTable;

  public RLLP(BNF bnf) {
    this.bnf = bnf;
    this.analyzer = new BNFAnalyzer(bnf);
    items = calculateItems();
    llPredictionTable = createLLPredictionTable();
    consolidationTable = calculateConsolidateTable();
    jumpsTable = calculateJumpsTable();
    rllPredictionTable = calculateRLLPredictionTable();
  }
  private List<Item> calculateItems() {
    List<Item> $ = new ArrayList<>();
    for (DerivationRule r : bnf.rules())
      for (int i = 0; i <= r.size(); i++)
        $.add(new Item(r, i));
    return $;
  }
  // NOTE not exactly as the algorithm specified in the paper--[or]
  private Map<Item, Map<Verb, Action>> calculateRLLPredictionTable() {
    Map<Item, Map<Verb, Action>> $ = new HashMap<>();
    for (Item i : items) {
      Map<Verb, Action> currentLine = new HashMap<>();
      $.put(i, currentLine);
      for (Verb v : bnf.verbs) {
        if (analyzer.firstSetOf(BNFAnalyzer.ruleSuffix(i.rule, i.dotIndex)).contains(v))
          if (i.afterDot().isVerb())
            currentLine.put(v, new Action.Advance(i));
          else
            currentLine.put(v, new Action.Push(i, v, consolidate(i, v)));
        else if (analyzer.followSetOf(i.rule.lhs).contains(v) && analyzer.isSuffixNullable(i)) {
          if (v == SpecialSymbols.$)
            currentLine.put(v, new Action.Accept());
          else
            currentLine.put(v, new Action.Jump(v));
        }
      }
    }
    return $;
  }
  private Map<Item, Map<Verb, List<Item>>> calculateJumpsTable() {
    Map<Item, Map<Verb, List<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      Map<Verb, List<Item>> jumps = calculateJumps(i);
      $.put(i, jumps);
    }
    return $;
  }
  // NOTE not exactly as the algorithm specified in the paper--[or]
  private Map<Verb, List<Item>> calculateJumps(Item i) {
    Map<Verb, List<Item>> $ = new HashMap<>();
    for (int j = i.dotIndex + 1; j < i.rule.size(); j++) {
      Item jumpLocation = new Item(i.rule, j);
      if (!analyzer.isNullable(i.rule.getRHS().subList(i.dotIndex + 1, j)))
        break;
      for (Verb v : analyzer.firstSetOf(i.rule.get(j))) {
        if ($.containsKey(v))
          continue;
        $.put(v, consolidate(jumpLocation, v));
      }
    }
    if (!i.readyToReduce() && !analyzer.isSuffixNullable(i.advance()))
      for (Verb v : bnf.verbs) {
        if (v.equals(SpecialSymbols.$))
          continue;
        $.putIfAbsent(v, JUMP_ERROR);
      }
    return $;
  }
  private Map<Item, Map<Verb, List<Item>>> calculateConsolidateTable() {
    Map<Item, Map<Verb, List<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      Map<Verb, List<Item>> itemEntry = new HashMap<>();
      for (Verb v : analyzer.firstSetOf(BNFAnalyzer.ruleSuffix(i.rule, i.dotIndex))) {
        List<Item> result = calculateConsolidate(i, v);
        itemEntry.put(v, result);
      }
      $.put(i, itemEntry);
    }
    return $;
  }
  private static void addToPredictionTable(Map<NonTerminal, Map<Verb, DerivationRule>> $, Verb v, DerivationRule d) {
    DerivationRule result = $.get(d.lhs).put(v, d);
    if (result != null)
      throw new RuntimeException(
          "Not an LL grammar: predict[" + d.lhs + "," + v.name() + "] has two conflicting rules : <" + result + "> , <" + d + ">");
  }
  private Map<NonTerminal, Map<Verb, DerivationRule>> createLLPredictionTable() {
    Map<NonTerminal, Map<Verb, DerivationRule>> $ = new HashMap<>();
    for (Symbol nt : bnf.nonTerminals)
      $.put((NonTerminal) nt, new HashMap<>());
    for (DerivationRule d : bnf.rules()) {
      for (Verb v : analyzer.firstSetOf(d.getRHS()))
        addToPredictionTable($, v, d);
      if (analyzer.isNullable(d.getRHS()))
        for (Verb v : analyzer.followSetOf(d.lhs))
          addToPredictionTable($, v, d);
    }
    return $;
  }
  // NOTE not exactly as the algorithm specified in the paper--[or]
  private List<Item> calculateConsolidate(Item i, Verb v) {
    List<Item> $ = new ArrayList<>();
    Item current_i = i;
    Symbol Y = current_i.afterDot();
    while (!Y.isVerb()) {
      $.add(current_i);
      DerivationRule r = llPredict((NonTerminal) Y, v);
      if (r.size() == 0) { // r is an epsilon move?
        while ($.get($.size() - 1).advance().readyToReduce())
          $.remove($.size() - 1);
        $.add($.remove($.size() - 1).advance());
      } else
        $.add(new Item(r, 0));
      current_i = $.remove($.size() - 1);
      Y = current_i.afterDot();
    }
    $.add(current_i.advance());
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
  public List<Item> consolidate(Item i, Verb v) {
    final Map<Verb, List<Item>> row = consolidationTable.get(i);
    if (row == null)
      throw new IllegalStateException("consolidate( " + i + " , _ ) does not exist.");
    final List<Item> $ = row.get(v);
    if ($ == null)
      throw new IllegalStateException("consolidate( " + i + " , " + v + " ) does not exist.");
    return $;
  }
  public List<Item> jumps(Item i, Verb v) {
    final Map<Verb, List<Item>> row = jumpsTable.get(i);
    if (row == null)
      throw new IllegalStateException("jumps( " + i + " , _ ) does not exist.");
    final List<Item> value = row.get(v);
    if (value == null)
      throw new IllegalStateException("jumps( " + i + " , " + v + " ) does not exist.");
    return value;
  }
  public Set<Verb> legalJumps(Item i) {
    final Map<Verb, List<Item>> row = jumpsTable.get(i);
    if (row == null)
      throw new IllegalStateException("jumps( " + i + " , _ ) does not exist.");
    return row.keySet().stream().filter(key -> row.get(key) != RLLP.JUMP_ERROR).collect(Collectors.toSet());
  }
  public Set<Verb> illegalJumps(Item i) {
    final Map<Verb, List<Item>> row = jumpsTable.get(i);
    if (row == null)
      throw new IllegalStateException("jumps( " + i + " , _ ) does not exist.");
    return row.keySet().stream().filter(key -> row.get(key) == RLLP.JUMP_ERROR).collect(Collectors.toSet());
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
  public Collection<Item> getStartItems() {
    return items.stream().filter(i -> i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol) && i.dotIndex == 0)
        .collect(Collectors.toList());
  }
  public Item getStartItem(Verb initialInput) {
    return items.stream().filter(i -> bnf.startSymbols.contains(i.rule.lhs) && i.dotIndex == 0)
        .filter(i -> analyzer.firstSetOf(i.rule.getRHS()).contains(initialInput)).findAny().get();
  }

  public static abstract class Action {
    public static enum ActionType {
      ACCEPT, PUSH, JUMP, ADVANCE;
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
      public final List<Item> itemsToPush;
      public final Item i;
      public final Verb v;

      public Push(Item i, Verb v, List<Item> toPush) {
        this.i = i;
        this.v = v;
        this.itemsToPush = toPush;
      }
      @Override public String toString() {
        return "push( cons(" + i + "," + v + ")";
      }
      @Override public ActionType type() {
        return ActionType.PUSH;
      }
    }

    public static class Advance extends Action {
      public final Item beforeAdvancing;

      public Advance(Item beforeAdvancing) {
        this.beforeAdvancing = beforeAdvancing;
      }
      @Override public ActionType type() {
        return ActionType.ADVANCE;
      }
    }
  }

  @Override public String toString() {
    return "" //
        + "*** BNF ***\n" + bnf //
        + "\n*** Items ***\n" + items + "\n" //
        + "\n*** Consolidation Table ***\n" + consolidationTable + "\n" //
        + "\n*** Jumps Table ***\n" + jumpsTable + "\n" //
        + "\n*** LL Prediction Table ***\n" + llPredictionTable + "\n" //
        + "\n*** RLL Prediction Table ***\n" + rllPredictionTable;
  }
}
