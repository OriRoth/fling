package org.spartan.fajita.api.examples;

import org.spartan.fajita.api.bnf.BNF;

public class BNFexample {

	public static void BnfAnalyzer(final BNF bnf) {
		System.out.println(bnf.toString());
	}

	public static void main(final String[] args) {
		BNF bnf = new BNF()
				.nonterminal("S").derivesTo("A","B")
				.nonterminal("S").derivesTo("A","S","B")
				.nonterminal("A").derivesTo("(")
				.nonterminal("B").derivesTo(")");
		BnfAnalyzer(bnf);
	}
}
