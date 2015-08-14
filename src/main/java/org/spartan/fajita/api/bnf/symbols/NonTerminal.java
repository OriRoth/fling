package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
    @Override
    public default String toString2() {
	return "<" + name() + ">";
    }

    public static final NonTerminal EPSILON = new NonTerminal() {
	@Override
	public String name() {
	    return "EPSILON";
	}

	@Override
	public String toString() {
	    return name();
	};
    };
}