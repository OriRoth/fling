package org.spartan.fajita.api.ll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.ll.PredictionTable.Push;

public class LLParser {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  private final PredictionTable predictionTable;

  public LLParser(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf);
    predictionTable = createPredictionTable();
  }
  private PredictionTable createPredictionTable() {
    PredictionTable $ = new PredictionTable(bnf.getNonTerminals());
    for (DerivationRule d : bnf.getRules())
      for (Verb v : analyzer.firstSetOf(d.getChildren()))
        $.set(d.lhs, v, new Push(d.getChildren()));
    return $;
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
      if (predictionTable.isError((NonTerminal) top, v))
        return false;
      i--;
      List<Symbol> toPush = new ArrayList<>(predictionTable.get((NonTerminal) top, v).string);
      Collections.reverse(toPush);
      for (Symbol x : toPush)
        stack.push(x);
    }
    throw new IllegalStateException("Impossible to get here");
  }
  public boolean parse(Verb... verbs) {
    return parse(Arrays.asList(verbs));
  }
}
