package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.parser.Item;

public class TestUtils {
    @SafeVarargs
    public static <T> Set<T> expectedSet(final T... expected) {
	return new HashSet<T>(Arrays.asList(expected));
    }

    public static Set<Item> expectedItemSet(final DerivationRule... rules) {
	HashSet<Item> $ = new HashSet<>();
	for (DerivationRule derivationRule : rules)
	    $.add(new Item(derivationRule, 0));
	return $;
    }
}
