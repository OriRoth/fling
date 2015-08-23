package org.spartan.fajita.api.parser;

import java.util.Set;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class State {
    public final Set<Item> items;
    public final BNF bnf;

    protected State(final Set<Item> items, final BNF bnf) {
	this.items = items;
	this.bnf = bnf;
    }

    public State goTo(final Symbol lookahead) {
	return StateCalculator.goTo(this, lookahead);
    }

    public boolean isLegalLookahead(final Symbol lookahead) {
	return items.stream().anyMatch(item -> item.isLegalLookahead(lookahead));
    }

    @Override
    public String toString() {
	String $ = "State: \n";
	for (Item item : items)
	    $ += item.toString();
	return $;
    }

    @Override
    public boolean equals(final Object obj) {
	if (obj.getClass() != State.class)
	    return false;
	State s = (State) obj;
	return bnf.equals(s.bnf) && s.items.equals(items);
    }
}
