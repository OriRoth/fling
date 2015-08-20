package org.spartan.fajita.api.bnf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.api.bnf.TestUtils.expectedSet;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class FollowSetTest {
    private enum Term implements Terminal {
	a, b, c, d;

	@Override
	public Type type() {
	    return Type.VOID;
	}
    };

    private enum NT implements NonTerminal {
	S, A, B, AB, C, NULLABLE, UNREACHABLE;
    };

    private BNF bnf;

    @Before
    public void initialize() {

	bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S) //
		.endConfig() //
		.derive(NT.S).to(NT.A).or(NT.B).or(NT.AB).or(NT.C) //
		.derive(NT.NULLABLE).to(NT.EPSILON) //
		.derive(NT.A).to(Term.a) //
		.derive(NT.B).to(Term.b)//
		.derive(NT.AB).to(NT.A).and(NT.B) //
		.derive(NT.C).to(Term.c).and(NT.NULLABLE).and(NT.NULLABLE) //
		.derive(NT.UNREACHABLE).to(Term.d) //
		.finish();
    }

    @Test
    public void testStartFollowedBy$() {
	assertThat(expectedSet(Term.$), equalTo(bnf.followSetOf(NT.S)));
    }

    @Test
    public void testMultipleStartsFollowedBy$() {

	BNF bnf = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("TEST") //
		.setStartSymbols(NT.S, NT.A) //
		.endConfig() //
		.derive(NT.S).to(NT.B).or(NT.AB) //
		.derive(NT.A).to(NT.C).or(NT.AB) //
		.derive(NT.NULLABLE).to(NT.EPSILON) //
		.derive(NT.B).to(Term.b)//
		.derive(NT.AB).to(NT.A).and(NT.B) //
		.derive(NT.C).to(Term.c).and(NT.NULLABLE).and(NT.NULLABLE) //
		.derive(NT.UNREACHABLE).to(Term.d) //
		.finish();

	assertThat(expectedSet(Term.$), equalTo(bnf.followSetOf(NT.S)));
	assertTrue(bnf.followSetOf(NT.A).contains(Term.$));
    }

    @Test
    public void testBasicFollow() {
	assertTrue(bnf.followSetOf(NT.A).contains(Term.b));
    }

    @Test
    public void testNTFolloweByNullableContainsLhsFollow() {
	assertTrue(bnf.followSetOf(NT.C).contains(Term.$));
    }

    @Test
    public void testEndOfExpressionContainsLhsFollow() {
	assertTrue(bnf.followSetOf(NT.A).contains(Term.$));
    }

    @Test
    public void testUnreachableNT() {
	assertEquals(expectedSet(), bnf.followSetOf(NT.UNREACHABLE));
    }

}
