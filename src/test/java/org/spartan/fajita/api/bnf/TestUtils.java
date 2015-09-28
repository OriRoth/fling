package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.spartan.fajita.api.bnf.rules.DerivationRule;

public class TestUtils {
  @SafeVarargs public static <T> Set<T> expectedSet(final T... expected) {
    return new HashSet<>(Arrays.asList(expected));
  }
  public static DerivationRule findRule(final BNF bnf, final Predicate<? super DerivationRule> p) {
    return bnf.getRules().stream().filter(p).findAny().get();
  }
}
