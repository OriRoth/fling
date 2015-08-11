package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
	@Override
	public default String toString2() {
		return "<"+name()+">";
	}
}