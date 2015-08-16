package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

/**
 * @author Tomer
 *
 */
public class BNFBuilder {

    private final Set<InheritenceRule> inheritenceRules;
    private final Set<DerivationRule> derivationRules;
    private final Set<Terminal> terminals;
    private final Set<NonTerminal> nonterminals;
    private final Set<NonTerminal> startSymbols;
    private final Set<Terminal> overloads;

    private String apiName;

    public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> BNFBuilder(
	    final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
	terminals = new HashSet<>(EnumSet.allOf(terminalEnum));
	nonterminals = new HashSet<>(EnumSet.allOf(nonterminalEnum));
	inheritenceRules = new HashSet<>();
	derivationRules = new HashSet<>();
	startSymbols = new HashSet<>();
	overloads = new HashSet<>();
    }

    public InitialConfigurator startConfig() {
	return new InitialConfigurator();
    }

    private boolean symbolExists(final Symbol symb) {
	return getNonTerminals().contains(symb) || getTerminals().contains(symb);
    }

    Set<NonTerminal> getNonTerminals() {
	return nonterminals;
    }

    Set<Terminal> getTerminals() {
	return terminals;
    }

    private BNFBuilder checkNewRule(final Rule r) {
	if (!symbolExists(r.lhs))
	    throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
	boolean firstRule = (!getInheritenceRules().contains(r)) && (!getDerivationRules().contains(r));
	if (!firstRule)
	    throw new IllegalStateException("Nonterminal '" + r.lhs.name() + "' already has a rule.");
	return this;
    }

    private void addDerivationRule(final NonTerminal lhs, final List<Symbol> symbols) {
	DerivationRule r = new DerivationRule(lhs, symbols, getInheritenceRules().size() + getDerivationRules().size());
	checkNewRule(r);
	getDerivationRules().add(r);

    }

    private void addInheritenceRule(final NonTerminal lhs, final List<NonTerminal> nts) {
	InheritenceRule r = new InheritenceRule(lhs, nts, getInheritenceRules().size() + getDerivationRules().size());
	checkNewRule(r);
	getInheritenceRules().add(r);
    }

    private void addOverload(final String name, final Type type) {
	// TODO: check that there are no duplicate (+even with erasure)
	overloads.add(new Terminal() {

	    @Override
	    public String name() {
		return name;
	    }

	    @Override
	    public String toString() {
		return methodSignatureString();
	    }

	    @Override
	    public Type type() {
		return type;
	    }
	});
    }

    private void validate() {
	validateNonterminals();
	validateNoEpsilons();
	validateOverloads();
    }

    private void validateOverloads() {
	overloads.forEach(t -> {
	    if (overloads.stream().anyMatch(t2 -> t2.name().equals(t.name()) && t2.type().equals(t.type()) && t != t2)
		    || terminals.stream().anyMatch(t2 -> t2.name().equals(t.name()) && t2.type().equals(t.type())))
		throw new IllegalStateException("overload: " + t.methodSignatureString() + " already exists.");
	});
    }

    private void validateNonterminals() {
	for (NonTerminal nonTerminal : getNonTerminals())
	    if ((!getDerivationRules().stream().anyMatch(rule -> rule.lhs.equals(nonTerminal))) //
		    && (!getInheritenceRules().stream().anyMatch(rule -> rule.lhs.equals(nonTerminal))))
		throw new IllegalStateException("nonTerminal " + nonTerminal + " has no rule");
    }

    private void validateNoEpsilons() {
	// TODO: is that necessary? will we have a possible hiding problem?
	if (getNonTerminals().stream().anyMatch(nt -> nt.name().equals(NonTerminal.EPSILON.name())))
	    throw new IllegalStateException(
		    "A NT with the name " + NonTerminal.EPSILON.name() + " was found. this is not allowed.");
	if (getTerminals().stream().anyMatch(term -> term.name().equals(Terminal.epsilon.name())))
	    throw new IllegalStateException(
		    "A terminal with the name " + Terminal.epsilon.name() + " was found. this is not allowed.");
    }

    private BNF finish() {
	validate();
	nonterminals.add(NonTerminal.EPSILON);
	terminals.add(Terminal.epsilon);
	terminals.addAll(overloads);
	return new BNF(BNFBuilder.this);
    }

    String getApiName() {
	return apiName;
    }

    private void setApiName(final String apiName) {
	this.apiName = apiName;
    }

    Set<DerivationRule> getDerivationRules() {
	return derivationRules;
    }

    Set<InheritenceRule> getInheritenceRules() {
	return inheritenceRules;
    }

    Set<NonTerminal> getStartSymbols() {
	return startSymbols;
    }

    private void setStartSymbols(final NonTerminal[] startSymbols) {
	this.startSymbols.addAll(Arrays.asList(startSymbols));
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
	    return new InitialDeriver(lhs);
	}

	public BNF finish() {
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
	    addDerivationRule(lhs, symbols);
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
	    addDerivationRule(lhs, symbols);
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
	    addInheritenceRule(lhs, nts);
	}

    }

    public class InitialConfigurator {
	public AfterName setApiNameTo(final String apiName) {
	    setApiName(apiName);
	    return new AfterName();
	}
    }

    public class AfterName {

	public NewOverload setStartSymbols(final NonTerminal nt, final NonTerminal... nts) {
	    NonTerminal[] newNts = Arrays.copyOf(nts, nts.length + 1);
	    newNts[nts.length] = nt;
	    BNFBuilder.this.setStartSymbols(newNts);
	    return new NewOverload();
	}

    }

    public class NewOverload {
	public OverloadWith overload(final Terminal t) {
	    return new OverloadWith(t.name());
	}

	public EndConfig endConfig() {
	    return new EndConfig();
	}
    }

    public class OverloadWith {

	private final String terminalName;

	public OverloadWith(final String terminalName) {
	    this.terminalName = terminalName;
	}

	public NewOverload with(final Class<?> clss1, final Class<?>... classes) {
	    addOverload(terminalName, new Type(clss1, classes));
	    return new NewOverload();
	}

    }

    public class EndConfig {
	public InitialDeriver derive(final NonTerminal nt) {
	    return new InitialDeriver(nt);
	}
    }

}
