package roth.ori.fling.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class DAG<N> extends LinkedHashMap<N, Set<N>> {
  private static final long serialVersionUID = -5357744620287200470L;

  public Set<N> initialize(N node) {
    return putIfAbsent(node, new LinkedHashSet<>());
  }
  @SuppressWarnings("null") public boolean add(N node, N parent) {
    Set<N> reach = new LinkedHashSet<>(), seen = new LinkedHashSet<>();
    reach.add(parent);
    do {
      Set<N> current = new LinkedHashSet<>(reach);
      reach.clear();
      for (N n : current)
        if (n.equals(node))
          throw new CircleDetected(node, parent);
        else if (!seen.contains(n) && containsKey(n))
          get(n).stream().filter(x -> !seen.contains(x)).forEach(x -> reach.add(x));
      seen.addAll(current);
    } while (!reach.isEmpty());
    return get(node).add(parent);
  }
  public DAG<N> reverse() {
    DAG<N> $ = new DAG<>();
    for (N n : keySet())
      for (N p : get(n)) {
        $.putIfAbsent(p, new LinkedHashSet<>());
        $.get(p).add(n);
      }
    return $;
  }

  public static class Tree<N> extends DAG<N> {
    private static final long serialVersionUID = -4612179429466418896L;

    @Override public boolean add(N node, N parent) {
      if (containsKey(node) && !get(node).isEmpty() && (get(node).size() > 1 || !get(node).contains(parent)))
        throw new MoreThanOneParent(node, parent);
      return super.add(node, parent);
    }
  }

  public static class CircleDetected extends IllegalArgumentException {
    private static final long serialVersionUID = -6758854252723486609L;

    public CircleDetected(Object node, Object parent) {
      super("Addition of node " + node + " as the descendant of " + parent + " to the tree creates a circle");
    }
  }

  public static class MoreThanOneParent extends IllegalArgumentException {
    private static final long serialVersionUID = 3465683203197637602L;

    public MoreThanOneParent(Object node, Object parent) {
      super("Addition of node " + node + " as the descendant of " + parent + " to the tree creates multiple inheritance");
    }
  }
}
