package org.spartan.fajita.api.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BNFAnalyzer<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {

    private final BNF<Term, NT> bnf;

    private final Hashtable<Term, AnalyzedTerm> terminals;
    private final Hashtable<NT, AnalyzedNT> nonterminals;

    public BNFAnalyzer(final BNF<Term, NT> bnf) {
	this.bnf = bnf;
	terminals = new Hashtable<>();
	nonterminals = new Hashtable<>();
    }

    public void analyze() {
	for (InheritenceRule<Term, NT> rule : bnf.getInheritenceRules())
	    analyzeInheritenceRule(rule);
	for (DerivationRule<Term, NT> rule : bnf.getDerivationRules())
	    analyzeDerivationRule(rule);

    }

    private void analyzeDerivationRule(final DerivationRule<Term, NT> rule) {
	AnalyzedNT analyzed = getNT(rule.lhs);
	analyzed.setAbstract(false);
	analyzed.setChildren(rule.expression);
	for (Symbol symb : rule.expression) {
	    AnalyzedSymbol<? extends Symbol> analyzedSymb = getSymbol(symb);
	    analyzedSymb.setDerivedFrom(rule.lhs);
	}
    }

    private void analyzeInheritenceRule(final InheritenceRule<Term, NT> rule) {
	AnalyzedNT analyzed = getNT(rule.lhs);
	analyzed.setAbstract(true);
	analyzed.setChildren(rule.subtypes);
	for (NT subtype : rule.subtypes)
	    getNT(subtype).setAbstractParent(rule.lhs);
    }

    @SuppressWarnings("unchecked")
    private AnalyzedSymbol<? extends Symbol> getSymbol(final Symbol s){
	if (bnf.getNtClass().isAssignableFrom(Symbol.class))
	    return getNT((NT)s);
	if (bnf.getTermClass().isAssignableFrom(Symbol.class))
	    return getTerm((Term)s);
	throw new IllegalArgumentException("unknown type");
    }

    private AnalyzedNT getNT(final NT nt) {
	if (nonterminals.containsKey(nt))
	    return nonterminals.get(nt);
	AnalyzedNT analyzedNT = new AnalyzedNT(nt);
	nonterminals.put(nt, analyzedNT);
	return analyzedNT;
    }

    private AnalyzedTerm getTerm(final Term term) {
	if (terminals.containsKey(term))
	    return terminals.get(term);
	AnalyzedTerm analyzedTerm = new AnalyzedTerm(term);
	terminals.put(term, analyzedTerm);
	return analyzedTerm;
    }
    
    Collection<AnalyzedTerm> getAnalyzedTerms() {
	return terminals.values();
    }

    Collection<AnalyzedNT> getAnalyzedNTs() {
	return nonterminals.values();
    }

    public class AnalyzedSymbol<S extends Symbol> {
	private final S symbol;
	private NT derivedFrom;

	private AnalyzedSymbol(final S symb) {
	    this.symbol= symb;
	    derivedFrom = null;
	}


	public S getSymbol() {
	    return symbol;
	}

	public NT getDerivedFrom() {
	    return derivedFrom;
	}
	
	private void setDerivedFrom(final NT derivedFrom) {
	    this.derivedFrom = derivedFrom;
	}
    }
    
    public final class AnalyzedNT extends AnalyzedSymbol<NonTerminal>{

	private boolean isAbstract;
	private NT abstractParent;
	private final List<Symbol> children;


	public AnalyzedNT(final NT nt) {
	    super(nt);
	    isAbstract = false;
	    children = new ArrayList<>();
	}

	protected void setChildren(final Symbol[] subtypes) {
	    this.children.addAll(Arrays.asList(subtypes));
	}
	
	
	public Collection<Symbol> getChildren() {
	    return children;
	}
	
	public boolean isAbstract() {
	    return isAbstract;
	}

	private void setAbstract(final boolean isAbstract) {
	    this.isAbstract = isAbstract;
	}

	public NT getAbstractParent() {
	    return abstractParent;
	}
	
	private void setAbstractParent(final NT parent) {
	    abstractParent = parent;
	}


	public boolean isStarter(){
	    return true;
	}
	
    }

    public final class AnalyzedTerm extends AnalyzedSymbol<Term>{

	public AnalyzedTerm(final Term t) {
	    super(t);
	}

    }
    
}
