package org.spartan.fajita.api.examples;

import org.spartan.fajita.api.bnf.BNF;

public class BNFexample {

	public static void BnfAnalyzer(final BNF bnf) {
		System.out.println(bnf.toString());
	}

	public static void main(final String[] args) {
		BNF bnf = new BNF()
				.derive("S").to("A","B")
				.derive("S").to("A","S","B")
				.derive("A").to("(")
				.derive("B").to(")");
		BnfAnalyzer(bnf);
	}
}
