package org.spartan.fajita.api.parser;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

/**
 * A LR0 Item
 * 
 * @author Tomer
 *
 */
public class Item<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    private final Rule rule;
    private final List<Compound> astFractions;
    private int dotIndex;

    public Item(final Rule rule) {
	this.rule = rule;
	dotIndex = 0;
	astFractions = new ArrayList<>();
    }

    public int getDotIndex() {
	return dotIndex;
    }

    public void advance(final Compound child) {
	astFractions.add(child);
	dotIndex++;
    }

    public boolean isLegalLookahead(final Symbol symb) {
	return (rule.getChildren().size() > dotIndex) //
		&& symb == rule.getChildren().get(dotIndex);
    }
}
