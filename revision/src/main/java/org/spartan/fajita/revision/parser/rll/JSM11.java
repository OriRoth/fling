package org.spartan.fajita.revision.parser.rll;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM11.J.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM11 implements Cloneable {
  public static final JSM11 JAMMED = new JSM11();
  public static final JSM11 UNKNOWN = new JSM11();
  final BNF bnf;
  final BNFAnalyzer analyzer;
  private final Stack<Symbol> S0;
  private final Stack<Map<Verb, J>> S1;
  private Set<Verb> baseLegalJumps;

  public JSM11(BNF bnf) {
    this(bnf, new BNFAnalyzer(bnf));
  }
  public JSM11(BNF bnf, BNFAnalyzer analyzer) {
    this.bnf = bnf;
    this.analyzer = analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.baseLegalJumps = null;
  }
  public JSM11(BNF bnf, BNFAnalyzer analyzer, Set<Verb> baseLegalJumps) {
    this(bnf, analyzer);
    this.baseLegalJumps = new LinkedHashSet<>(baseLegalJumps);
  }
  public JSM11(BNF bnf, BNFAnalyzer analyzer, Symbol initial, Set<Verb> baseLegalJumps) {
    this(bnf, analyzer);
    this.baseLegalJumps = new LinkedHashSet<>(baseLegalJumps);
    pushJumps(initial);
    S0.push(initial);
  }
  private JSM11(JSM11 jsm) {
    this.bnf = jsm.bnf;
    this.analyzer = jsm.analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.S0.addAll(jsm.S0);
    for (Map<Verb, J> m : jsm.S1)
      this.S1.add(new HashMap<>(m));
    this.baseLegalJumps = jsm.baseLegalJumps == null ? null : new LinkedHashSet<>(jsm.baseLegalJumps);
  }
  private JSM11() {
    this.bnf = null;
    this.analyzer = null;
    this.S0 = null;
    this.S1 = null;
    this.baseLegalJumps = null;
  }
  @Override public JSM11 clone() {
    return new JSM11(this);
  }
  public List<Symbol> getS0() {
    return new ArrayList<>(S0);
  }
  public Symbol peek() {
    return S0.peek();
  }
  public JSM11 pop() {
    JSM11 $ = clone();
    $.S0.pop();
    $.S1.pop();
    return $;
  }
  public JSM11 jump(Verb v) {
    return jjump(v).asJSM();
  }
  public J jjump(Verb v) {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    return !isEmpty() ? S1.peek().get(v) : baseLegalJumps.contains(v) ? JUNKNOWN : JJAMMED;
  }
  // TODO Roth: can be optimized
  public J jjumpFirstAvailable(Verb v) {
    if (isEmpty())
      return baseLegalJumps.contains(v) ? JUNKNOWN : JJAMMED;
    J jjump = jjump(v);
    return JJAMMED != jjump ? jjump : pop().jjumpFirstAvailable(v);
  }
  public JSM11 pushAll(List<Symbol> items) {
    if (items.isEmpty())
      return this;
    JSM11 $ = clone();
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
  // TODO Roth: can be optimized
  private Set<Verb> allLegalJumps() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    return new LinkedHashSet<>(bnf.verbs.stream().filter(v -> jump(v) != JAMMED).collect(toList()));
  }
  public Set<Verb> baseLegalJumps() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty() && baseLegalJumps != null;
    return new LinkedHashSet<>(baseLegalJumps);
  }
  // TODO Roth: can be optimized
  public JSM11 trim() {
    if (this == JAMMED || this == UNKNOWN || isEmpty())
      return this;
    return new JSM11(bnf, analyzer, allLegalJumps());
  }
  // TODO Roth: can be optimized
  public JSM11 trim1() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    return new JSM11(bnf, analyzer, S0.peek(), pop().allLegalJumps());
  }
  public int size() {
    assert S0.size() == S1.size();
    return S0.size();
  }
  private Map<Verb, J> emptyMap() {
    Map<Verb, J> $ = new HashMap<>();
    for (Verb v : bnf.verbs)
      $.put(v, baseLegalJumps != null && baseLegalJumps.contains(v) ? J.JUNKNOWN : J.JJAMMED);
    return $;
  }
  @Override public int hashCode() {
    return (S0 == null ? 1 : S0.hashCode()) * (S1 == null ? 1 : S1.hashCode());
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM11 && S0.equals(((JSM11) obj).S0) //
        && Objects.equals(baseLegalJumps, ((JSM11) obj).baseLegalJumps);
  }
  @Override public String toString() {
    return this == JAMMED ? "JAMMED"
        : this == UNKNOWN ? "UNKNOWN" //
            : toString(0, null, new HashSet<>(), new ArrayList<>());
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
    $.append(" S0: ").append(S0).append(" + ").append(toPush).append("\n");
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

  public static class J {
    public static final J JJAMMED = new J();
    public static final J JUNKNOWN = new J();
    // NOTE address is cloned
    public final JSM11 address;
    public final List<Symbol> toPush;
    private JSM11 asJSM;

    private J(JSM11 address, List<Symbol> toPush) {
      this.address = address;
      this.toPush = toPush;
    }
    private J() {
      address = null;
      toPush = null;
    }
    public static J of(JSM11 address, List<Symbol> toPush) {
      assert address != JAMMED && address != UNKNOWN;
      return new J(address, toPush);
    }
    public static J of(JSM11 address) {
      return address == JAMMED ? JJAMMED : address == UNKNOWN ? JUNKNOWN : of(address, new ArrayList<>());
    }
    public boolean isEmpty() {
      return address.isEmpty() && toPush.isEmpty();
    }
    public JSM11 asJSM() {
      return asJSM != null ? asJSM //
          : this == J.JJAMMED ? (asJSM = JAMMED) //
              : this == J.JUNKNOWN ? (asJSM = UNKNOWN) //
                  : (asJSM = address.pushAll(toPush));
    }
    public J trim() {
      return address.isEmpty() ? this : new J(address.trim(), toPush);
    }
    public J trim1() {
      assert !address.isEmpty();
      return new J(address.trim1(), toPush);
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
      return this == JJAMMED ? "JJAMMED"
          : this == JUNKNOWN ? "JUNKNOWN" //
              : address.toString(0, null, new HashSet<>(), toPush);
    }
  }
}
