package il.ac.technion.cs.fling.internal.util;

import java.util.Arrays;

public class Is {
  @SafeVarargs public static final <T> boolean included(final T t, final T... ts) {
    return Arrays.asList(ts).contains(t);
  }
}
