package org.spartan.fajita.api.ll;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFAnalyzer;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.export.RuntimeVerb;

public class LLRecognizer {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  public final Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;
  private Stack<Symbol> stack = new Stack<>();
  private Stack<RuntimeNonTerminal> match = new Stack<>();
  private boolean initialized = false;
  private boolean accept = false;
  private boolean reject = false;

  public LLRecognizer(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf);
    actionTable = createActionTable();
  }
  public void consume(RuntimeVerb t) {
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      stack.push(terminal(SpecialSymbols.$));
      stack.push(bnf.getStartSymbols().stream().filter(x -> analyzer.firstSetOf(x).contains(t)).findAny().get());
      match.push(nonTerminal(SpecialSymbols.augmentedStartSymbol));
      match.push(nonTerminal((NonTerminal) stack.peek()));
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
      if (t.equals(top)) {
        match.peek().interpret(top, t.values());
        return;
      }
      reject = true;
      return;
    }
    if (isError(((NonTerminal) top), t)) {
      reject = true;
      return;
    }
    RuntimeNonTerminal mtop = match.peek();
    mtop.interpret(t.terminal, t.values());
    while (mtop.value.size() == actionTable.get(mtop.nt).size() && !SpecialSymbols.augmentedStartSymbol.equals(mtop.nt)) {
      match.pop();
      match.peek().interpret(mtop.nt, mtop.value);
      mtop = match.peek();
    }
    List<Symbol> toPush = getPush((NonTerminal) top, t);
    for (Symbol x : toPush) {
      stack.push(x);
      if (x.isNonTerminal())
        match.push(nonTerminal((NonTerminal) x));
    }
  }
  public Object ast() {
    return match.get(0).value;
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
  public static RuntimeTerminal terminal(Terminal t) {
    return new RuntimeTerminal(t);
  }
  public static RuntimeNonTerminal nonTerminal(NonTerminal nt) {
    return new RuntimeNonTerminal(nt);
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

  interface RuntimeSymbol extends Symbol {
    //
  }

  public static class RuntimeTerminal implements RuntimeSymbol {
    Terminal t;
    Interpretation value;

    public RuntimeTerminal(Terminal t) {
      this.t = t;
    }
    @Override public boolean isNonTerminal() {
      return false;
    }
    @Override public boolean isVerb() {
      return true;
    }
    public void interpret(@SuppressWarnings("hiding") Object value) {
      this.value = new Interpretation(t, value);
    }
    @Override public String name() {
      return t.name();
    }
    @Override public int hashCode() {
      return t.hashCode();
    }
    @Override public boolean equals(Object obj) {
      return obj instanceof RuntimeTerminal ? t.equals(((RuntimeTerminal) obj).t) : t.equals(obj);
    }
    @Override public String toString() {
      return t.toString();
    }
  }

  public static class RuntimeNonTerminal implements RuntimeSymbol {
    NonTerminal nt;
    List<Interpretation> value;

    public RuntimeNonTerminal(NonTerminal nt) {
      this.nt = nt;
      value = new LinkedList<>();
    }
    @Override public boolean isNonTerminal() {
      return true;
    }
    @Override public boolean isVerb() {
      return false;
    }
    public void interpret(Symbol s, @SuppressWarnings("hiding") Object value) {
      this.value.add(new Interpretation(s, value));
    }
    @Override public String name() {
      return nt.name();
    }
    @Override public int hashCode() {
      return nt.hashCode();
    }
    @Override public boolean equals(Object obj) {
      return obj instanceof RuntimeNonTerminal ? nt.equals(((RuntimeNonTerminal) obj).nt) : nt.equals(obj);
    }
    @Override public String toString() {
      return nt.toString();
    }
  }

  public static class Interpretation extends AbstractMap.SimpleEntry<Symbol, Object> {
    private static final long serialVersionUID = 6948208130895284355L;

    public Interpretation(Symbol key, Object value) {
      super(key, value);
    }
    @Override public String toString() {
      return "(" + getKey().name() + "->" + (!getValue().getClass().isArray() ? getValue() : Arrays.deepToString((Object[]) getValue()))
          + ")";
    }
  }
}
