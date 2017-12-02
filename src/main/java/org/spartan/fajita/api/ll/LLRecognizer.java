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
  private Stack<Symbol> stack = new Stack<>();
  private boolean initialized = false;
  private boolean accept = false;
  private boolean reject = false;

  public LLRecognizer(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf);
    actionTable = createActionTable();
  }
  public void consume(Verb t) {
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      stack.push(SpecialSymbols.$);
      stack.push(bnf.getStartSymbols().stream().filter(x -> analyzer.firstSetOf(x).contains(t)).findAny().get());
      initialized = true;
    }
    Symbol top = stack.pop();
    if (t.equals(SpecialSymbols.$)) {
      if (top.equals(SpecialSymbols.$))
        accept = true;
      else
        reject = true;
      return;
    }
    if (top.isVerb()) {
      if (top.equals(t))
        return;
      reject = true;
      return;
    }
    if (isError((NonTerminal) top, t)) {
      reject = true;
      return;
    }
    List<Symbol> toPush = getPush((NonTerminal) top, t);
    for (Symbol x : toPush)
      stack.push(x);
  }
  public List<Symbol> getPush(NonTerminal nt, Verb v) {
    return actionTable.get(nt).get(v);
  }
  public boolean isError(NonTerminal nt, Verb v) {
    return !actionTable.get(nt).containsKey(v);
  }
  public boolean accepted() {
    return accept;
  }
  public boolean rejected() {
    return reject;
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
}
