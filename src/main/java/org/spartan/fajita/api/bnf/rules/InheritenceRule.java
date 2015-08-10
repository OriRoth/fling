package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class InheritenceRule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> extends Rule<Term,NT> {
	public final List<NT> subtypes;

	public InheritenceRule(final Class<Term> termClass,final Class<NT> ntClass,final NT lhs, final List<NT> subtypes, final int index) {
		super(termClass,ntClass,lhs,index);
		this.subtypes = new ArrayList<>(subtypes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + lhs.name()+ "> ::= ");
		for (NonTerminal nt : subtypes)
			sb.append("<"+nt.toString() + "> | ");
		sb.deleteCharAt(sb.length() - 2);
		return sb.toString();
	}
}