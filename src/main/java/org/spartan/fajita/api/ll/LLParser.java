package org.spartan.fajita.api.ll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class LLParser {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  private final Map<Symbol, Map<Verb, List<Symbol>>> table;

  public LLParser(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf);
    table = createPredictionTable();
  }
  private Map<Symbol, Map<Verb, List<Symbol>>> createPredictionTable() {
    Map<Symbol, Map<Verb, List<Symbol>>> $ = new HashMap<>();
    for (Symbol nt : bnf.getNonTerminals())
      $.put(nt, new HashMap<>());
    for (DerivationRule d : bnf.getRules())
      for (Verb v : analyzer.firstSetOf(d.getRHS())) {
        List<Symbol> result = $.get(d.lhs).put(v, d.getRHS());
        if (result != null)
          throw new NotLLGrammar(
              "nonterminal " + d.lhs + " has two rules with intersecting First set : <" + result + "> , <" + d.getRHS() + ">");
      }
    return $;
  }
  boolean isError(NonTerminal nt, Verb v) {
    return !table.get(nt).containsKey(v);
  }
  List<Symbol> get(NonTerminal nt, Verb v) {
    if (isError(nt, v))
      throw new IllegalStateException("M[" + nt + "," + v + "] not a push operaion!");
    return table.get(nt).get(v);
  }
  public boolean parse(List<Verb> input) {
    Stack<Symbol> stack = new Stack<>();
    stack.push(SpecialSymbols.$);
    stack.push(bnf.getStartSymbols().stream().findAny().get());
    input.add(SpecialSymbols.$);
    for (int i = 0; i < input.size(); i++) {
      Verb v = input.get(i);
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
      i--;
      List<Symbol> toPush = new ArrayList<>(get((NonTerminal) top, v));
      Collections.reverse(toPush);
      for (Symbol x : toPush)
        stack.push(x);
    }
    throw new IllegalStateException("Impossible to get here");
  }
  public boolean parse(Verb... verbs) {
    return parse(Arrays.asList(verbs));
  }

  public static class NotLLGrammar extends RuntimeException {
    private static final long serialVersionUID = -6362446023081095384L;

    public NotLLGrammar(String message) {
      super(message);
    }
  }
}
