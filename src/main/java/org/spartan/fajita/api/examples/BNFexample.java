package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.bnf.BNF.nt;
import static org.spartan.fajita.api.bnf.BNF.term;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;

public class BNFexample {

	public static void BnfAnalyzer(final BNF bnf) {
		System.out.println(bnf.toString());
	}

	public static void main(final String[] args) {
		NonTerminal s = nt("S");
		BNF bnf = new BNF().rule(s, nt("A"), nt("B"))
				.rule(s, nt("A"), s, nt("B"))
				.rule(nt("A"), term("("))
				.rule(nt("B"), term(")"));
		BnfAnalyzer(bnf);
	}
}
