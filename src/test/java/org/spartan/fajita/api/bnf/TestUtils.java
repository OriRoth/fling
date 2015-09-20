package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.parser.Item;

public class TestUtils {
  @SafeVarargs public static <T> Set<T> expectedSet(final T... expected) {
    return new HashSet<>(Arrays.asList(expected));
  }
  public static Set<Item> expectedItemSet(final DerivationRule... rules) {
    HashSet<Item> $ = new HashSet<>();
    for (DerivationRule derivationRule : rules)
      $.add(new Item(derivationRule, 0));
    return $;
  }
  public static DerivationRule findRule(final BNF bnf, final Predicate<? super DerivationRule> p) {
    return bnf.getRules().stream().filter(p).findAny().get();
  }
}
