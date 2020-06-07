package il.ac.technion.cs.fling;

import static il.ac.technion.cs.fling.internal.grammar.sententials.Constants.intermediateVariableName;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;

import org.antlr.runtime.tree.Tree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.*;

import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;
import il.ac.technion.cs.fling.internal.grammar.types.TypeParameter;
import il.ac.technion.cs.fling.internal.util.Counter;

/**
 * An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>V ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 * 
 * @author Ori Roth
 */
public class FancyEBNF extends EBNF {
	/** Set of nullable variables and notations */
	public final Set<Symbol> nullables;
	/** Maps variables and notations to their firsts set */
	public final Map<Symbol, Set<Verb>> firsts;
	/** Maps variables and notations to their follows set */
	public final Map<Variable, Set<Verb>> follows;
	/** Head variables set, containing variables used as API parameters */
	public final Set<Variable> headVariables;
	/** Maps generated variables to the notation originated them. Optional */
	public final Map<Variable, Quantifier> extensionHeadsMapping;
	/** Set of generated variables */
	public final Set<Variable> extensionProducts;

	public FancyEBNF(final Set<Verb> Σ, final Set<Variable> Γ, final Set<DerivationRule> R,
			final Variable startVariable, final Set<Variable> headVariables,
			final Map<Variable, Quantifier> extensionHeadsMapping, final Set<Variable> extensionProducts,
			final boolean addStartSymbolDerivationRules) {
		super(Σ, Γ, startVariable, R);
		if (addStartSymbolDerivationRules) {
			this.Γ.add(Constants.S);
			R.add(new DerivationRule(Constants.S, new ArrayList<>()));
			rhs(Constants.S).add(new ExtendedSententialForm(startVariable));
		}
		this.headVariables = headVariables;
		this.extensionHeadsMapping = extensionHeadsMapping == null ? Collections.emptyMap() : extensionHeadsMapping;
		this.extensionProducts = extensionProducts == null ? Collections.emptySet() : extensionProducts;
		this.nullables = getNullables();
		this.firsts = getFirsts();
		this.follows = getFollows();
	}

	/**
	 * @param symbols sequence of grammar symbols
	 * @return whether the sequence is nullable
	 */
	public boolean isNullable(final Symbol... symbols) {
		return isNullable(Arrays.asList(symbols));
	}

	/**
	 * @param symbols sequence of grammar symbols
	 * @return whether the sequence is nullable
	 */
	public boolean isNullable(final List<Symbol> symbols) {
		return symbols.stream().allMatch(symbol -> nullables.contains(symbol) || //
				symbol.isQuantifier() && symbol.asQuantifier().isNullable(this::isNullable));
	}

	public Set<Verb> firsts(final Symbol... symbols) {
		return firsts(Arrays.asList(symbols));
	}

	public Set<Verb> firsts(final Collection<Symbol> symbols) {
		final Set<Verb> $ = new LinkedHashSet<>();
		for (final Symbol s : symbols) {
			$.addAll(firsts.get(s));
			if (!isNullable(s))
				break;
		}
		return unmodifiableSet($);
	}

	public FancyEBNF reachableSubBNF() {
		final Set<DerivationRule> subR = new LinkedHashSet<>();
		final Set<Verb> subΣ = new LinkedHashSet<>();
		final Set<Variable> subV = new LinkedHashSet<>();
		Set<Variable> newSubV = new LinkedHashSet<>();
		newSubV.add(ε);
		int previousCount = -1;
		while (previousCount < subV.size()) {
			previousCount = subV.size();
			final Set<Variable> newestSubV = new LinkedHashSet<>();
			for (final DerivationRule rule : R) {
				if (!newSubV.contains(rule.lhs))
					continue;
				subR.add(rule);
				for (final ExtendedSententialForm sf : rule.rhs)
					for (final Symbol symbol : sf)
						if (symbol.isVerb())
							subΣ.add(symbol.asVerb());
						else if (symbol.isVariable())
							newestSubV.add(symbol.asVariable());
						else
							throw new RuntimeException("problem while analyzing BNF");
			}
			subV.addAll(newSubV);
			newSubV = newestSubV;
		}
		return new FancyEBNF(subΣ, subV, subR, ε, null, null, null, true);
	}

	public boolean isOriginalVariable(final Symbol symbol) {
		return symbol.isVariable() && !extensionProducts.contains(symbol);
	}

	private Set<Symbol> getNullables() {
		final Set<Symbol> $ = new LinkedHashSet<>();
		while ($.addAll(Γ.stream() //
				.filter(v -> rhs(v).stream() //
						.anyMatch(sf -> sf.stream().allMatch(symbol -> isNullable(symbol, $)))) //
				.collect(toSet())))
			;
		return $;
	}

