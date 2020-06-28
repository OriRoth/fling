package il.ac.technion.cs.fling.internal.util;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
public class As {
  @SafeVarargs public static <T> Set<T> set(final T... items) {
    final Set<T> $ = new LinkedHashSet<>();
    Collections.addAll($, items);
    return $;
  }
  public static <T> Word<T> word(final Collection<T> collection) {
    return new Word<>(collection);
  }
  @SafeVarargs public static <T> List<T> list(final Collection<T>... collections) {
    final List<T> list = new ArrayList<>();
    for (final Collection<T> collection : collections)
      list.addAll(collection);
    return Collections.unmodifiableList(list);
  }
  public static <T> List<T> reversed(final T[] ts) {
    return reversed(Arrays.asList(ts));
  }
  public static <T> List<T> reversed(final List<T> ts) {
    final List<T> $ = new ArrayList<>(ts);
    Collections.reverse($);
    return $;
  }
  public static <T> Word<T> reversed(final Word<T> w) {
    final List<T> $ = new ArrayList<>(w);
    Collections.reverse($);
    return new Word<>($);
  }
}
