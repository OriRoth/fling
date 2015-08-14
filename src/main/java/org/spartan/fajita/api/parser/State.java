package org.spartan.fajita.api.parser;

import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class State<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    public final List<Item<Term, NT>> items;

    State(final List<Item<Term, NT>> items) {
	this.items = items;
    }

    public State<Term, NT> nextState(final Symbol lookahead) {
	return StateCalculator.calculate(this, lookahead);
    }
}
