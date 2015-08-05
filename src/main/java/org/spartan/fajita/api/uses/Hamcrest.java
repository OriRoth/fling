package org.spartan.fajita.api.uses;



import static org.spartan.fajita.api.examples.HamcrestBuilder.anything;
import static org.spartan.fajita.api.examples.HamcrestBuilder.assertThat;
import static org.spartan.fajita.api.examples.HamcrestBuilder.equal_to;
import static org.spartan.fajita.api.examples.HamcrestBuilder.instance_of;
import static org.spartan.fajita.api.examples.HamcrestBuilder.not;
import static org.spartan.fajita.api.uses.ASTViewer.showASTs;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Hamcrest {
	@SuppressWarnings("unchecked")
	public static void expressionBuilder(){
				
		// top down
		Compound e0 = assertThat("A", anything());
		
		/*for some reason i don't get type safety as expected*/
		Compound e1 = assertThat("A", instance_of(String.class));
		Compound e2 = assertThat(123, equal_to("S"));
		Compound e3 = assertThat("A", not().anything());
		Compound e4 = assertThat("A", not().not().not().equals_to("V"));
		
		// bottom up
		Compound e5 = assertThat(e1, not(equal_to("S")));
		Compound e6 = assertThat("A", not(not(equal_to("S"))));
		Compound e7 = assertThat("ASDAD", not().any_of(not().anything(),equal_to("AS")));
		showASTs(e0,e1,e2,e3,e4,e5,e6,e7);
	}
	
	public static void buildBNF() {
		BNF b = new BNF();

		// defining the nonterminals
		
		NonTerminal S = b.addNT("S"), 
				ASSERT = b.addNT("ASSERT"),
				MATCHER = b.addNT("MATCHER"),
				INSTANCEOF = b.addNT("INSTANCEOF"),
				ANYTHING = b.addNT("ANYTHING"),
				EQUALS_TO = b.addNT("EQUALS_TO"),
				NOT = b.addNT("NOT"),
				ANY_OF = b.addNT("ANY_OF"),
				MATCHERS = b.addNT("MATCHERS"),
				MATCHERS_OPT = b.addNT("MATCHERS_OPT");

		// defining the terminals
		Terminal assertThat = b.addTerm("assertThat"),
				instance_of = b.addTerm("instance_of"),
				anything = b.addTerm("anything"),
				not = b.addTerm("not"),
				equals_to = b.addTerm("equals_to"),
				any_of = b.addTerm("any_of"),
				value = b.addTerm("value"),
				type = b.addTerm("type");

		// define the rules
		b		.derive(S).toOneOf(ASSERT)
				.derive(ASSERT).to(assertThat,value,MATCHER)
				.derive(MATCHER).toOneOf(INSTANCEOF, ANYTHING, EQUALS_TO, NOT,ANY_OF)
				.derive(INSTANCEOF).to(instance_of,type)
				.derive(ANYTHING).to(anything)
				.derive(EQUALS_TO).to(equals_to, value)
				.derive(NOT).to(not, MATCHER)
				.derive(ANY_OF).to(any_of,MATCHERS)
				.derive(MATCHERS).to(MATCHER,MATCHERS_OPT)
				.derive(MATCHERS_OPT).toOneOf(MATCHERS,b.EPSILON)
				.finish();
		
		System.out.println(b);
	}
	
	public static void main(final String[] args) {
		buildBNF();
		expressionBuilder();
	}
}
