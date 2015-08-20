package org.spartan.fajita.api.parser;

import java.util.Set;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class State {
    public final Set<Item> items;
    public final BNF bnf;

    State(final Set<Item> items, final BNF bnf) {
	this.items = items;
	this.bnf = bnf;
    }

    public State goTo(final Symbol lookahead) {
	return StateCalculator.goTo(this, lookahead);
    }

    @Override
    public String toString() {
	String $ = "State: \n";
	for (Item item : items)
	    $ += item.toString();
	return $;
    }
}
