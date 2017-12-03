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
import org.spartan.fajita.api.export.FluentAPIRecorder;
import org.spartan.fajita.api.export.RuntimeVerb;

public class ELLRecognizer {
  public final BNF bnf;
  private final BNFAnalyzer analyzer;
  public final Map<NonTerminal, Map<Verb, List<Symbol>>> actionTable;
  private Stack<Symbol> stack = new Stack<>();
  private Stack<RuntimeNonTerminal> match = new Stack<>();
  private boolean initialized = false;
  private boolean accept = false;
  private boolean reject = false;
  private static final String PP_IDENT = "-";

  public ELLRecognizer(final BNF bnf) {
    this.bnf = bnf;
    analyzer = new BNFAnalyzer(bnf, true);
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
    if (isError(((NonTerminal) top), t)) {
      reject = true;
      return;
    }
    assert mtop.toConsume == -1;
    mtop.interpret(t.terminal, t.values());
    List<Symbol> toPush = getPush((NonTerminal) top, t);
    mtop.toConsume = toPush.size();
    for (Symbol x : toPush) {
      stack.push(x);
    }
    for (int i = toPush.size() - 1; i >= 0; --i)
      if (toPush.get(i).isNonTerminal())
        match.push(nonTerminal((NonTerminal) toPush.get(i)));
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

  public static String pp(Object o) {
    return pp(o, 0, false);
  }
  @SuppressWarnings({ "unchecked", "rawtypes" }) private static String pp(Object o, int t, boolean inner) {
    StringBuilder $ = new StringBuilder();
    if (o instanceof Interpretation) {
      for (int i = 0; i < t; ++i)
        $.append(PP_IDENT);
      Interpretation x = (Interpretation) o;
      $.append(x.getKey().name()).append("=").append(pp(x.getValue(), t + 1, inner));
    } else if (o instanceof List) {
      $.append(pp(((List) o).toArray(new Object[((List) o).size()]), t, inner));
    } else if (o instanceof Object[]) {
      Object[] x = (Object[]) o;
      if (x.length > 0) {
        // if (!inner)
        $.append("\n");
        for (int i = 0; i < x.length - 1; ++i) {
          $.append(pp(x[i], t + 1, inner));
          // if (!inner)
          $.append("\n");
        }
        $.append(pp(x[x.length - 1], t + 1, inner));
      }
    } else if (o instanceof RuntimeNonTerminal) {
      RuntimeNonTerminal x = ((RuntimeNonTerminal) o);
      $.append(x.nt.name()).append("->").append(pp(x.value, t, inner));
    } else if (o instanceof FluentAPIRecorder) {
      for (int j = 0; j < t; ++j)
        $.append(PP_IDENT);
      $.append(pp(((FluentAPIRecorder) o).ll.ast(), t, true));
    } else {
      for (int j = 0; j < t; ++j)
        $.append(PP_IDENT);
      $.append(o);
    }
    return $.toString();
  }
}
