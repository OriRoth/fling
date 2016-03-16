package org.spartan.fajita.api.ll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class LLRecognizer {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  public final Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;

  public LLRecognizer(final BNF bnf) {
    this.bnf = bnf;
    sanitycheck();
    analyzer = new BNFAnalyzer(bnf);
    actionTable = createActionTable();
  }
  private void sanitycheck() {
    if (bnf.getRules().stream().anyMatch(d -> d.getChildren().isEmpty())) // epsilon
                                                                          // rule
      throw new IllegalArgumentException("I found epsilon rule! shame on you!!!");
  }
  private Map<NonTerminal, Map<Verb, List<Symbol>>> createActionTable() {
    Map<NonTerminal, Map<Verb, List<Symbol>>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.getNonTerminals()) {
      Map<Verb, List<Symbol>> innerMap = new HashMap<>();
      for (Verb v : bnf.getVerbs()) {
        List<Symbol> closure = analyzer.llClosure(nt, v);
        if (closure != null)
          innerMap.put(v, closure);
      }
      $.put(nt, innerMap);
    }
    return $;
  }

  public boolean recognize(List<Verb> input) {
    Stack<Symbol> stack = new Stack<>();
    stack.push(SpecialSymbols.$);
    stack.push(bnf.getStartSymbols().stream().findAny().get());
    input.add(SpecialSymbols.$);
    for (Verb v : input) {
      Symbol top = stack.pop();
      // Accept !
      if (v.equals(SpecialSymbols.$))
        return top.equals(SpecialSymbols.$);
      if (top.isVerb()) {
        if (top.equals(v))
          // Match !
          continue;
        // Reject !
        return false;
      }
      if (isError((NonTerminal) top, v))
        return false;
      List<Symbol> toPush = getPush((NonTerminal) top,v);
      for (Symbol x : toPush)
        stack.push(x);
    }
    throw new IllegalStateException("Impossible to get here");
  }
  public List<Symbol> getPush(NonTerminal nt, Verb v) {
    return actionTable.get(nt).get(v);
  }
  public boolean isError(NonTerminal nt, Verb v) {
    return ! actionTable.get(nt).containsKey(v);
  }
}
