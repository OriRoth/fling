package fling.util;

import java.util.HashSet;
import java.util.Set;

public class Collections {
  @SafeVarargs public static <T> Set<T> set(T... items) {
    Set<T> $ = new HashSet<>();
    java.util.Collections.addAll($, items);
    return $;
  }
}
