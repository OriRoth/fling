package org.spartan.fajita.revision.parser.rll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM2 implements Cloneable {
  public static final JSM2 JAMMED = null;
  private final RLLP rllp;
  private final Stack<Item> S0;
  private final Stack<Map<Verb, JSM2>> S1;
  private final Map<Stack<Item>, JSM2> cache;
  private boolean jammed;

  public JSM2(RLLP rllp) {
    this(rllp, new ArrayList<>());
  }
  public JSM2(RLLP rllp, List<Item> initialPush) {
    this.rllp = rllp;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.cache = new LinkedHashMap<>();
    this.jammed = false;
    _pushAll(initialPush);
    cache.put(this.S0, this);
  }
  private JSM2(JSM2 jsm) {
    this.rllp = jsm.rllp;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.cache = jsm.cache;
    this.jammed = false;
    for (Item i : jsm.S0)
      this.S0.push(i);
    for (Map<Verb, JSM2> m : jsm.S1)
      this.S1.push(new HashMap<>(m));
  }
  @Override public JSM2 clone() {
    return new JSM2(this);
  }
  public Collection<Item> getS0() {
    return new ArrayList<>(S0);
  }
  private Stack<Item> cloneS0() {
    Stack<Item> $ = new Stack<>();
    $.addAll(S0);
    return $;
  }
  public Item peek() {
    return S0.peek();
  }
  public JSM2 pop() {
    JSM2 $ = clone();
    $._pop();
    if (cache.containsKey($.S0))
      return cache.get($.S0);
    cache.put($.cloneS0(), $);
    return $;
  }
  public JSM2 pushAll(List<Item> items) {
    Stack<Item> x = new Stack<>();
    x.addAll(S0);
    x.addAll(items);
    if (cache.containsKey(x))
      return cache.get(x);
    JSM2 $ = clone();
    $._pushAll(items);
    return $;
  }
  public JSM2 jump(Verb v) {
    return S1.peek().get(v);
  }
  private Item _pop() {
    checkJammed();
    S1.pop();
    return S0.pop();
  }
  private void _pushAll(List<Item> items) {
    for (Item i : items)
      _pushS0(i);
    cache.put(cloneS0(), this);
    for (Item i : items)
      _pushS1(i);
  }
  private void _pushS0(Item i) {
    checkJammed();
    S0.push(i);
  }
  // NOTE according to Jumps(i) algorithm in the paper
  private void _pushS1(Item i) {
    checkJammed();
    Map<Verb, JSM2> m = S1.isEmpty() ? emptyMap() : new HashMap<>(S1.peek());
    List<Symbol> suffix = i.rule.getRHSSuffix(i.dotIndex);
    Item currenti = i;
    for (int j = 1; j < suffix.size(); ++j) {
      if (!rllp.analyzer.isNullable(suffix.subList(1, j + 1)))
        break;
      currenti = currenti.advance();
      for (Verb v : rllp.analyzer.firstSetOf(suffix.get(j))) {
        JSM2 n = S1.isEmpty() || S1.peek().get(v) == JAMMED ? cache.get(new Stack<>()) : S1.peek().get(v);
        List<Item> toPush = rllp.consolidate(currenti, v);
        List<Item> k = new ArrayList<>(n.S0);
        k.addAll(toPush);
        if (cache.containsKey(k))
          m.put(v, cache.get(k));
        else {
          n._pushAll(toPush);
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
  private void checkJammed() {
    if (jammed)
      throw new RuntimeException("JSM is jammed");
  }
  @Override public int hashCode() {
    // TODO Roth: set a proper hashCode method
    return 0;
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM2 && S0.equals(((JSM2) obj).S0);
  }
  @Override public String toString() {
    return "JSM: " + S0.toString();
  }
}
