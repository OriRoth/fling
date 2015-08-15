package org.spartan.fajita.api.bnf.symbols;

public interface Symbol {
    public String name();

    public String toString2();

    public default boolean isTerminal() {
	return Terminal.class.isAssignableFrom(getClass());
    }

    public default boolean isNonTerminal() {
	return NonTerminal.class.isAssignableFrom(getClass());
    }
}