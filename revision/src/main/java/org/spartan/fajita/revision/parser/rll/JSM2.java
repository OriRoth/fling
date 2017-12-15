package org.spartan.fajita.revision.parser.rll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM2 implements Cloneable {
  public static final JSM2 JAMMED = null;
  private final RLLP rllp;
  private final Stack<Item> S0;
  private final Stack<Map<Verb, JSM2>> S1;
  private final Map<List<Item>, JSM2> cache;

  public JSM2(RLLP rllp) {
    this.rllp = rllp;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.cache = new LinkedHashMap<>();
    cache.put(this.S0, this);
  }
  private JSM2(JSM2 jsm) {
    this.rllp = jsm.rllp;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.cache = jsm.cache;
    this.S0.addAll(jsm.S0);
    for (Map<Verb, JSM2> m : jsm.S1)
      this.S1.add(new HashMap<>(m));
  }
  @Override public JSM2 clone() {
    return new JSM2(this);
  }
  public List<Item> getS0() {
    return new ArrayList<>(S0);
  }
  public Item peek() {
    return S0.peek();
  }
  public JSM2 pop() {
    JSM2 $ = clone();
    $.S0.pop();
    $.S1.pop();
    if (cache.containsKey($.getS0()))
      return cache.get($.getS0());
    cache.put($.getS0(), $);
    return $;
  }
  public JSM2 jump(Verb v) {
    return S1.peek().get(v);
  }
  public JSM2 pushAll(List<Item> items) {
    JSM2 $ = clone();
    $.S0.addAll(items);
    if (cache.containsKey($.getS0()))
      return cache.get($.getS0());
    cache.put($.getS0(), $);
    $.pushJumps(items);
    return $;
  }
  private void pushJumps(List<Item> items) {
    for (Item i : items)
      pushJumps(i);
  }
  // NOTE according to Jumps(i) algorithm in the paper
  private void pushJumps(Item i) {
    Map<Verb, JSM2> m = S1.isEmpty() ? emptyMap() : new HashMap<>(S1.peek());
    List<Symbol> suffix = i.rule.getRHSSuffix(i.dotIndex);
    Item currenti = i;
    Set<Verb> seen = new HashSet<>();
    for (int j = 1; j < suffix.size(); ++j) {
      if (!rllp.analyzer.isNullable(suffix.subList(1, j + 1)))
        break;
      currenti = currenti.advance();
      for (Verb v : rllp.analyzer.firstSetOf(suffix.get(j))) {
        if (seen.contains(v))
          continue;
        seen.add(v);
        JSM2 n = (S1.isEmpty() || S1.peek().get(v) == JAMMED ? cache.get(new Stack<>()) : S1.peek().get(v)).clone();
        List<Item> toPush = rllp.consolidate(currenti, v);
        n.S0.addAll(toPush);
        if (cache.containsKey(n.getS0()))
          m.put(v, cache.get(n.getS0()));
        else {
          cache.put(n.getS0(), n);
          n.pushJumps(toPush);
          m.put(v, n);
        }
      }
    }
    S1.push(m);
  }
  private Map<Verb, JSM2> emptyMap() {
    Map<Verb, JSM2> $ = new HashMap<>();
    for (Verb v : rllp.bnf.verbs)
      $.put(v, JAMMED);
    return $;
  }
  @Override public int hashCode() {
    // TODO Roth: set a proper hashCode method
    return 0;
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM2 && S0.equals(((JSM2) obj).S0);
  }
  @Override public String toString() {
    return toString(0, null);
  }
  private String toString(int ind, Verb v) {
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    if (v != null)
      $.append(v).append(":");
    $.append(super.toString()).append(" {\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S0: ").append(S0).append("\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S1: (").append(S1.size()).append(")\n");
    if (!S1.isEmpty())
      for (Verb x : rllp.bnf.verbs)
        if (S1.peek().get(x) != null) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": ").append(S1.peek().get(x).id()).append("\n");
        }
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    return $.append("}").toString();
  }
  private String id() {
    return super.toString();
  }
}
