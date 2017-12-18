package org.spartan.fajita.revision.parser.rll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM3 implements Cloneable {
  public static final J JAMMED = new J();
  public static final J UNKNOWN = new J();
  private final BNF bnf;
  private final BNFAnalyzer analyzer;
  private final Stack<Symbol> S0;
  private final Stack<Map<Verb, J>> S1;

  public JSM3(BNF bnf) {
    this.bnf = bnf;
    this.analyzer = new BNFAnalyzer(bnf);
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
  }
  private JSM3(JSM3 jsm) {
    this.bnf = jsm.bnf;
    this.analyzer = jsm.analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.S0.addAll(jsm.S0);
    for (Map<Verb, J> m : jsm.S1)
      this.S1.add(new HashMap<>(m));
  }
  @Override public JSM3 clone() {
    return new JSM3(this);
  }
  public List<Symbol> getS0() {
    return new ArrayList<>(S0);
  }
  public Symbol peek() {
    return S0.peek();
  }
  public JSM3 pop() {
    JSM3 $ = clone();
    $.S0.pop();
    $.S1.pop();
    return $;
  }
  public JSM3 jump(Verb v) {
    J j = S1.peek().get(v);
    JSM3 $ = j.address;
    $ = $.pushAll(j.toPush);
    return $;
  }
  public JSM3 pushAll(List<Symbol> items) {
    if (items.isEmpty())
      return clone();
    List<Symbol> l = getS0();
    l.addAll(items);
    JSM3 $ = clone();
    $.pushJumps(items);
    return $;
  }
  private void pushJumps(List<Symbol> items) {
    for (Symbol s : items) {
      pushJumps(s);
      S0.push(s);
    }
  }
  private void pushJumps(Symbol s) {
    Map<Verb, J> m = S1.isEmpty() ? emptyMap() : new HashMap<>(S1.peek());
    if (s.isVerb()) {
      S1.push(m);
      return;
    }
    NonTerminal nt = s.asNonTerminal();
    for (Verb v : bnf.verbs) {
      List<Symbol> c = analyzer.llClosure(nt, v);
      if (c == null) {
        if (!analyzer.followSetOf(nt).contains(v))
          m.put(v, JAMMED);
      } else {
        J j = J.of(clone(), c);
        m.put(v, j);
        List<Symbol> l = getS0();
        l.addAll(c);
      }
    }
    S1.push(m);
  }
  private Map<Verb, J> emptyMap() {
    Map<Verb, J> $ = new HashMap<>();
    for (Verb v : bnf.verbs)
      $.put(v, JAMMED);
    return $;
  }
  @Override public int hashCode() {
    return S0.hashCode();
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM3 && S0.equals(((JSM3) obj).S0);
  }
  @Override public String toString() {
    return toString(0, null, new HashSet<>(), new ArrayList<>());
  }
  String toString(int ind, Verb v, Set<J> seen, List<Symbol> toPush) {
    seen.add(J.of(this));
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    if (v != null)
      $.append(v).append(": ");
    $.append(super.toString()).append(" {\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S0: ").append(S0);
    if (!toPush.isEmpty())
      $.append(" + ").append(toPush);
    $.append("\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S1: (").append(S1.size()).append(")\n");
    if (!S1.isEmpty())
      for (Verb x : bnf.verbs) {
        J j = S1.peek().get(x);
        if (j == JAMMED) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": JAMMED\n");
        } else if (j == UNKNOWN) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": UNKNOWN\n");
        } else {
          if (!seen.contains(j))
            $.append(j.address.toString(ind + 2, x, seen, j.toPush));
          else {
            for (int i = 0; i < ind; ++i)
              $.append(" ");
            $.append("  ").append(x).append(": ").append(j.address.id()).append(" + ").append(j.toPush).append("\n");
          }
        }
      }
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    return $.append("}\n").toString();
  }
  private String id() {
    return super.toString();
  }

  private static class J {
    final JSM3 address;
    final List<Symbol> toPush;

    J(JSM3 address, List<Symbol> toPush) {
      this.address = address;
      this.toPush = toPush;
    }
    J() {
      address = null;
      toPush = null;
    }
    static J of(JSM3 address, List<Symbol> toPush) {
      return new J(address, toPush);
    }
    static J of(JSM3 address) {
      return of(address, new ArrayList<>());
    }
    @Override public int hashCode() {
      int $ = 1;
      $ += 31 * address.hashCode();
      $ += 17 * toPush.hashCode();
      return $;
    }
    @Override public boolean equals(Object obj) {
      return obj == JAMMED ? this == JAMMED
          : obj == UNKNOWN ? this == UNKNOWN
              : obj instanceof J && address.equals(((J) obj).address) && toPush.equals(((J) obj).toPush);
    }
    @Override public String toString() {
      return address.toString(0, null, new HashSet<>(), toPush);
    }
  }
}