	private boolean isNullable(final Symbol symbol, final Set<Symbol> knownNullables) {
		if (symbol.isVerb())
			return false;
		if (symbol.isVariable())
			return knownNullables.contains(symbol);
		if (symbol.isQuantifier())
			return symbol.asQuantifier().isNullable(s -> isNullable(s, knownNullables));
		throw new RuntimeException("problem while analyzing BNF");
	}

	private Map<Symbol, Set<Verb>> getFirsts() {
		final Map<Symbol, Set<Verb>> $ = new LinkedHashMap<>();
		Σ.forEach(σ -> $.put(σ, singleton(σ)));
		Γ.forEach(v -> $.put(v, new LinkedHashSet<>()));
		for (boolean changed = true; changed;) {
			changed = false;
			for (final Variable v : Γ)
				for (final ExtendedSententialForm sf : rhs(v))
					for (final Symbol symbol : sf) {
						changed |= $.get(v).addAll(!symbol.isQuantifier() ? $.get(symbol) : //
								symbol.asQuantifier().getFirsts($::get));
						if (!isNullable(symbol))
							break;
					}
		}
		Γ.forEach(v -> $.put(v, unmodifiableSet($.get(v))));
		return unmodifiableMap($);
	}

	private Map<Variable, Set<Verb>> getFollows() {
		final Map<Variable, Set<Verb>> $ = new LinkedHashMap<>();
		Γ.forEach(v -> $.put(v, new LinkedHashSet<>()));
		$.get(Constants.S).add(Constants.$$);
		for (boolean changed = true; changed;) {
			changed = false;
			for (final Variable v : Γ)
				for (final ExtendedSententialForm sf : rhs(v))
					for (int i = 0; i < sf.size(); ++i) {
						if (!sf.get(i).isVariable())
							continue;
						final Variable current = sf.get(i).asVariable();
						final List<Symbol> rest = sf.subList(i, sf.size());
						changed |= $.get(current).addAll(firsts(rest));
						if (isNullable(rest))
							changed |= $.get(v).addAll($.get(current));
					}
		}
		Γ.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
		return unmodifiableMap($);
	}

	public static FancyEBNF toBNF(final PlainBNF specification) {
		final Builder $ = new Builder();
		$.start(specification.start);
		for (final Rule rule : specification.rule)
			if (rule instanceof Derivation) {
				// Derivation rule.
				final Derivation derivation = (Derivation) rule;
				Variable lhs = derivation.derive;
				if (derivation.ruleBody instanceof ConcreteDerivation) {
					// Concrete derivation rule.
					ConcreteDerivation concrete = (ConcreteDerivation) derivation.ruleBody;
					$.derive(lhs).to((concrete).to);
					for (RuleTail tail : concrete.ruleTail)
						if (tail instanceof ConcreteDerivationTail)
							// Concrete tail.
							$.derive(lhs).to(((ConcreteDerivationTail) tail).or);
						else
							// Epsilon tail.
							$.derive(lhs).toEpsilon();
				} else
					// Epsilon derivation rule.
					$.derive(lhs).toEpsilon();
			} else {
				// Specialization rule.
				final Specialization specializationRule = (Specialization) rule;
				$.specialize(specializationRule.specialize).into(specializationRule.into);
			}
		try {
			return $.build();
		} catch (Exception e) {
			throw new RuntimeException(
					"problem while analyzing BNF, make sure the grammar adheres its class description (LL/LR/etc)", e);
		}
	}

	@Deprecated
	private static class Builder {
		private final Set<Verb> Σ;
		private final Set<Variable> V;
		private final Set<DerivationRule> R;
		private Variable start;
		private final Set<Variable> heads;

		public Builder() {
			this.Σ = new LinkedHashSet<>();
			this.V = new LinkedHashSet<>();
			this.R = new LinkedHashSet<>();
			this.heads = new LinkedHashSet<>();
		}

		public Derive derive(final Variable lhs) {
			processSymbol(lhs);
			return new Derive(lhs);
		}

		public Specialize specialize(final Variable lhs) {
			return new Specialize(lhs);
		}

		void processSymbol(final Symbol symbol) {
			assert !symbol.isTerminal();
			if (symbol.isVerb()) {
				Σ.add(symbol.asVerb());
				symbol.asVerb().parameters.stream() //
						.map(TypeParameter::declaredHeadVariables) //
						.forEach(heads::addAll);
			} else if (symbol.isQuantifier())
				symbol.asQuantifier().abbreviatedSymbols().forEach(this::processSymbol);
			else if (symbol.isVariable()) {
				final Variable variable = symbol.asVariable();
				if (!V.contains(variable)) {
					V.add(variable);
					R.add(new DerivationRule(variable, new ArrayList<>()));
				}
			}
		}

