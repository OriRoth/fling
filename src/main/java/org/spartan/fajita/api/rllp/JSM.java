package org.spartan.fajita.api.rllp;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM {
  Deque<Item> S0;
  Map<Verb, JSM> S1;
  private Collection<Verb> verbs;
  private Map<Item, Map<Verb, List<Item>>> jumpsTable;

  public JSM(Collection<Verb> verbs, Map<Item, Map<Verb, List<Item>>> jumpsTable) {
    this.verbs = verbs;
    this.jumpsTable = jumpsTable;
    S0 = new ArrayDeque<>();
    S1 = new HashMap<>();
    verbs.forEach(v -> S1.put(v, null));
  }
  public Item peek() {
    return S0.peek();
  }
  public void push(Item i) {
    HashMap<Verb, JSM> addToS1 = new HashMap<>();
    for (Verb v : jumpsTable.get(i).keySet()) {
      JSM snapshot = new JSM(this);
      for (Item toPush : jumpsTable.get(i).get(v))
        snapshot.push(toPush);
      addToS1.put(v, snapshot);
    }
    addToS1.forEach((v, jsm) -> S1.put(v, jsm));
    S0.push(i);
  }
  public void jump(Verb v) {
    JSM dest = S1.get(v);
    S1 = dest.S1;
    S0 = dest.S0;
  }
  private JSM(JSM snapshot) {
    this(snapshot.verbs, snapshot.jumpsTable);
    snapshot.S0.descendingIterator().forEachRemaining(i -> S0.addLast(i));
    for (Verb v : verbs) {
      S1.put(v, new JSM(snapshot.S1.get(v)));
    }
  }
}
