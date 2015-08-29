package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public final class BNF {
    private final String apiName;
    private final Set<Terminal> terminals;
    private final Set<NonTerminal> nonterminals;
    private final Collection<DerivationRule> derivationRules;
    private final Collection<InheritenceRule> inheritenceRules;
    private final Set<NonTerminal> startSymbols;
    private final Set<NonTerminal> nullableSymbols;
    private final Map<Symbol, Set<Terminal>> baseFirstSets;
    private final Map<NonTerminal, Set<Terminal>> followSets;

    BNF(final BNFBuilder builder) {
	apiName = builder.getApiName();
	terminals = builder.getTerminals();
	nonterminals = builder.getNonTerminals();
	derivationRules = builder.getDerivationRules();
	inheritenceRules = builder.getInheritenceRules();
	startSymbols = builder.getStartSymbols();
	nullableSymbols = calculateNullableSymbols();
	baseFirstSets = calculateSymbolFirstSet();
	followSets = calculateFollowSets();
    }

    private Set<NonTerminal> calculateNullableSymbols() {
	HashSet<NonTerminal> nullables = new HashSet<>();
	nullables.add(NonTerminal.EPSILON);
	boolean moreChanges;
	do {
	    moreChanges = false;
	    for (Rule rule : getInheritenceRules())
		if ((!nullables.contains(rule.lhs))
			&& rule.getChildren().stream().anyMatch(child -> nullables.contains(child))) {
		    nullables.add(rule.lhs);
		    moreChanges = true;
		}

	    for (Rule rule : getInheritenceRules())
		if ((!nullables.contains(rule.lhs))
			&& rule.getChildren().stream().allMatch(child -> nullables.contains(child))) {
		    nullables.add(rule.lhs);
		    moreChanges = true;
		}

	} while (moreChanges);
	return nullables;
    }

    private Map<Symbol, Set<Terminal>> calculateSymbolFirstSet() {

	Map<Symbol, Set<Terminal>> $ = new HashMap<>();

	for (NonTerminal nt : getNonTerminals())
	    if (isNullable(nt))
		$.put(nt, new HashSet<>(Arrays.asList(Terminal.epsilon)));
	    else
		$.put(nt, new HashSet<>());

	for (Terminal term : getTerminals())
	    $.put(term, new HashSet<>(Arrays.asList(term)));

	boolean moreChanges;
	do {
	    moreChanges = false;
	    for (InheritenceRule iRule : getInheritenceRules())
		for (NonTerminal subtype : iRule.subtypes)
		    moreChanges |= $.get(iRule.lhs).addAll($.get(subtype));

	    for (DerivationRule dRule : getDerivationRules())
		for (Symbol symbol : dRule.expression) {
		    moreChanges |= $.get(dRule.lhs).addAll($.get(symbol));
		    if (!isNullable(symbol))
			break;
		}
	} while (moreChanges);
	return $;
    }

    private Map<NonTerminal, Set<Terminal>> calculateFollowSets() {
	Map<NonTerminal, Set<Terminal>> $ = new HashMap<>();
	Set<NonTerminal> startNTs = getStartSymbols();

	// initialization
	for (NonTerminal nt : getNonTerminals())
	    if (startNTs.contains(nt))
		$.put(nt, new HashSet<>(Arrays.asList(Terminal.$)));
	    else
		$.put(nt, new HashSet<>());

	// iterative step
	boolean moreChanges;
	do {
	    moreChanges = false;
	    for (InheritenceRule iRule : getInheritenceRules())
		for (NonTerminal subtype : iRule.subtypes)
		    moreChanges |= $.get(subtype).addAll($.get(iRule.lhs));

	    for (DerivationRule dRule : getDerivationRules())
		for (int i = 0; i < dRule.expression.size(); i++) {
		    if (dRule.expression.get(i).isTerminal())
			continue;
		    Symbol subExpression[];
		    if (i != dRule.expression.size() - 1)
			subExpression = Arrays.copyOfRange(dRule.expression.toArray(), i + 1, dRule.expression.size(),
				Symbol[].class);
		    else
			subExpression = new Symbol[] {};

		    moreChanges |= $.get(dRule.expression.get(i)).addAll(firstSetOf(subExpression));

		    if (isNullable(subExpression))
			moreChanges |= $.get(dRule.expression.get(i)).addAll($.get(dRule.lhs));
		}
	} while (moreChanges);

	$.values().forEach(followSet -> followSet.remove(Terminal.epsilon));

	return $;
    }

    public Collection<Rule> getAllRules() {
	ArrayList<Rule> $ = new ArrayList<>();
	$.addAll(getDerivationRules());
	$.addAll(getInheritenceRules());
	return $;
    }

    public Set<NonTerminal> getNonTerminals() {
	return nonterminals;
    }

    public Set<Terminal> getTerminals() {
	return terminals;
    }

    public Set<NonTerminal> getStartSymbols() {
	return startSymbols;
    }

    public String getApiName() {
	return apiName;
    }

    @Override
    public String toString() {
	SortedSet<Rule> rules = new TreeSet<>();
	rules.addAll(getDerivationRules());
	rules.addAll(getInheritenceRules());
	StringBuilder sb = new StringBuilder() //
		.append("Terminals set: " + terminals + "\n") //
		.append("Nonterminals set: " + nonterminals + "\n") //
		.append("Rules for " + getApiName() + ":\n");
	for (Rule rule : rules)
	    sb.append(rule.toString() + "\n");
	return sb.toString();
    }

    public Collection<DerivationRule> getDerivationRules() {
	return derivationRules;
    }

    public Collection<InheritenceRule> getInheritenceRules() {
	return inheritenceRules;
    }

    public boolean isNullable(final Symbol... expression) {
	return Arrays.asList(expression).stream()
		.allMatch(symbol -> nullableSymbols.contains(symbol) || symbol == Terminal.epsilon);
    }

    public Set<Terminal> firstSetOf(final Symbol... expression) {
	HashSet<Terminal> $ = new HashSet<>();
	for (Symbol symbol : expression) {
	    $.addAll(baseFirstSets.get(symbol));
	    if (!isNullable(symbol))
		break;
	}

	if (Arrays.asList(expression).stream().anyMatch(symbol -> !isNullable(symbol)))
	    $.remove(Terminal.epsilon);

	return $;
    }

    public Set<Terminal> followSetOf(final NonTerminal nt) {
	return followSets.get(nt);
    }

    public Set<Type> getOverloadsOf(final Terminal t) {
	Set<Type> collect = getTerminals() //
		.stream() //
		.filter(terminal -> terminal.name().equals(t.name())) //
		.map(terminal -> terminal.type()) //
		.collect(Collectors.toSet());
	return collect;
    }

}
