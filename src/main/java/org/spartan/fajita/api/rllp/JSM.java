package org.spartan.fajita.api.rllp;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM {
  Deque<Item> S0;
  Deque<Map<Verb, JSM>> S1;
  private Collection<Verb> verbs;
  private Map<Item, Map<Verb, Deque<Item>>> jumpsTable;

  public JSM(Collection<Verb> verbs, Map<Item, Map<Verb, Deque<Item>>> jumpsTable) {
    this.verbs = verbs;
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
   * 
   * @param loadFrom
   *          the JSM the is loaded into ``this'' instance.
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
    HashMap<Verb, JSM> partMap = new HashMap<>();
    for (Verb v : jumpsTable.get(i).keySet()) {
      JSM copy = deepCopy();
      // This is the push after jump
      final Deque<Item> nextConsolidate = jumpsTable.get(i).get(v);
      nextConsolidate.descendingIterator()//
          .forEachRemaining(toPush -> copy.push(toPush));
      partMap.put(v, copy);
    }
    S1.addFirst(partMap);
    S0.addFirst(i);
  }
  public void jump(Verb v) {
    JSM dest = null;
    for (Map<Verb, JSM> partMap : S1)
      if (partMap.containsKey(v)) {
        dest = partMap.get(v);
        break;
      }
    if (dest == null)
      throw new IllegalStateException("The jump stack for verb " + v + " is empty!");
    load(dest);
  }
}
