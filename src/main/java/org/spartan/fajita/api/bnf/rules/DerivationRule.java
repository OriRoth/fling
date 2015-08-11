package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class DerivationRule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal>
	extends Rule<Term, NT> {

    public final List<Symbol> expression;

    public DerivationRule(final Class<Term> termClass, final Class<NT> ntClass, final NT lhs,
	    final List<Symbol> expression,final int index) {
	super(termClass, ntClass, lhs,index);
	for (Symbol s : expression)
	    if ((!ntClass.isAssignableFrom(s.getClass())) && (!termClass.isAssignableFrom(s.getClass())))
		throw new IllegalArgumentException("symbol " + s + " in expression is illegal");
	this.expression = new ArrayList<>(expression);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
	for (Symbol symb : expression)
	    if (ntClass.isAssignableFrom(symb.getClass()))
		sb.append(symb.toString2()+" ");
	    else
		sb.append(symb.toString2() + " ");
	return sb.toString();
    }
}