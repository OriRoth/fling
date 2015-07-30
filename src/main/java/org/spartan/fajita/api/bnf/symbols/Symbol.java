package org.spartan.fajita.api.bnf.symbols;

public abstract class Symbol {
	public final String identifier;

	public Symbol(final String id) {
		identifier = id;
	}

	@Override
	public int hashCode() {
		return (int) Math.pow(identifier.hashCode(), 2) + (int) Math.pow(identifier.getClass().hashCode(), 3);
	}
}