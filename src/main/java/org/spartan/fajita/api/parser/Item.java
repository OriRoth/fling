package org.spartan.fajita.api.parser;

import java.util.List;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.Symbol;

/**
 * A LR0 Item
 * 
 * @author Tomer
 *
 */
public class Item {
    public final DerivationRule rule;
    public final int dotIndex;

    public Item(final DerivationRule rule, final int dotIndex) {
	this.dotIndex = dotIndex;
	this.rule = rule;
    }

    public Item advance() {
	if (readyToReduce())
	    throw new IllegalStateException("cannot advance a ready to reduce item");
	return new Item(rule, dotIndex + 1);
    }

    public boolean readyToReduce() {
	return dotIndex == rule.getChildren().size();
    }

    public boolean isLegalLookahead(final Symbol symb) {
	return (rule.getChildren().size() > dotIndex) //
		&& symb == rule.getChildren().get(dotIndex);
    }

    @Override
    public int hashCode() {
	return rule.hashCode() * Integer.hashCode(dotIndex);
    }

    @Override
    public boolean equals(final Object obj) {
	if (obj.getClass() != Item.class)
	    return false;
	Item i = (Item) obj;
	return dotIndex == i.dotIndex && rule.equals(i.rule);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(rule.lhs.methodSignatureString() + " ::= ");
	List<Symbol> expression = rule.getChildren();
	for (int i = 0; i < expression.size(); i++) {
	    if (i == dotIndex)
		sb.append(". ");
	    Symbol symb = expression.get(i);
	    sb.append(symb.methodSignatureString() + " ");
	}
	if (expression.size() == dotIndex)
	    sb.append(". ");
	return sb.toString();
    }
}
