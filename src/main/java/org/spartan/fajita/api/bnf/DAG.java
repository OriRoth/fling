package org.spartan.fajita.api.bnf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DAG<N> extends HashMap<N, Set<N>> {
  private static final long serialVersionUID = -5357744620287200470L;

  public Set<N> initialize(N node) {
    return putIfAbsent(node, new HashSet<>());
  }
  public boolean add(N node, N parent) {
    Set<N> reach = new HashSet<>(), seen = new HashSet<>();
    reach.add(parent);
    do {
      Set<N> current = new HashSet<>(reach);
      reach.clear();
      for (N n : current)
        if (n.equals(node))
          throw new IllegalArgumentException(circleDetectedMessage(node, parent));
        else if (!seen.contains(n) && containsKey(n))
          get(n).stream().filter(x -> !seen.contains(x)).forEach(x -> reach.add(x));
      seen.addAll(current);
    } while (!reach.isEmpty());
    return get(node).add(parent);
  }
  public String circleDetectedMessage(N node, N parent) {
    return "Addition of node " + node + " as the descendant of " + parent + " to the tree creates a circle";
  }
}
