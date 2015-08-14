package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public final class BNF<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    private final Class<NT> ntClass;
    private final Class<Term> termClass;
    private final String apiName;
    private final Collection<DerivationRule<Term, NT>> derivationRules;
    private final Collection<InheritenceRule<Term, NT>> inheritenceRules;

    BNF(final BNFBuilder<Term, NT> builder) {
	ntClass = builder.ntClass;
	termClass = builder.termClass;
	apiName = builder.apiName;
	derivationRules = builder.derivationRules;
	inheritenceRules = builder.inheritenceRules;
	nullableSymbols = calculateNullableSymbols();
    }

    public Collection<Rule<Term, NT>> getAllRules() {
	ArrayList<Rule<Term, NT>> $ = new ArrayList<>();
	$.addAll(getDerivationRules());
	$.addAll(getInheritenceRules());
	return $;
    }

    public Collection<NT> getNonTerminals() {
	return EnumSet.allOf(ntClass);
    }

    public Collection<Term> getTerminals() {
	return EnumSet.allOf(termClass);
    }

    public Class<Term> getTermClass() {
	return termClass;
    }

    public Class<NT> getNtClass() {
	return ntClass;
    }

    public String getApiName() {
	return apiName;
    }

    @Override
    public String toString() {
	SortedSet<Rule<Term, NT>> rules = new TreeSet<>();
	rules.addAll(this.getDerivationRules());
	rules.addAll(this.getInheritenceRules());
	StringBuilder sb = new StringBuilder("Rules for " + getApiName() + ":\n");
	for (Rule<Term, NT> rule : rules)
	    sb.append(rule.toString() + "\n");
	return sb.toString();
    }

    public Collection<DerivationRule<Term, NT>> getDerivationRules() {
	return derivationRules;
    }

    public Collection<InheritenceRule<Term, NT>> getInheritenceRules() {
	return inheritenceRules;
    }

    private final Set<NonTerminal> nullableSymbols;

    private Set<NonTerminal> calculateNullableSymbols() {
	HashSet<NonTerminal> nullables = new HashSet<>();
	nullables.add(NonTerminal.EPSILON);
	boolean moreChanges;
	do {
	    moreChanges = false;
	    for (Rule<Term, NT> rule : getInheritenceRules())
		if ((!nullables.contains(rule.lhs))
			&& rule.getChildren().stream().anyMatch(child -> nullables.contains(child))) {
		    nullables.add(rule.lhs);
		    moreChanges = true;
		}

	    for (Rule<Term, NT> rule : getInheritenceRules())
		if ((!nullables.contains(rule.lhs))
			&& rule.getChildren().stream().allMatch(child -> nullables.contains(child))) {
		    nullables.add(rule.lhs);
		    moreChanges = true;
		}

	} while (moreChanges);
	return nullables;
    }

    public boolean isNullable(final Symbol ... expression) {
	return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
    }

    // public Collection<Term> getFirstSet(final List<Symbol> expression) {
    //
    // }
}
