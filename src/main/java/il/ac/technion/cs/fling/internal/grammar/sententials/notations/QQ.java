package il.ac.technion.cs.fling.internal.grammar.sententials.notations;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Objects;

import il.ac.technion.cs.fling.Symbol;
import il.ac.technion.cs.fling.internal.grammar.sententials.Quantifier;

public abstract class QQ implements Quantifier {
	public final Symbol symbol;

	public QQ(final Symbol symbol) {
		Objects.requireNonNull(symbol);
		assert !symbol.isQuantifier() : "nested notations are not supported";
		this.symbol = symbol;
	}

	@Override
	public final Symbol symbol() {
		return symbol;
	}

	@Override
	public final String name() {
		return symbol.name() + marker();
	}

	public final String toString() {
		return symbol + marker();
	}

	abstract String marker();

	public final Collection<Symbol> abbreviatedSymbols() {
		return asList(symbol);
	}
}
