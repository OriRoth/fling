package roth.ori.fling.parser.rll;

import static java.util.stream.Collectors.toSet;
import static roth.ori.fling.parser.rll.JSM.J.JJAMMED;
import static roth.ori.fling.parser.rll.JSM.J.JUNKNOWN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import roth.ori.fling.bnf.BNF;
import roth.ori.fling.parser.ll.BNFAnalyzer;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Verb;

public class JSM implements Cloneable {
  public static final JSM JAMMED = new JSM();
  public static final JSM UNKNOWN = new JSM();
  public static final JSM ACCEPT = UNKNOWN;
  final BNF bnf;
  final BNFAnalyzer analyzer;
  private final Stack<Map<Verb, J>> stack;
  private Set<Verb> baseLegalJumps;

  private JSM(BNF bnf, BNFAnalyzer analyzer) {
    this.bnf = bnf;
    this.analyzer = analyzer;
    this.stack = new Stack<>();
    this.baseLegalJumps = new LinkedHashSet<>();
  }
  public JSM(BNF bnf, BNFAnalyzer analyzer, Set<Verb> baseLegalJumps) {
    this(bnf, analyzer);
    this.baseLegalJumps = new LinkedHashSet<>(baseLegalJumps);
  }
  public JSM(BNF bnf, BNFAnalyzer analyzer, GrammarElement initial, Set<Verb> baseLegalJumps) {
    this(bnf, analyzer);
    this.baseLegalJumps = new LinkedHashSet<>(baseLegalJumps);
    this.stack.addAll(clone().push(initial).stack);
  }
  private JSM(JSM jsm) {
    this.bnf = jsm.bnf;
    this.analyzer = jsm.analyzer;
    this.stack = new Stack<>();
    for (Map<Verb, J> m : jsm.stack)
      this.stack.add(new HashMap<>(m));
    this.baseLegalJumps = new LinkedHashSet<>(jsm.baseLegalJumps);
  }
  private JSM() {
    this.bnf = null;
    this.analyzer = null;
    this.stack = null;
    this.baseLegalJumps = null;
  }
  @Override public JSM clone() {
    return new JSM(this);
  }
  public static Set<Verb> initialBaseLegalJumps() {
    Set<Verb> $ = new LinkedHashSet<>();
    $.add(SpecialSymbols.$);
    return $;
  }
  private JSM push(GrammarElement symbol) {
    JSM $ = clone();
    Map<Verb, J> newDictionary = new LinkedHashMap<>();
    Function<Verb, J> jumpSolution = stack.isEmpty() ? v -> baseLegalJumps.contains(v) ? JUNKNOWN : JJAMMED
        : v -> stack.peek().get(v);
    for (Verb verb : bnf.verbs)
      if (analyzer.firstSetOf(symbol).contains(verb))
        newDictionary.put(verb, J.of(this, symbol.isVerb() ? new ArrayList<>() : analyzer.llClosure(symbol.asNonTerminal(), verb)));
      else if (!analyzer.isNullable(symbol))
        newDictionary.put(verb, JJAMMED);
      else
        newDictionary.put(verb, jumpSolution.apply(verb));
    $.stack.push(newDictionary);
    return $;
  }
  public JSM push(Collection<GrammarElement> symbols) {
    JSM $ = clone();
    for (GrammarElement symbol : symbols)
      $ = $.push(symbol);
    return $;
  }
  public JSM jump(Verb verb) {
    return jjump(verb).asJSM();
  }
  public J jjump(Verb verb) {
    assert this != JAMMED && this != UNKNOWN;
    return !isEmpty() ? stack.peek().get(verb) : baseLegalJumps.contains(verb) ? JUNKNOWN : JJAMMED;
  }
  public Set<Verb> baseLegalJumps() {
    assert this != JAMMED && this != UNKNOWN && baseLegalJumps != null;
    return new LinkedHashSet<>(baseLegalJumps);
  }
  public JSM trim() {
    if (this == JAMMED || this == UNKNOWN || isEmpty())
      return this;
    return new JSM(bnf, analyzer,
        stack.peek().keySet().stream().filter(verb -> stack.peek().get(verb) != JJAMMED).collect(toSet()));
  }
  // TODO Roth: generate hash code
  @Override public int hashCode() {
    return stack.hashCode();
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM && stack.equals(((JSM) obj).stack) //
        && Objects.equals(baseLegalJumps, ((JSM) obj).baseLegalJumps);
  }
  @Override public String toString() {
    return this == JAMMED ? "JAMMED"
        : this == UNKNOWN ? "UNKNOWN" //
            : toString(0, null, new HashSet<>(), new ArrayList<>());
  }
  String toString(int ind, Verb v, Set<J> seen, List<GrammarElement> toPush) {
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
    $.append(" stack: (").append(stack.size()).append(")\n");
    if (!stack.isEmpty())
      for (Verb x : bnf.verbs) {
        J j = stack.peek().get(x);
        if (j == J.JJAMMED) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": JAMMED\n");
        } else if (j == J.JUNKNOWN) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": UNKNOWN\n");
        } else {
          if (!seen.contains(j)) {
            seen.add(j);
            $.append(j.address.toString(ind + 2, x, seen, j.toPush));
          } else {
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
    return $.append("} + ").append(toPush).append("\n").toString();
  }
  private String id() {
    return super.toString();
  }
  public boolean isEmpty() {
    return stack.isEmpty();
  }

  public static class J {
    public static final J JJAMMED = new J();
    public static final J JUNKNOWN = new J();
    public static final J JACCEPT = JUNKNOWN;
    // NOTE address is cloned
    public final JSM address;
    public final List<GrammarElement> toPush;
    private JSM asJSM;

    private J(JSM address, List<GrammarElement> toPush) {
      this.address = address;
      this.toPush = toPush;
    }
    private J() {
      address = null;
      toPush = null;
    }
    public static J of(JSM address, List<GrammarElement> toPush) {
      assert address != JAMMED && address != UNKNOWN;
      return new J(address, toPush);
    }
    public static J of(JSM address) {
      return address == JAMMED ? JJAMMED : address == UNKNOWN ? JUNKNOWN : of(address, new ArrayList<>());
    }
    public boolean isEmpty() {
      return address.isEmpty() && toPush.isEmpty();
    }
    public JSM asJSM() {
      return asJSM != null ? asJSM //
          : this == J.JJAMMED ? (asJSM = JAMMED) //
              : this == J.JUNKNOWN ? (asJSM = UNKNOWN) //
                  : (asJSM = address.push(toPush));
    }
    public J trim() {
      return new J(address.trim(), toPush);
    }
    @SuppressWarnings("synthetic-access") public Set<Verb> baseLegalJumps() {
      return address.trim().baseLegalJumps;
    }
    @Override public int hashCode() {
      int $ = 1;
      if (this == JJAMMED || this == JUNKNOWN)
        return $;
      // $ += 31 * address.hashCode();
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
