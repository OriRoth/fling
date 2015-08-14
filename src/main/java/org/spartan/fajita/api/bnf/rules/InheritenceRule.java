package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class InheritenceRule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal>
	extends Rule<Term, NT> {
    public final List<NonTerminal> subtypes;

    public InheritenceRule(final Class<Term> termClass, final Class<NT> ntClass, final NonTerminal lhs,
	    final List<NonTerminal> nts, final int index) {
	super(termClass, ntClass, lhs, index);
	this.subtypes = new ArrayList<>(nts);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
	for (NonTerminal nt : subtypes)
	    sb.append(nt.toString2() + " | ");
	sb.deleteCharAt(sb.length() - 2);
	return sb.toString();
    }

    @Override
    public List<Symbol> getChildren() {
	return new ArrayList<Symbol>(subtypes);
    }
}