package org.spartan.fajita.api.bnf.rules;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class InheritenceRule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> extends Rule<Term,NT> {
	public final NT[] subtypes;

	@SafeVarargs
	public InheritenceRule(final Class<Term> termClass,final Class<NT> ntClass,final NT lhs, final NT ... subtypes) {
		super(termClass,ntClass,lhs);
		this.subtypes = subtypes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + lhs.name()+ "> ::= ");
		for (NonTerminal nt : subtypes)
			sb.append(nt.toString() + " | ");
		sb.deleteCharAt(sb.length() - 2);
		return sb.toString();
	}
}