package fling.util;

import java.util.LinkedHashSet;
import java.util.Set;

public class Collections {
  @SafeVarargs public static <T> Set<T> set(T... items) {
    Set<T> $ = new LinkedHashSet<>();
    java.util.Collections.addAll($, items);
    return $;
  }
}
