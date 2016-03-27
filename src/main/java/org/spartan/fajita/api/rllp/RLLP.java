package org.spartan.fajita.api.rllp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.ll.LLParser;

import com.javax0.fluflu.AddTo;

public class RLLP {
  public final BNF bnf;
  public final BNFAnalyzer analyzer;
  public final List<Item> items;
  private final Map<NonTerminal, Map<Verb, DerivationRule>> llPredictionTable;
  private Map<Item, Map<Verb, List<Item>>> consolidationTable;

  public RLLP(BNF bnf) {
    this.bnf = bnf;
    this.analyzer = new BNFAnalyzer(bnf, true);
    items = calculateItems();
    llPredictionTable = createPredictionTable();
    consolidationTable = calculateConsolidateTable();
  }
  private List<Item> calculateItems() {
    List<Item> $ = new ArrayList<>();
    for (DerivationRule r : bnf.getRules())
      for (int i = 0; i <= r.getChildren().size(); i++)
        $.add(new Item(r, i));
    return $;
  }
  private Map<Item, Map<Verb, List<Item>>> calculateConsolidateTable() {
    Map<Item, Map<Verb, List<Item>>> $ = new HashMap<>();
    for (Item i : items) {
      HashMap<Verb, List<Item>> itemEntry = new HashMap<>();
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
      throw new LLParser.NotLLGrammar("predict[" + d.lhs + ","+v.name()+"] has two conflicting rules : <" + result + "> , <" + d + ">");
  }
  private Map<NonTerminal, Map<Verb, DerivationRule>> createPredictionTable() {
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
  private List<Item> calculateConsolidate(Item i, Verb v) {
    Stack<Item> $ = new Stack<>();
    Item current_i = i;
    Symbol Y = current_i.afterDot();
    while (!Y.isVerb()) {
      $.push(current_i.advance());
      DerivationRule r = llPredict((NonTerminal) Y, v);
      if (r.getChildren().size() == 0) // r is an epsilon move?
        while ($.peek().readyToReduce())
          $.pop();
      else
        $.push(new Item(r, 0));
      current_i = $.pop();
      Y = current_i.afterDot();
    }
    $.push(current_i.advance());
    Stack<Item> reverse = new Stack<>();
    reverse.addAll($);
    return reverse;
  }
  public DerivationRule llPredict(NonTerminal Y, Verb v) {
    return llPredictionTable.get(Y).get(v);
  }
  public List<Item> consolidate(Item i, Verb v) {
    return consolidationTable.get(i).get(v);
  }
  public Item getStartItem(Verb initialInput) {
    final Stream<Item> filter2 = items.stream().filter(i -> bnf.getStartSymbols().contains(i.rule.lhs) && i.dotIndex == 0);
    final Stream<Item> filter = filter2 // get start rules
        .filter(i -> analyzer.firstSetOf(i.rule.getChildren()).contains(initialInput));
    return filter // get the correct rule using the initial input character
        .findAny().get();
  }
}
