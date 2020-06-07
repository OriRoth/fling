package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.GeneralizedSymbol;
import il.ac.technion.cs.fling.Variable;

public class DerivationRule {
	private static Stream<Variable> variables(Stream<GeneralizedSymbol> symbols) {
		return symbols.filter(GeneralizedSymbol::isVariable).map(GeneralizedSymbol::asVariable);
	}
	public final Variable lhs;

	public final List<ExtendedSententialForm> rhs;

	public DerivationRule(final Variable lhs, final List<ExtendedSententialForm> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DerivationRule))
			return false;
		final DerivationRule other = (DerivationRule) o;
		return lhs.equals(other.lhs) && rhs.equals(other.rhs);
	}

	@Override
	public int hashCode() {
		int $ = 1;
		$ = $ * 31 + lhs.hashCode();
		$ = $ * 31 + rhs.hashCode();
		return $;
	}

	public Variable lhs() {
		return lhs;
	}

	private Stream<GeneralizedSymbol> quantifiedSymbols() {
		return quantifiers().flatMap(Quantifier::symbols);
	}

	private Stream<Quantifier> quantifiers() {
		return symbols().filter(GeneralizedSymbol::isQuantifier).map(GeneralizedSymbol::asQuantifier);
	}


	public List<ExtendedSententialForm> rhs() {
		return rhs;
	}

	private Stream<GeneralizedSymbol> symbols() {
		return rhs.stream().flatMap(Collection::stream);
	}

	@Override
	public String toString() {
		return String.format("%s::=%s", lhs, String.join("|",
				rhs.stream().map(sf -> sf.isEmpty() ? "ε" : sf.toString()).collect(Collectors.toList())));
	}

	public Stream<Variable> variables() {
		return Stream.concat(variables(symbols()), variables(quantifiedSymbols()));
	}

	public Stream<Verb> verbs() {
		return Stream.concat(verbs(symbols()), verbs(quantifiedSymbols()));
	}
	private Stream<Verb> verbs(Stream<GeneralizedSymbol> symbols) {
		return symbols.filter(GeneralizedSymbol::isVerb).map(GeneralizedSymbol::asVerb);
	}
}
