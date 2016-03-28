package org.spartan.fajita.api.rllp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spartan.fajita.api.bnf.symbols.Verb;

public class JSM {
  Deque<Item> S0;
  Map<Verb, Deque<JSM>> S1;
  private List<Verb> verbs;

  public JSM(List<Verb> verbs,jumpsTable) {
    this.verbs = verbs;
    S0 = new ArrayDeque<>();
    S1 = new HashMap<>();
    verbs.forEach(v -> S1.put(v, new ArrayDeque<>()));
  }
  private JSM(JSM snapshot) {
    this(snapshot.verbs);
    snapshot.S0.descendingIterator().forEachRemaining(i -> S0.addLast(i));
    for (Verb v : verbs) {
      Deque<JSM> currentStack = S1.get(v);
      snapshot.S1.get(v).descendingIterator().forEachRemaining(jsm -> currentStack.addLast(new JSM(jsm)));
    }
  }
}
