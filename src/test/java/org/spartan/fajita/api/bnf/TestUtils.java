package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestUtils {
  @SafeVarargs static <T> Set<T> expectedSet(final T... expected) {
    return new HashSet<T>(Arrays.asList(expected));
  }
}
