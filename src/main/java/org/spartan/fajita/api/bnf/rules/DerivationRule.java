package org.spartan.fajita.api.bnf.rules;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class DerivationRule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal>
	extends Rule<Term, NT> {

    public final Symbol[] expression;

    public DerivationRule(final Class<Term> termClass, final Class<NT> ntClass, final NT lhs,
	    final Symbol... expression) {
	super(termClass, ntClass, lhs);
	for (Symbol s : expression)
	    if ((!ntClass.isAssignableFrom(s.getClass())) && (!termClass.isAssignableFrom(s.getClass())))
		throw new IllegalArgumentException("symbol " + s + " in expression is illegal");
	this.expression = expression;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder("<" + lhs.name() + "> ::= ");
	for (Symbol symb : expression)
	    sb.append(symb.toString() + " ");
	return sb.toString();
    }
}