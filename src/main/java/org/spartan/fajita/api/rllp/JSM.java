package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM {
  public static final JSM JAMMED = null;
  /**
   * If by any chance you change the type of java.util.Stack note that the below
   * code uses the faulty implementation of the iteration (FIFO instead of LIFO)
   */
  private Stack<Item> S0;
  private Stack<Map<Verb, JSM>> S1;
  private final Collection<Verb> verbs;
  private Hashtable<JSM.CompactConfiguration, JSM> configurationCache;
  private boolean readonly;
  private boolean recursive;
  private List<Item> recursiveAddition;
  private final RLLP rllp;
  private CompactConfiguration currentCompactConfig;

  public JSM(RLLP rllp) {
    this.rllp = rllp;
    this.verbs = new ArrayList<>(rllp.bnf.getVerbs());
    this.verbs.remove(SpecialSymbols.$);
    this.readonly = false;
    this.recursive = false;
    S0 = new Stack<>();
    S1 = new Stack<>();
    this.configurationCache = new Hashtable<>();
  }
  private JSM(JSM fromJSM) {
    this(fromJSM.rllp);
    fromJSM.S0.forEach(i -> S0.add(i));
    fromJSM.S1.forEach(partMap -> S1.add(partMap));
    configurationCache = fromJSM.configurationCache;
  }
  /**
   * Cannot cause recursion. always finite time.
   * 
   * @return
   */
  private JSM deepCopy() {
    return new JSM(this);
  }
  /**
   * @param loadFrom the JSM that is loaded into ``this'' instance.
   */
  private void load(JSM loadFrom) {
    if (readonly)
      throw new IllegalStateException("Can't load in readonly mode.");
    S0 = loadFrom.S0;
    S1 = loadFrom.S1;
  }
  public Item peek() {
    if (S0.isEmpty())
      return null;
    return S0.peek();
  }
  /**
   * Pushes items (in given order) to the JSM and makes it readonly afterwards
   * 
   * @param toPush
   */
  public void pushAll(List<Item> toPush) {
    if (readonly)
      throw new IllegalStateException("Can't push in readonly mode.");
    currentCompactConfig = new CompactConfiguration(this.peek(), toPush);
    configurationCache.put(currentCompactConfig, this);
    toPush.forEach(i -> push(i));
    makeReadOnly();
  }
  /**
   * Pushes item i to S0 and coordinates S1 stack.
   * 
   * @param i
   */
  private void push(Item i) {
    if (readonly)
      throw new IllegalStateException("Can't push in readonly mode.");
    HashMap<Verb, JSM> jumpInfo = new HashMap<>();
    for (Verb v : rllp.legalJumps(i))
      // This is the push after jump
      jumpInfo.put(v, calculateJumpConfiguration(rllp.jumps(i, v)));
    for (Verb v : rllp.illegalJumps(i))
      jumpInfo.put(v, JSM.JAMMED);
    S0.add(i);
    S1.add(jumpInfo);
  }
  /**
   * Returns the state of the JSM after pushing all items in "toPush" (in their
   * given order).
   */
  private JSM calculateJumpConfiguration(List<Item> toPush) {
    final CompactConfiguration nextConfig = new CompactConfiguration(this.peek(), toPush);
    if (configurationCache.containsKey(nextConfig)) {
      final JSM jsm = configurationCache.get(nextConfig);
      jsm.setRecursive(toPush);
      return jsm;
    }
    final JSM $ = deepCopy();
    $.pushAll(toPush);
    $.makeReadOnly();
    return $;
  }
  public List<Item> getS0() {
    return Collections.unmodifiableList(S0);
  }
  public void makeReadOnly() {
    this.readonly = true;
  }
  public boolean isRecursive() {
    return recursive;
  }
  private void setRecursive(List<Item> toPush) {
    recursive = true;
    recursiveAddition = new ArrayList<>(S0);
    recursiveAddition.addAll(toPush);
  }
  private JSM findJump(Verb v) {
    /**
     * We remove the first item in S1 because each item in S1 is relevant only
     * when there are other elements on top (meaning the matching item in S0 is
     * NOT the top of the stack). This makes sense since S1 is only used as jump
     * information for items that does not know what happen in the depth of the
     * stack. The top element of S1 is therefore not relevant (and causes error
     * because it override legal jumps).
     */
    List<Map<Verb, JSM>> reversed = new ArrayList<>(S1.subList(0, S1.size() - 1));
    Collections.reverse(reversed);
    for (Map<Verb, JSM> partMap : reversed)
      if (partMap.containsKey(v))
        return partMap.get(v);
    return null;
  }
  public Collection<SimpleEntry<Verb, JSM>> legalJumps() {
    return verbs.stream().map(v -> new SimpleEntry<>(v, findJump(v)))//
        .filter(e -> e.getValue() != null)//
        .collect(Collectors.toList());
  }
  @Override public int hashCode() {
    throw new UnsupportedOperationException(
        "Since JSM is highly recursive, and hashCode() and equals() cannot be consistent, hashCode() is not supported");
  }
  /**
   * @see JSM#hashCode()
   */
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JSM other = (JSM) obj;
    if (rllp != other.rllp)
      return false;
    if (S0 == null) {
      if (other.S0 != null)
        return false;
    }
    return currentCompactConfig.equals(other.currentCompactConfig);
  }
  @Override public String toString() {
    return "JSM.S0: " + S0.toString();
  }
  @Deprecated // This is used only in tests..
  public Item pop() {
    if (readonly)
      throw new IllegalStateException("Can't load in readonly mode.");
    S1.pop();
    return S0.pop();
  }
  /**
   * Jumps to using v's jump stack, changing the state of the JSM accordingly.
   * 
   * @param v the jump stack used
   */
  @Deprecated // This is used only in tests..
  public void jump(Verb v) {
    if (readonly)
      throw new IllegalStateException("Can't load in readonly mode.");
    JSM dest = findJump(v);
    if (dest == null)
      throw new IllegalStateException("The jump stack for verb " + v + " is empty!");
    load(dest);
  }

  public static class CompactConfiguration {
    private Item topOfStack;
    private List<Item> toPush;

    public CompactConfiguration(Item topOfStack, List<Item> toPush) {
      this.topOfStack = topOfStack;
      this.toPush = toPush;
    }
    @Override public String toString() {
      if (topOfStack == null)
        return "< empty stack ;;" + toPush.toString() + ">";
      return "<" + topOfStack.toString() + ";;" + toPush.toString() + ">";
    }
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((topOfStack == null) ? 0 : topOfStack.hashCode());
      result = prime * result + ((toPush == null) ? 0 : toPush.hashCode());
      return result;
    }
    @Override public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CompactConfiguration other = (CompactConfiguration) obj;
      if (topOfStack == null) {
        if (other.topOfStack != null)
          return false;
      } else if (!topOfStack.equals(other.topOfStack))
        return false;
      if (toPush == null) {
        if (other.toPush != null)
          return false;
      } else if (!toPush.equals(other.toPush))
        return false;
      return true;
    }
  }
}
