package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class InheritenceRule extends Rule {
    public final List<NonTerminal> subtypes;

    public InheritenceRule(final NonTerminal lhs, final List<NonTerminal> nts, final int index) {
	super(lhs, index);
	subtypes = new ArrayList<>(nts);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(lhs.methodSignatureString() + " ::= ");
	for (NonTerminal nt : subtypes)
	    sb.append(nt.methodSignatureString() + " | ");
	sb.deleteCharAt(sb.length() - 2);
	return sb.toString();
    }

    @Override
    public List<Symbol> getChildren() {
	return new ArrayList<Symbol>(subtypes);
    }
}