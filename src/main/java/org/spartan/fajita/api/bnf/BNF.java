package org.spartan.fajita.api.bnf;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

/**
 * There are a few possibilities regarding checking the validity of the BNF
 * (each Deriver has exactly one rule):
 * <li>Throwing exception when using unknown nt - that would require building
 * the BNF rules 'Bottom up'</li>
 * <li>Waiting until parsing to check validy and throw exception - for example
 * when invoking inspectors</li>
 * <li>Adding a 'check' method that validates the BNF</li>
 * 
 * @author Tomer
 *
 */
public class BNF<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    final Set<Rule<Term,NT>> rules;
    final Set<NT> nonTerminals;
    final Set<Term> terminals;
    private final Class<Term> termClass;
    private final Class<NT> ntClass;

    public BNF(final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
	this.termClass = terminalEnum;
	this.ntClass = nonterminalEnum;
	rules = new LinkedHashSet<>();
	terminals = EnumSet.allOf(terminalEnum);
	nonTerminals = EnumSet.allOf(nonterminalEnum);
    }

    public Deriver derive(final NT nt) {
	return new Deriver(nt);
    }

    private boolean symbolExists(final Symbol symb) {
	return nonTerminals.contains(symb) || terminals.contains(symb);
    }

    private BNF<Term,NT> addNewRule(final Rule<Term,NT> r, final Symbol[] symbols) {
	if (!symbolExists(r.lhs))
	    throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
	boolean firstRule = rules.add(r);
	if (!firstRule)
	    throw new IllegalStateException("Nonterminal '" + r.lhs.name() + "' already has a rule.");
	return this;
    }

    private BNF<Term,NT> addRule(final DerivationRule<Term,NT> r) {
	return addNewRule(r, r.expression);
    }

    private BNF<Term,NT> addRule(final InheritenceRule<Term,NT> r) {
	return addNewRule(r, r.subtypes);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder("Rules:\n");
	for (Rule<Term,NT> rule : rules)
	    sb.append(rule.toString() + "\n");
	return sb.toString();
    }

    public void finish() {
	for (NonTerminal nonTerminal : nonTerminals)
	    if (!rules.stream().anyMatch(rule -> rule.lhs.equals(nonTerminal)))
		throw new IllegalStateException("nonTerminal " + nonTerminal + " has no rule");
    }

    public final class Deriver {

	public final NT lhs;

	private Deriver(final NT lhs) {
	    this.lhs = lhs;
	}

	@SuppressWarnings("unchecked")
	public BNF<Term,NT> toOneOf(final NT... nonterminals) {
	    return addRule(new InheritenceRule<Term,NT>(termClass,ntClass,lhs, nonterminals));
	}

	public BNF<Term,NT> to(final Symbol... symbols) {
	    return addRule(new DerivationRule<Term,NT>(termClass,ntClass,lhs, symbols));
	}
    }

    // public static Func func(final String functionName){
    // return new Func(functionName);
    // }

    // public static final class Func{
    //
    // private final String functionName;
    //
    // public Func(final String functionName) {
    // this.functionName = functionName;
    // }
    //
    // public String[] withParams(final String ... parameters){
    // ArrayList<String> l = new ArrayList<>(Arrays.asList(parameters));
    // l.add(0,functionName);
    // return l.toArray(new String[l.size()]);
    // }
    //
    // public String[] noParams(){
    // return new String[]{functionName};
    // }
    // }
}
