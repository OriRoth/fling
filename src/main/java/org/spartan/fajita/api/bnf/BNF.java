package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
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
public class BNF {
	Set<Rule> rules;
	Set<NonTerminal> nonTerminals;
	Set<Terminal> terminals;

	private final Terminal EPSILON_TERM;
	public final NonTerminal EPSILON;

	public BNF() {
		rules = new LinkedHashSet<>();
		nonTerminals = new LinkedHashSet<>();
		terminals = new LinkedHashSet<>();
		
		EPSILON = addNT("EPSILON");
		EPSILON_TERM = addTerm("");

		nonTerminals.add(EPSILON);
		terminals.add(EPSILON_TERM);
		
		derive(EPSILON).to(EPSILON_TERM);
	}

	public Deriver derive(final NonTerminal nt) {
		return new Deriver(nt);
	}

	private boolean symbolExists(final Symbol symb){
		return nonTerminals.contains(symb) || terminals.contains(symb);	
	}
	
	private BNF addNewRule(final Rule r,final Symbol[] symbols) {
		Optional<Symbol> missingSymbol= Arrays.asList(symbols)
				.stream()
				.filter(symb -> !symbolExists(symb))
				.findAny();
		if (missingSymbol.isPresent())
			throw new IllegalArgumentException(missingSymbol.get().identifier+" is undefined.");
		if (!symbolExists(r.lhs))
			throw new IllegalArgumentException(r.lhs.identifier+" is undefined.");
		boolean firstRule = rules.add(r);
		if (!firstRule)
			throw new IllegalStateException("Nonterminal '" + r.lhs.identifier + "' already has a rule.");
		return this;
	}
	
	private BNF addRule(final DerivationRule r){
		return addNewRule(r,r.expression);
	}
	
	private BNF addRule(final InheritenceRule r){
		return addNewRule(r,r.subtypes);
	}
	
	
	public NonTerminal addNT(final String name){
		NonTerminal nt = new NonTerminal(name);
		nonTerminals.add(nt);
		return nt;
	}
	
	public Terminal addTerm(final String name){
		Terminal term = new Terminal(name);
		terminals.add(term);
		return term;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Rules:\n");
		for (Rule rule : rules)
			sb.append(rule.toString() + "\n");
		return sb.toString();
	}

//	public static Func func(final String functionName){
//		return new Func(functionName);
//	}
	

	public final class Deriver {

		public final NonTerminal lhs;

		private Deriver(final NonTerminal lhs) {
			this.lhs = lhs;
		}

		public BNF toOneOf(final NonTerminal... nonterminals) {
			return addRule(new InheritenceRule(lhs, nonterminals));
		}

		public BNF to(final Symbol... symbols) {
			return addRule(new DerivationRule(lhs, symbols));
		}
	}

//	public static final class Func{
//
//		private final String functionName;
//
//		public Func(final String functionName) {
//			this.functionName = functionName;
//		}
//
//		public String[] withParams(final String ... parameters){
//			ArrayList<String> l = new ArrayList<>(Arrays.asList(parameters));
//			l.add(0,functionName);
//			return l.toArray(new String[l.size()]);
//		}
//		
//		public String[] noParams(){
//			return new String[]{functionName};
//		}
//	}
}
