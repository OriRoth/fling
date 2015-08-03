package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNF.func;

import org.spartan.fajita.api.bnf.BNF;

public class BNFexample {

	public static void BnfAnalyzer(final BNF bnf) {
		System.out.println(bnf.toString());
	}

	public static void main(final String[] args) {
		BNF bnf = new BNF()
				.derive("S").toOneOf("S_prime")
				.derive("S_prime").to("A","S_prime","B")
				.derive("A").to("(")
				.derive("B").to(")");
		BnfAnalyzer(bnf);
		
		System.out.println();
		System.out.println();
		
		BNF bnf2 = new BNF()
				.derive("S").to(func("foo").withParams("a","A"),func("bar").noParams())
				.derive("A").toOneOf(func("f2").noParams(),func("f3").withParams("b","B"))
				.derive("B").toOneOf("C","D")
				.derive("C").to("c")
				.derive("D").to("d");
		
		BnfAnalyzer(bnf2);
	}
}
