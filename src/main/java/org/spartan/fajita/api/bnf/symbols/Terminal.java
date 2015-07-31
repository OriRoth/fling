package org.spartan.fajita.api.bnf.symbols;

public class Terminal extends Symbol {
	public Terminal(final String term) {
		super(term);
	}

	@Override
	public String toString() {
		return "\"" + identifier + "\"";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj.getClass() != NonTerminal.class)
			return false;
		return identifier.equals(((NonTerminal) obj).identifier);
	}

	public final static Terminal EPSILON = new Terminal("");
}