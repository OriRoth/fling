package org.spartan.fajita.api.parser;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class StateCalculator {

    public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> State<Term, NT> calculate(
	    final State<Term, NT> state, final Symbol lookahead) {
	return null;
    }

}
