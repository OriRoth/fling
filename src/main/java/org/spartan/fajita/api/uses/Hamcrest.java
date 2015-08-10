package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.examples.HamcrestBuilder.anything;
import static org.spartan.fajita.api.examples.HamcrestBuilder.assertThat;
import static org.spartan.fajita.api.examples.HamcrestBuilder.equal_to;
import static org.spartan.fajita.api.examples.HamcrestBuilder.instance_of;
import static org.spartan.fajita.api.examples.HamcrestBuilder.not;
import static org.spartan.fajita.api.uses.ASTViewer.showASTs;
import static org.spartan.fajita.api.uses.Hamcrest.NT.*;
import static org.spartan.fajita.api.uses.Hamcrest.Term.*;
import static org.spartan.fajita.api.uses.Hamcrest.Term.any_of;
import static org.spartan.fajita.api.uses.Hamcrest.Term.anything;
import static org.spartan.fajita.api.uses.Hamcrest.Term.assertThat;
import static org.spartan.fajita.api.uses.Hamcrest.Term.instance_of;
import static org.spartan.fajita.api.uses.Hamcrest.Term.not;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Hamcrest {
    @SuppressWarnings("unchecked")
    public static void expressionBuilder() {

	// top down
	Compound e0 = assertThat("A", anything());

	/* for some reason i don't get type safety as expected */
	Compound e1 = assertThat("A", instance_of(String.class));
	Compound e2 = assertThat(123, equal_to("S"));
	Compound e3 = assertThat("A", not().anything());
	Compound e4 = assertThat("A", not().not().not().equals_to("V"));

	// bottom up
	Compound e5 = assertThat(e1, not(equal_to("S")));
	Compound e6 = assertThat("A", not(not(equal_to("S"))));
	Compound e7 = assertThat("ASDAD", not().any_of(not().anything(), equal_to("AS")));
	showASTs(e0, e1, e2, e3, e4, e5, e6, e7);
    }

    public enum Term implements Terminal {
	assertThat, instance_of, anything, not, equals_to, any_of, value, type, epsilon;

	@Override
	public Class<?> type() {
	    return null;
	}
    }

    public static enum NT implements NonTerminal {
	ASSERT, MATCHER, INSTANCE_OF, ANYTHING, EQUAL_TO, NOT, ANY_OF, MATCHERS, MATCHERS_OPT, EPSILON;
    }

    public static void buildBNF() {
	BNF<Hamcrest.Term, Hamcrest.NT> b = new BNF<>(Term.class, NT.class);

	b //
		.derive(ASSERT).to(assertThat, value, MATCHER) //
		.derive(MATCHER).toOneOf(INSTANCE_OF, ANYTHING, EQUAL_TO, NOT, ANY_OF) //
		.derive(INSTANCE_OF).to(instance_of, type) //
		.derive(ANYTHING).to(anything) //
		.derive(EQUAL_TO).to(equals_to, value) //
		.derive(NOT).to(not, MATCHER) //
		.derive(ANY_OF).to(any_of, MATCHERS) //
		.derive(MATCHERS).to(MATCHER, MATCHERS_OPT) //
		.derive(MATCHERS_OPT).toOneOf(MATCHERS, EPSILON) //
		.derive(EPSILON).to(epsilon) //
		.finish();

	System.out.println(b.generateCode());
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }
}
