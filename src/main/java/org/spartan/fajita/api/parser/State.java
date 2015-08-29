package org.spartan.fajita.api.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class State {
    public final Set<Item> items;
    public final BNF bnf;
    public final Map<Symbol, Integer> transitions;
    public final int stateIndex;

    State(final Set<Item> items, final BNF bnf) {
	this(items, bnf, 0);
    }

    State(final Set<Item> items, final BNF bnf, final int stateIndex) {
	this.items = items;
	this.stateIndex = stateIndex;
	this.bnf = bnf;
	transitions = new HashMap<Symbol, Integer>();

    }

    void addGotoTransition(final Symbol symbol, final int stateIndex) {
	transitions.put(symbol, stateIndex);
    }

    public boolean isLegalLookahead(final Symbol lookahead) {
	if (lookahead == Terminal.$)
	    return items.stream()
		    .anyMatch(item -> item.readyToReduce() && bnf.getStartSymbols().contains(item.rule.lhs));
	return transitions.containsKey(lookahead) || items.stream().anyMatch(item -> item.isLegalLookahead(lookahead));
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

    @Override
    public int hashCode() {
	return items.hashCode() + bnf.hashCode();
    }
}
