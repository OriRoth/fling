package org.spartan.fajita.api.parser;

import static org.junit.Assert.*;
import static org.spartan.fajita.api.bnf.TestUtils.expectedItemSet;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class StateGotoTest {
    private enum Term implements Terminal {
	a, b, c, d;

	@Override
	public Type type() {
	    return Type.VOID;
	}
    };

    private enum NT implements NonTerminal {
	S, A;
    };

    @Test
    public void testEmptyNextState() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).and(Term.b) //
		.derive(NT.A).to(Term.a).and(Term.c) //
		.finish();

	State initialState = bnf.getInitialState();
	assertFalse(initialState.isLegalLookahead(Term.c));

	State nextState = initialState.goTo(Term.c);
	assertEquals(nextState.items, expectedItemSet());
    }

    @Test
    public void testNextStateTerminalLookahead() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).and(Term.b) //
		.derive(NT.A).to(Term.a).and(Term.c) //
		.finish();

	State initialState = bnf.getInitialState();
	assertTrue(initialState.isLegalLookahead(Term.a));

	State nextState = initialState.goTo(Term.a);
	Item A_Rule = nextState.items.stream().filter(r -> r.rule.lhs.equals(NT.A)).findAny().get();

	assertEquals(1, A_Rule.dotIndex);
	assertEquals(nextState.items, new HashSet<Item>(Arrays.asList(A_Rule)));
    }

    @Test
    public void testNextStateNonTerminalLookahead() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).and(Term.b) //
		.derive(NT.A).to(Term.a).and(Term.c) //
		.finish();

	State initialState = bnf.getInitialState();
	assertTrue(initialState.isLegalLookahead(NT.A));

	State nextState = initialState.goTo(NT.A);
	Item S_Rule = nextState.items.stream().filter(r -> r.rule.lhs.equals(NT.S)).findAny().get();

	assertEquals(1, S_Rule.dotIndex);
	assertEquals(nextState.items, new HashSet<Item>(Arrays.asList(S_Rule)));
    }

    @Test
    public void testUsesClosureInNextState() {
	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(Term.a).and(NT.A) //
		.derive(NT.A).to(Term.b).and(Term.c) //
		.finish();

	State initialState = bnf.getInitialState();

	State nextState = initialState.goTo(Term.a);

	assertEquals(2, nextState.items.size());
    }
}
