package org.spartan.fajita.api.bnf.rules;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;

public class InheritenceRule extends Rule {
	public final NonTerminal[] subtypes;

	public InheritenceRule(final NonTerminal lhs, final NonTerminal... subtypes) {
		super(lhs);
		this.subtypes = subtypes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + lhs.identifier + "> ::= ");
		for (NonTerminal nt : subtypes)
			sb.append(nt.toString() + " | ");
		sb.deleteCharAt(sb.length() - 2);
		return sb.toString();
	}
}