package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM implements Iterable<SimpleEntry<Verb, JSM>> {
  Deque<Item> S0;
  Deque<Map<Verb, JSM>> S1;
  private Collection<Verb> verbs;
  private Map<Item, Map<Verb, Deque<Item>>> jumpsTable;

  public JSM(Collection<Verb> verbs, Map<Item, Map<Verb, Deque<Item>>> jumpsTable) {
    this.verbs = new ArrayDeque<>(verbs);
    verbs.remove(SpecialSymbols.$);
    this.jumpsTable = jumpsTable;
    S0 = new ArrayDeque<>();
    S1 = new ArrayDeque<>();
  }
  private JSM(JSM fromJSM) {
    this(fromJSM.verbs, fromJSM.jumpsTable);
    fromJSM.S0.descendingIterator().forEachRemaining(i -> S0.addFirst(i));
    fromJSM.S1.descendingIterator().forEachRemaining(partMap -> S1.addFirst(partMap));
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
  public void push(Item i) {
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
        .forEachRemaining((Item i)-> $.push(i));
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
  @Override public Iterator<SimpleEntry<Verb, JSM>> iterator() {
    return verbs.stream().map(v -> new SimpleEntry<>(v, findJump(v))).filter(e -> e.getValue() != null)//
        .collect(Collectors.toList()).iterator();
  }
}
