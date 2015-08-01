package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

/**
 * There are a few possibilities regarding checking the validity of the BNF
 * (each NT has exactly one rule):
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

	public BNF() {
		rules = new HashSet<>();
		nonTerminals = new HashSet<>();
		terminals = new HashSet<>();
	}

	public final class NT {

		public final String lhs;

		private NT(final String lhs) {
			this.lhs = lhs;
		}

		public BNF isOneOf(final String... nonterminals) {
			NonTerminal[] array = (NonTerminal[]) Arrays.stream(nonterminals)
					.map(nonterminal -> nt(nonterminal))
					.toArray();
			return inheritenceRule(nt(lhs), array);
		}
		
		public BNF derivesTo(final String ... symbols){
			Symbol[] array = (Symbol[]) Arrays.stream(symbols)
					.map(symb -> (symb.charAt(0)>='a' && symb.charAt(0)<='z') ? term(symb) : nt(symb))
					.toArray();
			return rule(nt(lhs), array);
			
		}
	}

	public NT nonterminal(final String ntName) {
		return new NT(ntName);
	}

	private BNF inheritenceRule(final NonTerminal lhs, final NonTerminal... subtypes) {
		addSymbol(lhs);
		for (NonTerminal nt : subtypes)
			addSymbol(nt);

		addRule(new InheritenceRule(lhs, subtypes));
		return this;
	}

	private BNF rule(final NonTerminal nt, final Symbol... symbols) {
		addSymbol(nt);
		for (Symbol symbol : symbols)
			addSymbol(symbol);

		addRule(new DerivationRule(nt, symbols));
		return this;
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
		return new NonTerminal(ntName);
	}
}
