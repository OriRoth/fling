package org.spartan.fajita.api.bnf;

import static org.junit.Assert.*;
import static org.spartan.fajita.api.bnf.symbols.NonTerminal.EPSILON;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class FirstSetTest {

    private enum Term implements Terminal {
	a, b, c, d;
    };

    private enum NT implements NonTerminal {
	A, B, AB, C, D;
    };

    private enum NT_RECURSIVE implements NonTerminal {
	REC_1, REC_2;
    };

    private BNF<Term, NT> bnf;
    private BNF<Term, NT_RECURSIVE> recursive_bnf;

    @Before
    public void initialize() {

	bnf = new BNFBuilder<>(Term.class, NT.class) //
		.derive(NT.A).to(Term.a) //
		.derive(NT.B).to(Term.b) //
		.derive(NT.AB).to(NT.A).or(NT.B) //
		.derive(NT.C).to(NT.AB).or(EPSILON) //
		.derive(NT.D).to(Term.d) //
		.finish();

	recursive_bnf = new BNFBuilder<>(Term.class, NT_RECURSIVE.class) //
		.derive(NT_RECURSIVE.REC_1).to(NT_RECURSIVE.REC_2) //
		.derive(NT_RECURSIVE.REC_2).to(NT_RECURSIVE.REC_1) //
		.finish();
    }

    private static Set<Terminal> expectedSet(final Terminal... terminals) {
	return new HashSet<Terminal>(Arrays.asList(terminals));
    }

    @Test
    public void testEpsilon() {
	assertEquals(expectedSet(Terminal.epsilon), bnf.firstSetOf(NT.EPSILON));
    }

    @Test
    public void testTerminal() {
	assertEquals(expectedSet(Term.a), bnf.firstSetOf(Term.a));
    }

    @Test
    public void testNT() {
	assertEquals(expectedSet(Term.a), bnf.firstSetOf(NT.A));
    }

    @Test
    public void testInheritedNT() {
	assertEquals(expectedSet(Term.a, Term.b), bnf.firstSetOf(NT.AB));
    }

    @Test
    public void testNotNullableExpression() {
	assertFalse(bnf.firstSetOf(NT.EPSILON, NT.A).contains(Term.epsilon));
    }

    @Test
    public void testNullableExpression() {
	assertTrue(bnf.firstSetOf(NT.C, NT.EPSILON, Term.epsilon).contains(Term.epsilon));
    }

    @Test
    public void testExpressionWithNoNullables() {
	assertEquals(expectedSet(Term.a), bnf.firstSetOf(NT.A, NT.B));
    }

    @Test
    public void testExpressionWithNullables() {
	assertEquals(expectedSet(Term.d, Term.a, Term.b), bnf.firstSetOf(NT.C, NT.D));
    }

    @Test
    public void testRecursiveBNF() {
	recursive_bnf.firstSetOf(NT_RECURSIVE.REC_1);
	// no infinite recursion!
    }

    @Test
    public void testRedundantNT() {
	assertTrue(recursive_bnf.firstSetOf(NT_RECURSIVE.REC_2).isEmpty());
    }
}
