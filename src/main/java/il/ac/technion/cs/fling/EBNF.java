package il.ac.technion.cs.fling;

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.sententials.Constants;
import il.ac.technion.cs.fling.internal.grammar.sententials.DerivationRule;
import il.ac.technion.cs.fling.internal.grammar.sententials.ExtendedSententialForm;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;

/**
 * An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>V ::= w X | Y z.</code>
 * 
 * @author Yossi Gil
 */
public class EBNF {
	/** Verbs collection */
	public final Set<Verb> Σ;
	/** Variables collection */
	public final Set<Variable> Γ;
	/** Start variable */
	public final Variable ε;
	/** Derivation rules collection */
	public final Set<DerivationRule> R;

	public EBNF(Set<Verb> Σ, Set<Variable> Γ, Variable ε, Set<DerivationRule> R) {
		this.Σ = Σ;
		this.Γ = Γ;
		this.ε = ε;
		this.R = R;
		Σ.add(Constants.$$);
	}

	/** @return all grammar symbols */
	public Stream<Symbol> symbols() {
		return Stream.concat(Σ.stream(), Γ.stream());
	}

	/**
	 * @param v a variable
	 * @return the right hand side of its derivation rule
	 */
	public List<ExtendedSententialForm> rhs(final Variable v) {
		return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
	}

	/**
	 * @param v a variable
	 * @return the right hand side of its derivation rule
	 */
	public Stream<ExtendedSententialForm> rhss(final Variable v) {
		return R.stream().filter(r -> r.lhs.equals(v)).map(DerivationRule::rhs).flatMap(Collection::stream);
	}
}
