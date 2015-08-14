package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

/**
 * There are a few possibilities regarding checking the validity of the
 * BNFBuilder (each InitialDeriver has exactly one rule):
 * <li>Throwing exception when using unknown nt - that would require building
 * the BNFBuilder rules 'Bottom up'</li>
 * <li>Waiting until parsing to check validy and throw exception - for example
 * when invoking inspectors</li>
 * <li>Adding a 'check' method that validates the BNFBuilder</li>
 * 
 * @author Tomer
 *
 */
public class BNFBuilder<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    final Set<InheritenceRule<Term, NT>> inheritenceRules;
    final Set<DerivationRule<Term, NT>> derivationRules;
    final Class<Term> termClass;
    final Class<NT> ntClass;
    String apiName;

    public BNFBuilder(final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
	apiName = "defaultAPI";
	this.termClass = terminalEnum;
	this.ntClass = nonterminalEnum;
	inheritenceRules = new LinkedHashSet<>();
	derivationRules = new LinkedHashSet<>();
    }

    public BNFBuilder<Term, NT> setApiName(final String apiName) {
	this.apiName = apiName;
	return this;
    }

    public InitialDeriver derive(final NonTerminal nt) {
	return new InitialDeriver(nt);
    }

    private boolean symbolExists(final Symbol symb) {
	return getNonTerminals().contains(symb) || getTerminals().contains(symb);
    }

    private Collection<NT> getNonTerminals() {
	return EnumSet.allOf(ntClass);
    }

    private Collection<Term> getTerminals() {
	return EnumSet.allOf(termClass);
    }

    private BNFBuilder<Term, NT> checkNewRule(final Rule<Term, NT> r) {
	if (!symbolExists(r.lhs))
	    throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
	boolean firstRule = (!inheritenceRules.contains(r)) && (!derivationRules.contains(r));
	if (!firstRule)
	    throw new IllegalStateException("Nonterminal '" + r.lhs.name() + "' already has a rule.");
	return this;
    }

    private void addDerivationRule(final Class<Term> termClass, final Class<NT> ntClass, final NonTerminal lhs,
	    final List<Symbol> symbols) {
	DerivationRule<Term, NT> r = new DerivationRule<Term, NT>(termClass, ntClass, lhs, symbols,
		inheritenceRules.size() + derivationRules.size());
	checkNewRule(r);
	derivationRules.add(r);

    }

    private void addInheritenceRule(final Class<Term> termClass, final Class<NT> ntClass, final NonTerminal lhs,
	    final List<NonTerminal> nts) {
	InheritenceRule<Term, NT> r = new InheritenceRule<Term, NT>(termClass, ntClass, lhs, nts,
		inheritenceRules.size() + derivationRules.size());
	checkNewRule(r);
	inheritenceRules.add(r);
    }

    private void validate() {
	for (NonTerminal nonTerminal : getNonTerminals())
	    if ((!derivationRules.stream().anyMatch(rule -> rule.lhs.equals(nonTerminal))) //
		    && (!inheritenceRules.stream().anyMatch(rule -> rule.lhs.equals(nonTerminal))))
		throw new IllegalStateException("nonTerminal " + nonTerminal + " has no rule");
    }

    public BNF<Term, NT> finish() {
	validate();
	return new BNF<Term, NT>(BNFBuilder.this);
    }

    private abstract class Deriver {
	protected final NonTerminal lhs;
	protected final ArrayList<Symbol> symbols;

	public Deriver(final NonTerminal lhs, final Symbol... symbols) {
	    this.lhs = lhs;
	    this.symbols = new ArrayList<>(Arrays.asList(symbols));
	}

	public InitialDeriver derive(final NonTerminal lhs) {
	    addRuleToBNF();
	    return BNFBuilder.this.derive(lhs);
	}

	public BNF<Term, NT> finish() {
	    addRuleToBNF();
	    return BNFBuilder.this.finish();
	}

	/**
	 * Adds a rule to the BnfBuilder host.
	 */
	protected abstract void addRuleToBNF();
    }

    /**
     * Class for the state after derive() is called from BNFBuilder
     * 
     * @author Tomer
     *
     */
    public class InitialDeriver {

	private final NonTerminal lhs;

	private InitialDeriver(final NonTerminal lhs) {
	    this.lhs = lhs;
	}

	public UnknownDeriver to(final NonTerminal nt) {
	    return new UnknownDeriver(lhs, nt);
	}

	public NormalDeriver to(final Terminal term) {
	    return new NormalDeriver(lhs, term);
	}
    }

    /**
     * We know the first symbol in the right side , but we don't know yet
     * whether it's a derivation rule or an inheritence rule.
     * 
     * if right hand side has only one symbol it will defaultively be a
     * derivation rule.
     * 
     * @author Tomer
     *
     */
    public final class UnknownDeriver extends Deriver {

	public UnknownDeriver(final NonTerminal lhs, final Symbol symb) {
	    super(lhs, symb);
	}

	public NormalDeriver and(final Symbol symb) {
	    return new NormalDeriver(lhs, symbols.get(0), symb);
	}

	public AbstractDeriver or(final NonTerminal nt) {
	    return new AbstractDeriver(lhs, (NonTerminal) symbols.get(0), nt);
	}

	@Override
	protected void addRuleToBNF() {
	    addDerivationRule(termClass, ntClass, lhs, symbols);
	}

    }

    /**
     * Currently deriving a normal rule
     * 
     * @author Tomer
     *
     */
    public final class NormalDeriver extends Deriver {

	public NormalDeriver(final NonTerminal lhs, final Symbol child) {
	    super(lhs, child);
	}

	public NormalDeriver(final NonTerminal lhs, final Symbol firstChild, final Symbol secondChild) {
	    super(lhs, firstChild, secondChild);
	}

	public NormalDeriver and(final Symbol symb) {
	    symbols.add(symb);
	    return this;
	}

	@Override
	protected void addRuleToBNF() {
	    addDerivationRule(termClass, ntClass, lhs, symbols);
	}
    }

    /**
     * currently deriving an inheritence rule.
     * 
     * @author Tomer
     *
     */
    public final class AbstractDeriver extends Deriver {

	public AbstractDeriver(final NonTerminal lhs, final NonTerminal firstChild) {
	    super(lhs, firstChild);
	}

	public AbstractDeriver(final NonTerminal lhs, final NonTerminal firstChild, final NonTerminal secondChild) {
	    super(lhs, firstChild, secondChild);
	}

	public AbstractDeriver or(final NonTerminal nt) {
	    symbols.add(nt);
	    return this;
	}

	@Override
	protected void addRuleToBNF() {
	    List<NonTerminal> nts = new ArrayList<NonTerminal>();
	    for (Symbol s : symbols)
		nts.add((NonTerminal) s);
	    addInheritenceRule(termClass, ntClass, lhs, nts);
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