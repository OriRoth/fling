package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	private final Terminal EPSILON_TERM = term("epsilon");
	private final NonTerminal EPSILON_NT = nt("EPSILON");

	public BNF() {
		rules = new HashSet<>();
		nonTerminals = new HashSet<>();
		terminals = new HashSet<>();

		nonTerminals.add(EPSILON_NT);
		terminals.add(EPSILON_TERM);
	}

	public Deriver derive(final String ntName) {
		return new Deriver(ntName);
	}

	private BNF inheritenceRule(final NonTerminal lhs, final NonTerminal... subtypes) {
		addSymbol(lhs);
		for (NonTerminal nt : subtypes)
			addSymbol(nt);

		addRule(new InheritenceRule(lhs, subtypes));
		return this;
	}

	private BNF productionRule(final NonTerminal nt, final Symbol... symbols) {
		addSymbol(nt);
		for (Symbol symbol : symbols) {
			addSymbol(symbol);
			if (symbol.getClass() == NonTerminal.class && isOptional(symbol))
				handleOptional((NonTerminal)symbol);
		}

		addRule(new DerivationRule(nt, symbols));
		return this;
	}

	private void handleOptional(final NonTerminal nt) {
		if (rules.stream().anyMatch(rule -> rule.lhs.equals(nt)))
			// if this optional is already handled, return
			return;
		NonTerminal original = (NonTerminal) stripOptional(nt);
		inheritenceRule(nt,EPSILON_NT,original);
	}

	private boolean isOptional(final Symbol symbol) {
		String name = symbol.identifier;
		return name.charAt(0) == '[' && 
				name.charAt(name.length() - 1) == ']';
	}

	private Symbol stripOptional(final Symbol s){
		String symbolName = s.identifier.substring(1,s.identifier.length()-1);
		if(s.getClass() == NonTerminal.class)
			return nt(symbolName);
		else if (s.getClass() == Terminal.class)
			return term(symbolName);
		throw new IllegalStateException("Symbol is not a Terminal nor a nonTerminal");
	}
	
	private void addRule(final Rule r) {
		boolean firstRule = rules.add(r);
		if (!firstRule)
			throw new IllegalStateException("Nonterminal '" + r.lhs.identifier + "' already has a rule.");
	}

	private void addSymbol(final Symbol symbol) {
		if (symbol.getClass() == NonTerminal.class)
			nonTerminals.add((NonTerminal) symbol);
		else if (symbol.getClass() == Terminal.class)
			terminals.add((Terminal) symbol);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Rules:\n");
		for (Rule rule : rules)
			sb.append(rule.toString() + "\n");
		return sb.toString();
	}

	private static Terminal term(final String terminal) {
		return new Terminal(terminal);
	}

	private static NonTerminal nt(final String ntName) {
		validateNT(ntName);
		return new NonTerminal(ntName);
	}

	private static void validateNT(final String nt) {
		// TODO check only capital letters + '_' , and no other chars :
		// ([A-Z_]+) + must start with capital

	}

	public static String optional(final String nt) {
		return "[" + nt + "]";
	}

	private static Symbol parseSymbol(final String symbol) {
		return Character.isLowerCase(symbol.charAt(0)) || (symbol.charAt(0) == '[' && Character.isLowerCase(symbol.charAt(1))) 
				? term(symbol) 
						: nt(symbol);
	}

	public static Func func(final String functionName){
		return new Func(functionName);
	}
	

	public final class Deriver {

		public final String lhs;

		private Deriver(final String lhs) {
			this.lhs = lhs;
		}

		public BNF toOneOf(final String... nonterminals) {
			NonTerminal[] array = Arrays.stream(nonterminals)
					.map(nonterminal -> nt(nonterminal))
					.collect(Collectors.toList())
					.toArray(new NonTerminal[]{});
			return inheritenceRule(nt(lhs), array);
		}

		public BNF toOneOf(final String[]... arrays) {
			List<String> nonterminals = new ArrayList<>();
			for (String[] array : arrays)
				nonterminals.addAll(Arrays.asList(array));
			return toOneOf(nonterminals.toArray(new String[nonterminals.size()]));
		}
		
		public BNF to(final String... symbols) {
			Symbol[] array = Arrays.stream(symbols)
					.map(symb -> parseSymbol(symb))
					.collect(Collectors.toList())
					.toArray(new Symbol[]{});
			return productionRule(nt(lhs), array);

		}

		public BNF to(final String[] ... arrays){
			List<String> symbols = new ArrayList<>();
			for (String[] array : arrays)
				symbols.addAll(Arrays.asList(array));
			return to(symbols.toArray(new String[symbols.size()]));
		}
	}

	public static final class Func{

		private final String functionName;

		public Func(final String functionName) {
			this.functionName = functionName;
		}

		public String[] withParams(final String ... parameters){
			ArrayList<String> l = new ArrayList<>(Arrays.asList(parameters));
			l.add(0,functionName);
			return l.toArray(new String[l.size()]);
		}
		
		public String[] noParams(){
			return new String[]{functionName};
		}
	}
}
