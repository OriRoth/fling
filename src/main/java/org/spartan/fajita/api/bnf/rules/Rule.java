package org.spartan.fajita.api.bnf.rules;

import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public abstract class Rule<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal>
	implements Comparable<Rule<Term, NT>> {
    public final NT lhs;
    protected final Class<NT> ntClass;
    protected final Class<Term> termClass;
    private final int index;

    public Rule(final Class<Term> termClass, final Class<NT> ntClass, final NT lhs, final int index) {
	this.termClass = termClass;
	this.ntClass = ntClass;
	this.lhs = lhs;
	this.index = index;
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

	return lhs.equals(((Rule<?, ?>) obj).lhs);
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

    public int getIndex() {
	return index;
    }

    @Override
    public int compareTo(final Rule<Term, NT> other) {
	return Integer.compare(index, other.index);
    }
    
    public abstract List<Symbol> getChildren();
}