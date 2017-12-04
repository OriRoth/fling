package org.spartan.fajita.revision.parser.ll;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.export.RuntimeVerb;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

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
  // AST creation does not work well
  @Deprecated public void consume(RuntimeVerb t) {
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      stack.push(terminal(SpecialSymbols.$));
      stack.push(bnf.startSymbols.stream().filter(x -> analyzer.firstSetOf(x).contains(t)).findAny().get());
      match.push(nonTerminal((NonTerminal) stack.peek()));
      initialized = true;
    }
    RuntimeNonTerminal mtop = match.peek();
    Stack<RuntimeNonTerminal> astToPush = new Stack<>();
    while (done(mtop)) {
      do {
        astToPush.push(mtop);
        match.pop();
        mtop = match.peek();
      } while (done(mtop));
      do {
        RuntimeNonTerminal p = astToPush.pop();
        mtop.interpret(p.nt, p.value);
      } while (!done(mtop) && !astToPush.isEmpty());
    }
    assert astToPush.isEmpty();
    Symbol top = stack.pop();
    if (t.equals(SpecialSymbols.$)) {
      if (top.equals(SpecialSymbols.$))
        accept = true;
      else
        reject = true;
      return;
    }
    if (top.isVerb()) {
      Verb v = (Verb) top;
      if (mtop.toConsume == -1 && analyzer.llClosure(mtop.nt, v) == null) {
        // Assume epsilon transition, no rejection
        mtop.toConsume = 0;
        consume(t);
        return;
      }
      if (t.equals(v)) {
        match.peek().interpret(v, t.values());
        return;
      }
      reject = true;
      return;
    }
    if (isError(top.asNonTerminal(), t)) {
      // Assume epsilon transition, no rejection
      consume(t);
      return;
    }
    assert mtop.toConsume == -1;
    mtop.interpret(t.terminal, t.values());
    List<Symbol> toPush = getPush(top.asNonTerminal(), t);
    mtop.toConsume = toPush.size();
    for (Symbol x : toPush) {
      stack.push(x);
      if (x.isNonTerminal())
        match.push(nonTerminal(x.asNonTerminal()));
    }
  }
  private static boolean done(RuntimeNonTerminal mtop) {
    return mtop.toConsume == 0;
  }
  public Object ast() {
    while (match.size() > 1) {
      RuntimeNonTerminal mtop = match.pop();
      match.peek().interpret(mtop.nt, mtop.value);
    }
    return match.get(0);
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

  interface RuntimeSymbol extends Symbol {
    //
  }

  public static class RuntimeTerminal implements RuntimeSymbol {
    final Terminal t;
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
      return "(" + t.name() + "->" + value + ")";
    }
  }

  public static class RuntimeNonTerminal implements RuntimeSymbol {
    final NonTerminal nt;
    final List<Interpretation> value;
    int toConsume = -1;

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
      assert toConsume != 0;
      if (toConsume > 0)
        --toConsume;
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
      return nt.name() + "[" + toConsume + "]" + "->" + value;
    }
  }

  public static class Interpretation extends AbstractMap.SimpleEntry<Symbol, Object> {
    private static final long serialVersionUID = 6948208130895284355L;

    public Interpretation(Symbol key, Object value) {
      super(key, value);
    }
    @Override public String toString() {
      return "(" + getKey().name() + "->"
          + (!getValue().getClass().isArray() ? getValue() : Arrays.deepToString((Object[]) getValue())) + ")";
    }
  }
}
