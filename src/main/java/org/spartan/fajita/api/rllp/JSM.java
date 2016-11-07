package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.JSM.Pair;

public class JSM implements Iterable<Pair<Verb, JSM>> {
  Deque<Item> S0;
  Deque<Map<Verb, JSM>> S1;
  private Collection<Verb> verbs;
  private Map<Item, Map<Verb, Deque<Item>>> jumpsTable;
  private Hashtable<Pair<Deque<Item>, Item>, JSM> alreadySeen;

  public JSM(Collection<Verb> verbs, Map<Item, Map<Verb, Deque<Item>>> jumpsTable) {
    this.verbs = new ArrayDeque<>(verbs);
    verbs.remove(SpecialSymbols.$);
    this.jumpsTable = jumpsTable;
    S0 = new ArrayDeque<>();
    S1 = new ArrayDeque<>();
    this.alreadySeen = new Hashtable<>();
  }
  private JSM(JSM fromJSM) {
    this(fromJSM.verbs, fromJSM.jumpsTable);
    fromJSM.S0.descendingIterator().forEachRemaining(i -> S0.addFirst(i));
    fromJSM.S1.descendingIterator().forEachRemaining(partMap -> S1.addFirst(partMap));
    alreadySeen = fromJSM.alreadySeen;
  }
  public JSM deepCopy() {
    return new JSM(this);
  }
  /**
   * @param loadFrom the JSM the is loaded into ``this'' instance.
   */
  private void load(JSM loadFrom) {
    S0 = loadFrom.S0;
    S1 = loadFrom.S1;
  }
  public Item peek() {
    return S0.getFirst();
  }
  public Item pop() {
    S1.removeFirst();
    return S0.removeFirst();
  }
  /**
   * Pushes item i to S0 and coordinates S1 stack.
   * 
   * @param i
   */
  public void push(Item i) {
    JSM existingJSM = alreadySeen.get(new Pair<>(S0, i));
    if (existingJSM != null) {
      System.out.println("Recursion found!!!!");
      load(existingJSM);
      return;
    }
    alreadySeen.put(new Pair<>(S0, i), this);
    S0.addFirst(i);
    HashMap<Verb, JSM> partMap = new HashMap<>();
    for (Verb v : jumpsTable.get(i).keySet()) {
      // This is the push after jump
      JSM nextConfiguration = nextConfiguration(jumpTable(i, v));
      partMap.put(v, nextConfiguration);
    }
    S1.addFirst(partMap);
  }
  private Deque<Item> jumpTable(Item i, Verb v) {
    return jumpsTable.get(i).get(v);
  }
  private JSM nextConfiguration(Deque<Item> deque) {
    final JSM $ = deepCopy();
    deque.descendingIterator()//
        .forEachRemaining((Item i) -> $.push(i));
    return $;
  }
  /**
   * Jumps to using v's jump stack, returning the result JSM.
   * 
   * @param v the jump stack used
   */
  public JSM dryJump(Verb v) {
    JSM dest = findJump(v);
    if (dest == null)
      throw new IllegalStateException("The jump stack for verb " + v + " is empty!");
    return dest;
  }
  private JSM findJump(Verb v) {
    JSM dest = null;
    for (Map<Verb, JSM> partMap : S1)
      if (partMap.containsKey(v)) {
        dest = partMap.get(v);
        break;
      }
    return dest;
  }
  /**
   * Jumps to using v's jump stack, changing the state of the JSM accordingly.
   * 
   * @param v the jump stack used
   */
  public void jump(Verb v) {
    load(dryJump(v));
  }
  @Override public Iterator<Pair<Verb, JSM>> iterator() {
    return verbs.stream().map(v -> new Pair<>(v, findJump(v))).filter(e -> e.getValue() != null)//
        .collect(Collectors.toList()).iterator();
  }
  /**
   * hashCode() and equals() functions use S0 field only. ("jumpsTable" and
   * "verbs" are only used to determine the same BNF is used). I believe that
   * the JSM is uniquely determined * by S0, as S1 is internally handled inside
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
    if (S0 == null) {
      if (other.S0 != null)
        return false;
    } else if (!S0.equals(other.S0))
      return false;
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
    return true;
  }
  @Override public String toString() {
    return "JSM " + S0.toString();
  }

  public static class Pair<K, V> extends SimpleEntry<K, V> {
    private static final long serialVersionUID = -5894898430181423583L;

    public Pair(K key, V value) {
      super(key, value);
    }
    @Override public String toString() {
      return "(" + getKey().toString() + "," + getValue().toString() + ")";
    }
  }
}
