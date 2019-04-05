package fling.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fling.grammar.sententials.Word;

public class Collections {
  @SafeVarargs public static <T> Set<T> set(T... items) {
    Set<T> $ = new LinkedHashSet<>();
    java.util.Collections.addAll($, items);
    return $;
  }
  public static <T> List<T> asList(Collection<T> collection) {
    return java.util.Collections.unmodifiableList(new ArrayList<>(collection));
  }
  public static <T> Word<T> asWord(Collection<T> collection) {
    return new Word<>(collection);
  }
  @SafeVarargs public static final <T> List<T> chainList(Collection<T>... collections) {
    List<T> list = new ArrayList<>();
    for (Collection<T> collection : collections)
      list.addAll(collection);
    return java.util.Collections.unmodifiableList(list);
  }
  public static <T> List<T> reversed(T[] ts) {
    return reversed(Arrays.asList(ts));
  }
  public static <T> List<T> reversed(List<T> ts) {
    List<T> $ = new ArrayList<>(ts);
    java.util.Collections.reverse($);
    return $;
  }
  public static <T> Word<T> reversed(Word<T> w) {
    List<T> $ = new ArrayList<>(w);
    java.util.Collections.reverse($);
    return new Word<>($);
  }
  @SafeVarargs public static final <T> boolean included(T t, T... ts) {
    return Arrays.asList(ts).contains(t);
  }
}
