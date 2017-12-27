package org.spartan.fajita.revision.parser.rll;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM10 implements Cloneable {
  public static final JSM10 JAMMED = new JSM10();
  public static final JSM10 UNKNOWN = new JSM10();
  final BNF bnf;
  final BNFAnalyzer analyzer;
  private final Stack<Symbol> S0;
  private final Stack<Map<Verb, J>> S1;
  private Set<Verb> baseLegalJumps;

  public JSM10(BNF bnf) {
    this(bnf, new BNFAnalyzer(bnf));
  }
  public JSM10(BNF bnf, BNFAnalyzer analyzer) {
    this.bnf = bnf;
    this.analyzer = analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.baseLegalJumps = null;
  }
  public JSM10(BNF bnf, BNFAnalyzer analyzer, Symbol initial, Set<Verb> emptyLegalJumps) {
    this(bnf, analyzer);
    this.baseLegalJumps = new LinkedHashSet<>(emptyLegalJumps);
    pushJumps(initial);
    S0.push(initial);
  }
  private JSM10(JSM10 jsm) {
    this.bnf = jsm.bnf;
    this.analyzer = jsm.analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.S0.addAll(jsm.S0);
    for (Map<Verb, J> m : jsm.S1)
      this.S1.add(new HashMap<>(m));
    this.baseLegalJumps = jsm.baseLegalJumps == null ? null : new LinkedHashSet<>(jsm.baseLegalJumps);
  }
  private JSM10() {
    this.bnf = null;
    this.analyzer = null;
    this.S0 = null;
    this.S1 = null;
    this.baseLegalJumps = null;
  }
  @Override public JSM10 clone() {
    return new JSM10(this);
  }
  public List<Symbol> getS0() {
    return new ArrayList<>(S0);
  }
  public Symbol peek() {
    return S0.peek();
  }
  public JSM10 pop() {
    JSM10 $ = clone();
    $.S0.pop();
    $.S1.pop();
    return $;
  }
  public JSM10 jump(Verb v) {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    J j = S1.peek().get(v);
    if (j == J.JJAMMED)
      return JAMMED;
    if (j == J.JUNKNOWN)
      return UNKNOWN;
    JSM10 $ = j.address;
    $ = $.pushAll(j.toPush);
    return $;
  }
  public JSM10 jumpFirstOption(Verb v) {
    if (isEmpty())
      return baseLegalJumps.contains(v) ? UNKNOWN : JAMMED;
    JSM10 jump = jump(v);
    return jump != JAMMED ? jump : pop().jumpFirstOption(v);
  }
  public JSM10 pushAll(List<Symbol> items) {
    if (items.isEmpty())
      return this;
    JSM10 $ = clone();
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
      for (Verb v : bnf.verbs)
        m.put(v, J.JJAMMED);
      m.put(s.asVerb(), J.of(clone()));
      S1.push(m);
      return;
    }
    NonTerminal nt = s.asNonTerminal();
    for (Verb v : bnf.verbs) {
      List<Symbol> c = analyzer.llClosure(nt, v);
      if (c == null) {
        if (!analyzer.followSetOf(nt).contains(v) || !analyzer.isNullable(nt))
          m.put(v, J.JJAMMED);
      } else {
        J j = J.of(clone(), c);
        m.put(v, j);
        List<Symbol> l = getS0();
        l.addAll(c);
      }
    }
    S1.push(m);
  }
  // TODO Roth: can be optimized
  public Set<Verb> peekLegalJumps() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    return new LinkedHashSet<>(
        bnf.verbs.stream().filter(v -> !analyzer.firstSetOf(S0.peek()).contains(v) && jump(v) != JAMMED).collect(toList()));
  }
  public Set<Verb> baseLegalJumps() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty() && baseLegalJumps != null;
    return new LinkedHashSet<>(baseLegalJumps);
  }
  public Set<Verb> allLegalJumps() {
    if (isEmpty())
      return baseLegalJumps();
    return pop().allLegalJumpsAfterPop();
  }
  private Set<Verb> allLegalJumpsAfterPop() {
    assert this != JAMMED && this != UNKNOWN;
    if (isEmpty())
      return baseLegalJumps();
    // TODO Roth: can be optimized
    Set<Verb> $ = new LinkedHashSet<>(bnf.verbs.stream().filter(v -> jump(v) != JAMMED).collect(toList()));
    $.addAll(pop().allLegalJumps());
    return $;
  }
  // TODO Roth: can be optimized
  public JSM10 trim() {
    if (this == JAMMED || this == UNKNOWN || isEmpty() || S0.size() == 1)
      return this;
    return new JSM10(bnf, analyzer, S0.peek(), allLegalJumps());
  }
  private Map<Verb, J> emptyMap() {
    Map<Verb, J> $ = new HashMap<>();
    for (Verb v : bnf.verbs)
      $.put(v, baseLegalJumps != null && baseLegalJumps.contains(v) ? J.JUNKNOWN : J.JJAMMED);
    return $;
  }
  @Override public int hashCode() {
    return S0 == null ? 1 : S0.hashCode();
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM10 && S0.equals(((JSM10) obj).S0);
  }
  @Override public String toString() {
    return this == JAMMED ? "JAMMED" : this == UNKNOWN ? "UNKNOWN" : toString(0, null, new HashSet<>(), new ArrayList<>());
  }
  String toString(int ind, Verb v, Set<J> seen, List<Symbol> toPush) {
    seen.add(J.of(this));
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    if (v != null)
      $.append(v).append(": ");
    if (this == JAMMED || this == UNKNOWN)
      return $.append(toString()).append(" + ").append(toPush).append("\n").toString();
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
        if (j == J.JJAMMED) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": JAMMED\n");
        } else if (j == J.JUNKNOWN) {
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
    if (baseLegalJumps != null) {
      for (int i = 0; i < ind; ++i)
        $.append(" ");
      $.append(" BLJ: ").append(baseLegalJumps).append("\n");
    }
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    return $.append("}\n").toString();
  }
  private String id() {
    return super.toString();
  }
  public boolean isEmpty() {
    return S0.isEmpty();
  }

  private static class J {
    static final J JJAMMED = new J();
    static final J JUNKNOWN = new J();
    final JSM10 address;
    final List<Symbol> toPush;

    J(JSM10 address, List<Symbol> toPush) {
      this.address = address;
      this.toPush = toPush;
    }
    J() {
      address = null;
      toPush = null;
    }
    static J of(JSM10 address, List<Symbol> toPush) {
      return new J(address, toPush);
    }
    static J of(JSM10 address) {
      return address == JAMMED ? JJAMMED : address == UNKNOWN ? JUNKNOWN : of(address, new ArrayList<>());
    }
    @Override public int hashCode() {
      int $ = 1;
      if (this == JJAMMED || this == JUNKNOWN)
        return $;
      $ += 31 * address.hashCode();
      $ += 17 * toPush.hashCode();
      return $;
    }
    @Override public boolean equals(Object obj) {
      return obj == JJAMMED ? this == JJAMMED
          : obj == JUNKNOWN ? this == JUNKNOWN
              : obj instanceof J && address.equals(((J) obj).address) && toPush.equals(((J) obj).toPush);
    }
    @Override public String toString() {
      return this == JJAMMED ? "JJAMMED" : this == JUNKNOWN ? "JUNKNOWN" : address.toString(0, null, new HashSet<>(), toPush);
    }
  }
}
