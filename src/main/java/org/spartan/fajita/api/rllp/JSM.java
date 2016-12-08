package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM {
  Deque<Item> S0;
  Deque<Map<Verb, JSM>> S1;
  private final Collection<Verb> verbs;
  private Map<Item, Map<Verb, Deque<Item>>> jumpsTable;
  private Hashtable<JSM.CompactConfiguration, JSM> configurationCache;
  private boolean readonly;

  public JSM(Collection<Verb> verbs, Map<Item, Map<Verb, Deque<Item>>> jumpsTable) {
    this.verbs = new ArrayList<>(verbs);
    this.jumpsTable = jumpsTable;
    this.verbs.remove(SpecialSymbols.$);
    this.readonly = false;
    S0 = new ArrayDeque<>();
    S1 = new ArrayDeque<>();
    this.configurationCache = new Hashtable<>();
  }
  private JSM(JSM fromJSM) {
    this(fromJSM.verbs, fromJSM.jumpsTable);
    fromJSM.S0.descendingIterator().forEachRemaining(i -> S0.addFirst(i));
    // TODO: Maybe... we have to deepcopy the JSMs inside the map too.
    // maybe they change or something..
    fromJSM.S1.descendingIterator().forEachRemaining(partMap -> S1.addFirst(partMap));
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
    return S0.getFirst();
  }
  /**
   * Pushes items to the JSM and makes it readonly afterwards
   * 
   * @param toPush
   */
  public void pushAll(Deque<Item> toPush) {
    final CompactConfiguration currentConfig = new CompactConfiguration(S0.peekFirst(), toPush);
    if (configurationCache.containsKey(currentConfig))
      load(configurationCache.get(currentConfig));
    else {
      configurationCache.put(currentConfig, this);
      toPush.descendingIterator().forEachRemaining(i -> push(i));
    }
    makeReadOnly();
  }
  /**
   * Pushes item i to S0 and coordinates S1 stack.
   * 
   * @param i
   */
  public void push(Item i) {
    if (readonly)
      throw new IllegalStateException("Can't push in readonly mode.");
    HashMap<Verb, JSM> partMap = new HashMap<>();
    for (Verb v : jumpsTable.get(i).keySet())
      // This is the push after jump
      partMap.put(v, calculateJumpConfiguration(jumpTable(i, v)));
    S0.addFirst(i);
    S1.addFirst(partMap);
  }
  private Deque<Item> jumpTable(Item i, Verb v) {
    return jumpsTable.get(i).get(v);
  }
  /**
   * Returns the state of the JSM after pushing all items in "toPush".
   */
  private JSM calculateJumpConfiguration(Deque<Item> toPush) {
    final JSM $ = deepCopy();
    $.pushAll(toPush);
    return $;
  }
  public void makeReadOnly() {
    this.readonly = true;
  }
  private JSM findJump(Verb v) {
    for (Map<Verb, JSM> partMap : S1)
      if (partMap.containsKey(v))
        return partMap.get(v);
    return null;
  }
  public Collection<SimpleEntry<Verb, JSM>> legalJumps() {
    return verbs.stream().map(v -> new SimpleEntry<>(v, findJump(v)))//
        .filter(e -> e.getValue() != null)//
        .collect(Collectors.toList());
  }
  /**
   * hashCode() and equals() functions use S0 field only. ("jumpsTable" and
   * "verbs" are only used to determine the same BNF is used). I believe that
   * the JSM is uniquely determined by S0, as S1 is internally handled inside
   * the implementation of "push()" according to the "jumps" table.
   */
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((S0 == null) ? 0 : S0.hashCode());
    result = prime * result + ((jumpsTable == null) ? 0 : jumpsTable.hashCode());
    result = prime * result + ((verbs == null) ? 0 : verbs.hashCode());
    return result;
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
    if (jumpsTable == null) {
      if (other.jumpsTable != null)
        return false;
    } else if (!jumpsTable.equals(other.jumpsTable))
      return false;
    if (verbs == null) {
      if (other.verbs != null)
        return false;
    } else if (!verbs.equals(other.verbs))
      return false;
    if (S0 == null) {
      if (other.S0 != null)
        return false;
    } else if (!S0.peekFirst().equals(other.S0.peekFirst()))
      return false;
    for (Verb v : verbs)
      if (findJump(v) != other.findJump(v))
        return false;
    return true;
  }
  @Override public String toString() {
    return "JSM.S0: " + S0.toString();
  }
  @Deprecated // This is used only in tests..
  public Item pop() {
    if (readonly)
      throw new IllegalStateException("Can't load in readonly mode.");
    S1.removeFirst();
    return S0.removeFirst();
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
    private Deque<Item> toPush;

    public CompactConfiguration(Item topOfStack, Deque<Item> toPush) {
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
        // For some reason Deque class does not implement equals and reference
        // semantics is used..
      } else if (!new ArrayList<>(toPush).equals(new ArrayList<>(other.toPush)))
        return false;
      return true;
    }
  }
}
