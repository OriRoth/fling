package fling.internal.util;

import java.util.*;

import fling.internal.grammar.sententials.Word;

public class Collections {
  @SafeVarargs public static <T> Set<T> set(final T... items) {
    final Set<T> $ = new LinkedHashSet<>();
    java.util.Collections.addAll($, items);
    return $;
  }
  public static <T> List<T> asList(final Collection<T> collection) {
    return java.util.Collections.unmodifiableList(new ArrayList<>(collection));
  }
  public static <T> Word<T> asWord(final Collection<T> collection) {
    return new Word<>(collection);
  }
  @SafeVarargs public static final <T> List<T> chainList(final Collection<T>... collections) {
    final List<T> list = new ArrayList<>();
    for (final Collection<T> collection : collections)
      list.addAll(collection);
    return java.util.Collections.unmodifiableList(list);
  }
  public static final <T> List<T> reversed(final T[] ts) {
    return reversed(Arrays.asList(ts));
  }
  public static final <T> List<T> reversed(final List<T> ts) {
    final List<T> $ = new ArrayList<>(ts);
    java.util.Collections.reverse($);
    return $;
  }
  public static final <T> Word<T> reversed(final Word<T> w) {
    final List<T> $ = new ArrayList<>(w);
    java.util.Collections.reverse($);
    return new Word<>($);
  }
  @SafeVarargs public static final <T> boolean included(final T t, final T... ts) {
    return Arrays.asList(ts).contains(t);
  }
}
