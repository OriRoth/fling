package org.spartan.fajita.api.bnf.rules;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class DerivationRule extends Rule {

	public final Symbol[] expression;

	public DerivationRule(final NonTerminal lhs, final Symbol... expression) {
		super(lhs);
		this.expression = expression;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + lhs.identifier + "> ::= ");
		for (Symbol symb : expression)
			sb.append(symb.toString() + " ");
		return sb.toString();
	}
}