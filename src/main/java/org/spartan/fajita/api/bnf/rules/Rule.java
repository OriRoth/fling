package org.spartan.fajita.api.bnf.rules;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;

public abstract class Rule {
	public final NonTerminal lhs;

	public Rule(final NonTerminal lhs) {
		this.lhs = lhs;
	}

	/**
	 * Because we should only one derivation rule for each nonterminal, equals
	 * only checks lhs equality.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj.getClass() != getClass())
			return false;

		return lhs.equals(((Rule) obj).lhs);
	}

	/**
	 * Because we should only one derivation rule for each nonterminal we return
	 * lhs's hashcode.
	 * 
	 * @return lhs's hashcode.
	 */
	@Override
	public int hashCode() {
		return lhs.hashCode();
	}

	@Override
	public abstract String toString();
}