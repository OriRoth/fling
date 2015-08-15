package org.spartan.fajita.api.bnf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.api.bnf.symbols.NonTerminal.EPSILON;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class NullablesTest {

    private enum Term implements Terminal {
	t1;
    };

    private enum NT implements NonTerminal {
	Nullable, Nullable2, NotNullable, A;
    };

    private BNF bnf;

    @Before
    public void initialize() {

	bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.A) //
		.endConfig() //
		.derive(NT.Nullable).to(EPSILON).or(NT.A) //
		.derive(NT.Nullable2).to(NT.Nullable).or(NT.A) //
		.derive(NT.NotNullable).to(Term.t1) //
		.derive(NT.A).to(Term.t1) //
		.finish();
    }

    @Test
    public void testEpsilonNullable() {
	assertTrue(bnf.isNullable(EPSILON));
    }

    @Test
    public void testDirectEpsilonInheritence() {
	assertTrue(bnf.isNullable(NT.Nullable));
    }

    @Test
    public void testTermNotNullable() {
	assertFalse(bnf.isNullable(Term.t1));
    }

    @Test
    public void testNTNotNullable() {
	assertFalse(bnf.isNullable(NT.A));
    }

    @Test
    public void testSecondDegreeInheritence() {
	assertTrue(bnf.isNullable(NT.Nullable2));
    }

    @Test
    public void testNullableExpression() {
	assertTrue(bnf.isNullable(NT.Nullable, NT.Nullable2));
    }

    @Test
    public void testNotNullableExpressionWithTerminal() {
	assertFalse(bnf.isNullable(NT.Nullable, NT.Nullable2, Term.t1));
    }

    @Test
    public void testNotNullableExpressionWithNonNullableNT() {
	assertFalse(bnf.isNullable(NT.Nullable, NT.Nullable2, NT.A));
    }

}
