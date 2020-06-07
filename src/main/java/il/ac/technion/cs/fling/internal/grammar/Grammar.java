package il.ac.technion.cs.fling.internal.grammar;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.Named;
import il.ac.technion.cs.fling.GeneralizedSymbol;
import il.ac.technion.cs.fling.Terminal;
import il.ac.technion.cs.fling.Variable;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.grammar.sententials.DerivationRule;
import il.ac.technion.cs.fling.internal.grammar.sententials.Quantifier;
import il.ac.technion.cs.fling.internal.grammar.sententials.ExtendedSententialForm;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;
import il.ac.technion.cs.fling.internal.grammar.sententials.Word;

public abstract class Grammar {
	public final BNF ebnf;
	public final Namer namer;
	public final BNF bnf;
	public final BNF normalizedBNF;
	public final BNF normalizedEBNF;
	private final Map<Variable, BNF> subBNFs;

	public Grammar(BNF ebnf, Namer namer) {
		this.ebnf = ebnf;
		this.namer = namer;
		this.bnf = getBNF(ebnf);
		this.normalizedEBNF = normalize(ebnf,namer);
		this.normalizedBNF = getBNF(normalizedEBNF);
		subBNFs = new LinkedHashMap<>();
		for (Variable head : bnf.headVariables)
			subBNFs.put(head, computeSubBNF(head));
	}

	public abstract DPDA<Named, Verb, Named> buildAutomaton(BNF bnf);

	// TODO compute lazily.
	public DPDA<Named, Verb, Named> toDPDA() {
		return buildAutomaton(bnf);
	}

	private BNF getBNF(BNF ebnf) {
		Set<Variable> Γ = new LinkedHashSet<>(ebnf.Γ);
		Set<DerivationRule> R = new LinkedHashSet<>();
		Map<Variable, Quantifier> extensionHeadsMapping = new LinkedHashMap<>();
		Set<Variable> extensionProducts = new LinkedHashSet<>();
		for (DerivationRule rule : ebnf.R) {
			List<ExtendedSententialForm> rhs = new ArrayList<>();
			for (ExtendedSententialForm sf : rule.rhs) {
				List<GeneralizedSymbol> symbols = new ArrayList<>();
				for (GeneralizedSymbol symbol : sf) {
					if (!symbol.isQuantifier()) {
						symbols.add(symbol);
						continue;
					}
					Quantifier q = symbol.asQuantifier();
					Variable head = q.expand(namer, extensionProducts::add, R::add);
					extensionHeadsMapping.put(head, q);
					symbols.add(head);
				}
				rhs.add(new ExtendedSententialForm(symbols));
			}
			R.add(new DerivationRule(rule.lhs, rhs));
		}
		Γ.addAll(extensionProducts);
		return new BNF(ebnf.Σ, Γ, R, ebnf.ε, ebnf.headVariables, extensionHeadsMapping, extensionProducts,
				false);
	}

	public BNF getSubBNF(Variable variable) {
		return subBNFs.get(variable);
	}

	private BNF computeSubBNF(Variable v) {
		final Set<Verb> Σ = new LinkedHashSet<>();
		final Set<Variable> V = new LinkedHashSet<>();
		V.add(v);
		final Set<DerivationRule> rs = new LinkedHashSet<>();
		for (boolean more = true; more; ) {
			more = false;
			for (DerivationRule r : bnf.R)
				if (!rs.contains(r) && V.contains(r.lhs)) {
					more = true;
					rs.add(r);
					r.variables().forEachOrdered(V::add);
					r.verbs().forEachOrdered(Σ::add);
				}
		}
		return new BNF(Σ, V, rs, v, null, null, null, true);
	}

	private static BNF normalize(BNF bnf,Namer namer) {
		Set<Variable> V = new LinkedHashSet<>(bnf.Γ);
		Set<DerivationRule> R = new LinkedHashSet<>();
		for (Variable v : bnf.Γ) {
			List<ExtendedSententialForm> rhs = bnf.rhs(v);
			assert rhs.size() > 0;
			if (rhs.size() == 1) {
				// Sequence (or redundant alteration).
				R.add(new DerivationRule(v, rhs));
				continue;
			}
			List<Variable> alteration = new ArrayList<>();
			for (ExtendedSententialForm sf : rhs)
				if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
					// Ready alteration variable.
					alteration.add(sf.get(0).asVariable());
				else {
					// Create a suitable child variable.
					Variable a = namer.createASTChild(v);
					V.add(a);
					R.add(new DerivationRule(a, Collections.singletonList(sf)));
					alteration.add(a);
				}
			R.add(new DerivationRule(v, alteration.stream().map(a -> new ExtendedSententialForm(a)).collect(toList())));
		}
		return new BNF(bnf.Σ, V, R, bnf.ε, bnf.headVariables, bnf.extensionHeadsMapping,
				bnf.extensionProducts, false);
	}

	public static boolean isSequenceRHS(BNF bnf, Variable v) {
		List<ExtendedSententialForm> rhs = bnf.rhs(v);
		return rhs.size() == 1 && (rhs.get(0).size() != 1 || !bnf.isOriginalVariable(rhs.get(0).get(0)));
	}

	@SuppressWarnings({ "null", "unused" })
	public static DPDA<Named, Verb, Named> cast(DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
		return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
				dpda.Σ().map(Verb::new).collect(toSet()), //
				new LinkedHashSet<>(dpda.Γ), //
				dpda.δs.stream() //
						.map(δ -> new DPDA.δ<Named, Verb, Named>(δ.q, δ.σ == ε() ? ε() : new Verb(δ.σ), δ.γ, δ.q$,
								new Word<>(δ.getΑ().stream() //
										.map(Named.class::cast) //
										.collect(toList())))) //
						.collect(toSet()), //
				new LinkedHashSet<>(dpda.F), //
				dpda.q0, //
				Word.of(dpda.γ0.stream().map(Named.class::cast)));
	}
}
