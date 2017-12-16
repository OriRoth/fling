package org.spartan.fajita.revision.parser.rll;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

public class RLLPConcrete3 {
  protected final BNF bnf;
  protected JSM3 jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected Symbol startSymbol;
  private Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;
  private BNFAnalyzer analyzer;

  public RLLPConcrete3(BNF bnf) {
    this.bnf = bnf;
    this.jsm = new JSM3(bnf);
    this.analyzer = new BNFAnalyzer(bnf);
    this.actionTable = createActionTable();
    accept = false;
    reject = false;
    initialized = false;
  }
  void push(Symbol... items) {
    push(Arrays.asList(items));
  }
  void push(List<Symbol> items) {
    jsm = jsm.pushAll(items);
  }
  void jump(Verb v) {
    if (jsm.getS0().isEmpty())
      reject = true;
    else
      jsm = jsm.jump(v);
  }
  Symbol pop() {
    Symbol $ = jsm.peek();
    jsm = jsm.pop();
    return $;
  }
  void reduce(@SuppressWarnings("unused") Item i) {
    //
  }
  public RLLPConcrete3 consume(Verb v) {
    if (jsm == null)
      reject = true;
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      push(bnf.startSymbols.stream().filter(s -> !isError(s.asNonTerminal(), v)).findAny().get());
      initialized = true;
    }
    Symbol top = pop();
    if (v.equals(SpecialSymbols.$)) {
      if (!top.equals(SpecialSymbols.$))
        reject = true;
      return this;
    }
    if (top.isVerb()) {
      if (!top.equals(v))
        reject = true;
      return this;
    }
    if (isError(top.asNonTerminal(), v)) {
      jump(v);
      return this;
    }
    push(getPush(top.asNonTerminal(), v));
    return this;
  }
  public RLLPConcrete3 consume(Terminal t) {
    return consume(new Verb(t));
  }
  public RLLPConcrete3 consume(Verb... ts) {
    for (Verb t : ts)
      consume(t);
    return this;
  }
  public RLLPConcrete3 consume(Terminal... ts) {
    for (Terminal t : ts)
      try {
        consume(t);
      } catch (@SuppressWarnings("unused") RuntimeException e) {
        assert reject;
        break;
      }
    return this;
  }
  public boolean accepted() {
    if (reject || jsm == null)
      return false;
    if (accept)
      return true;
    return jsm.getS0().stream().allMatch(s -> analyzer.isNullable(s));
  }
  public boolean rejected() {
    return reject;
  }
  private List<Symbol> getPush(NonTerminal nt, Verb v) {
    return actionTable.get(nt).get(v);
  }
  private boolean isError(NonTerminal nt, Verb v) {
    return !actionTable.get(nt).containsKey(v);
  }
  private Map<NonTerminal, Map<Verb, List<Symbol>>> createActionTable() {
    Map<NonTerminal, Map<Verb, List<Symbol>>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.nonTerminals) {
      Map<Verb, List<Symbol>> innerMap = new HashMap<>();
      for (Verb v : bnf.verbs) {
        List<Symbol> closure = analyzer.llClosure(nt, v);
        if (closure != null)
          innerMap.put(v, closure);
      }
      $.put(nt, innerMap);
    }
    return $;
  }
}
