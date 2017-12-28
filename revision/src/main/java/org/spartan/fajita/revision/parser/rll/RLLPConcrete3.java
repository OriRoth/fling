package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.parser.rll.JSM11.*;
import static org.spartan.fajita.revision.parser.rll.JSM11.J.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.JSM11.J;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

public class RLLPConcrete3 {
  protected final BNF bnf;
  protected JSM11 jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected Symbol startSymbol;
  private Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;
  private BNFAnalyzer analyzer;

  public RLLPConcrete3(BNF bnf) {
    this(bnf, new BNFAnalyzer(bnf));
  }
  public RLLPConcrete3(BNF bnf, BNFAnalyzer analyzer) {
    this(bnf, analyzer, new JSM11(bnf));
  }
  private RLLPConcrete3(BNF bnf, BNFAnalyzer analyzer, JSM11 jsm) {
    this.bnf = bnf;
    this.jsm = jsm;
    this.analyzer = analyzer;
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
    jsm = jsm.jump(v);
    if (jsm == JAMMED || jsm == UNKNOWN)
      reject = true;
  }
  Symbol pop() {
    Symbol $ = jsm.peek();
    jsm = jsm.pop();
    return $;
  }
  Symbol peek() {
    return jsm.peek();
  }
  void reduce(@SuppressWarnings("unused") Item i) {
    //
  }
  // TODO Roth: check whether reminder is needed
  private RLLPConcrete3 consume(Verb v) {
    if (jsm == null || initialized && jsm.isEmpty())
      reject = true;
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      push(bnf.startSymbols.stream().filter(s -> !isError(s.asNonTerminal(), v)).findAny().get());
      initialized = true;
    }
    Symbol top = peek();
    if (v.equals(SpecialSymbols.$)) {
      if (!top.equals(SpecialSymbols.$))
        reject = true;
      pop();
      return this;
    }
    if (top.isVerb()) {
      if (!top.equals(v))
        reject = true;
      pop();
      return this;
    }
    if (isError(top.asNonTerminal(), v)) {
      jump(v);
      return this;
    }
    pop();
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
  // TODO Roth: optimize action table creation in constructor
  // NOTE does not support all cases
  public static JSM11 next(JSM11 jsm, Verb v) {
    RLLPConcrete3 rllp = new RLLPConcrete3(jsm.bnf, jsm.analyzer, jsm);
    rllp.initialized = true;
    rllp.consume(v);
    return rllp.jsm;
  }
  // NOTE does not support all cases
  public static J nextj(JSM11 jsm, Verb v) {
    // TODO Roth: verify empty jsm support is not needed
    if (JAMMED == jsm || UNKNOWN == jsm || jsm.isEmpty())
      return JJAMMED;
    Symbol top = jsm.peek();
    if (v.equals(SpecialSymbols.$)) {
      if (!top.equals(SpecialSymbols.$))
        return JJAMMED;
      return J.of(jsm.pop());
    }
    if (top.isVerb()) {
      if (!top.equals(v))
        return JJAMMED;
      return J.of(jsm.pop());
    }
    // TODO Roth: optimize action table creation
    Map<NonTerminal, Map<Verb, List<Symbol>>> at = new RLLPConcrete3(jsm.bnf, jsm.analyzer).actionTable;
    if (!at.get(top.asNonTerminal()).containsKey(v))
      return jsm.jjump(v);
    return J.of(jsm.pop(), at.get(top.asNonTerminal()).get(v));
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
