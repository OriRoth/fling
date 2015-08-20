package org.spartan.fajita.api.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class StateCalculator {

    public static State calculateClosure(final Set<Item> initialItems, final BNF bnf) {
	Set<Item> items = new HashSet<>(initialItems);
	boolean moreChanges;
	do {
	    moreChanges = false;
	    Set<NonTerminal> dotBeforeNT = items.stream()
		    .filter(item -> (!item.readyToReduce()) && NonTerminal.class
			    .isAssignableFrom(item.rule.getChildren().get(item.dotIndex).getClass())) //
		    .map(item -> (NonTerminal) item.rule.getChildren().get(item.dotIndex)) //
		    .collect(Collectors.toSet());
	    ;
	    for (NonTerminal nt : dotBeforeNT) {
		for (DerivationRule dRule : bnf.getDerivationRules()) {
		    if (!dRule.lhs.equals(nt))
			continue;
		    moreChanges |= items.add(new Item(dRule, 0));
		}

		for (InheritenceRule iRule : bnf.getInheritenceRules()) {
		    if (!iRule.lhs.equals(nt))
			continue;
		    for (DerivationRule dRule : iRule.getAsDerivationRules())
			moreChanges |= items.add(new Item(dRule, 0));
		}
	    }
	} while (moreChanges);
	return new State(items, bnf);
    }

    public static State goTo(final State state, final Symbol lookahead) {
	Set<Item> initialItems = state.items.stream().//
		filter(item -> item.isLegalLookahead(lookahead)) //
		.map(item -> item.advance()) //
		.collect(Collectors.toSet());
	return calculateClosure(initialItems, state.bnf);
    }

}