		public final Builder start(final Variable startVariable) {
			start = startVariable;
			return this;
		}

		public FancyEBNF build() {
			assert start != null : "declare a start variable";
			return new FancyEBNF(Σ, V, R, start, heads, null, null, true);
		}

		List<ExtendedSententialForm> rhs(final Variable v) {
			return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
		}

		public class Derive {
			private final Variable lhs;

			public Derive(final Variable lhs) {
				this.lhs = lhs;
			}

			public Builder to(final Symbol... sententialForm) {
				final ExtendedSententialForm processedSententialForm = new ExtendedSententialForm(
						Arrays.stream(sententialForm) //
								.map(symbol -> {
									return !symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal());
								}) //
								.collect(Collectors.toList()));
				processedSententialForm.forEach(Builder.this::processSymbol);
				rhs(lhs).add(processedSententialForm);
				return Builder.this;
			}

			public Builder toEpsilon() {
				final ExtendedSententialForm processedSententialForm = new ExtendedSententialForm();
				rhs(lhs).add(processedSententialForm);
				return Builder.this;
			}
		}

		public class Specialize {
			private final Variable lhs;

			public Specialize(final Variable lhs) {
				this.lhs = lhs;
			}

			public Builder into(final Variable... variables) {
				for (final Variable variable : variables) {
					final ExtendedSententialForm processedSententialForm = new ExtendedSententialForm(variable);
					processedSententialForm.forEach(Builder.this::processSymbol);
					rhs(lhs).add(processedSententialForm);
				}
				return Builder.this;
			}
		}
	}

	public static FancyEBNF fromANTLR(Grammar grammar) {
		assert grammar.ast.getChildCount() == 2 : "ANTLR grammar is not simplified";
		Builder $ = new Builder();
		boolean initialized = false;
		Tree rules = grammar.ast.getChild(1);
		Counter counter = new Counter();
		for (int i = 0; i < rules.getChildCount(); ++i) {
			Tree rule = rules.getChild(i);
			assert rule.getChildCount() == 2;
			String variableName = rule.getChild(0).getText();
			Variable variable = Variable.byName(variableName);
			if (!initialized) {
				// Assume first ANTLR variable is start variable.
				$.start(variable);
				initialized = true;
			}
			Optional<Symbol> rhs = extractANTLRSentential($, rule.getChild(1), counter);
			if (rhs.isPresent())
				$.derive(variable).to(rhs.get());
			else
				$.derive(variable).toEpsilon();
		}
		return $.build();
	}

	private static Optional<Symbol> extractANTLRSentential(Builder $, Object element, Counter nameCounter) {
		if (element instanceof List) {
			List<?> elements = (List<?>) element;
			if (elements.isEmpty())
				return Optional.empty();
			if (elements.size() == 1)
				return extractANTLRSentential($, elements.get(0), nameCounter);
			Variable top = Variable.byName(intermediateVariableName + nameCounter.getAndInc());
			List<Symbol> items = new ArrayList<>();
			for (Object item : elements)
				extractANTLRSentential($, item, nameCounter).ifPresent(items::add);
			$.derive(top).to(items.toArray(new Symbol[items.size()]));
			return Optional.of(top);
		}
		if (element instanceof AltAST)
			return extractANTLRSentential($, ((AltAST) element).getChildren(), nameCounter);
		if (element instanceof BlockAST) {
			BlockAST block = (BlockAST) element;
			if (block.getChildCount() <= 1)
				return extractANTLRSentential($, block.getChildren(), nameCounter);
			Variable top = Variable.byName(intermediateVariableName + nameCounter.getAndInc());
			List<Symbol> items = new ArrayList<>();
			for (Object item : block.getChildren())
				extractANTLRSentential($, item, nameCounter).ifPresent(items::add);
			items.forEach(symbol -> $.derive(top).to(symbol));
			return Optional.of(top);
		}
		if (element instanceof RuleRefAST)
			return Optional.of(Variable.byName(element.toString()));
		if (element instanceof StarBlockAST) {
			Optional<Symbol> inner = extractANTLRSentential($, ((StarBlockAST) element).getChildren(),
					nameCounter);
			return inner.map(Symbol::noneOrMore);
		}
		if (element instanceof PlusBlockAST) {
			Optional<Symbol> inner = extractANTLRSentential($, ((PlusBlockAST) element).getChildren(),
					nameCounter);
			return inner.map(Symbol::oneOrMore);
		}
		if (element instanceof TerminalAST) {
			String name = ((TerminalAST) element).getText();
			name = name.substring(1, name.length() - 1);
			// Assume simple terminal.
			return Optional.of(Terminal.byName(name));
		}
		throw new RuntimeException(
				String.format("Grammar element %s no supported", element.getClass().getSimpleName()));
	}
}
