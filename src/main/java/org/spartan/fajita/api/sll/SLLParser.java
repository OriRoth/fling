package org.spartan.fajita.api.sll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class SLLParser {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  private final Map<Symbol, Map<Verb, DerivationRule>> predictionTable;

  public SLLParser(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf,true);
    predictionTable = createPredictionTable();
  }
  private Map<Symbol, Map<Verb, DerivationRule>> createPredictionTable() {
    Map<Symbol, Map<Verb, DerivationRule>> $ = new HashMap<>();
    for (Symbol nt : bnf.getNonTerminals())
      $.put(nt, new HashMap<>());
    for (DerivationRule d : bnf.getRules()) {
      List<Verb> verbs = analyzer.firstSetOf(d.getChildren());
      if (analyzer.isNullable(d.getChildren().toArray(new Symbol[] {})))
        verbs.addAll(analyzer.followSetOf(d.lhs));
      for (Verb v : verbs) {
        DerivationRule result = $.get(d.lhs).put(v, d);
        if (result != null)
          throw new NotSLLGrammar(
              "nonterminal " + d.lhs + " has two rules with intersecting First set : <" + result + "> , <" + d.getChildren() + ">");
      }
    }
    return $;
  }
  private boolean isError(NonTerminal nt, Verb v) {
    return !predictionTable.get(nt).containsKey(v);
  }
  DerivationRule predict(NonTerminal nt, Verb v) {
    if (isError(nt, v))
      throw new IllegalStateException("M[" + nt + "," + v + "] not a push operaion!");
    return predictionTable.get(nt).get(v);
  }
  // public boolean parse(List<Verb> input) {
  // Stack<Symbol> stack = new Stack<>();
  // stack.push(SpecialSymbols.$);
  // stack.push(bnf.getStartSymbols().stream().findAny().get());
  // input.add(SpecialSymbols.$);
  // for (int i = 0; i < input.size(); i++) {
  // Verb v = input.get(i);
  // Symbol top = stack.pop();
  // // Accept !
  // if (v.equals(SpecialSymbols.$))
  // return top.equals(SpecialSymbols.$);
  // if (top.isVerb()) {
  // if (top.equals(v))
  // // Match !
  // continue;
  // // Reject !
  // return false;
  // }
  // if (isError((NonTerminal) top, v))
  // return false;
  // i--;
  // List<Symbol> toPush = new ArrayList<>(predict((NonTerminal) top,
  // v).getChildren());
  // Collections.reverse(toPush);
  // for (Symbol x : toPush)
  // stack.push(x);
  // }
  // throw new IllegalStateException("Impossible to get here");
  // }
  // public boolean parse(Verb... verbs) {
  // return parse(Arrays.asList(verbs));
  // }
  public Stack<Item> closure(Item i, Verb t) {
    System.out.println("closure(" + i + "," + t + ")");
    Stack<Item> $ = new Stack<>();
    $.push(i);
    Symbol Y_i = i.afterDot();
    while (Y_i.isNonTerminal()) {
      DerivationRule prediction = predict((NonTerminal) Y_i, t);
      System.out.println(prediction);
      advance($);
      if (prediction.getChildren().isEmpty())
        while ($.peek().readyToReduce())
          $.pop();
      else
        $.push(new Item(prediction, 0));
      Y_i = $.peek().afterDot();
    }
    advance($);
    return $;
  }
  private static void advance(Stack<Item> s) {
    s.push(s.pop().advance());
  }

  public static class NotSLLGrammar extends RuntimeException {
    private static final long serialVersionUID = -6362446023081095384L;

    public NotSLLGrammar(String message) {
      super(message);
    }
  }
}
