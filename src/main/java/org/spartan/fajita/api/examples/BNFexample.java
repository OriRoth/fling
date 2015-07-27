package org.spartan.fajita.api.examples;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BNFexample {
	/**
	 * I put Symbol parameterized as a future reference for me, hoping that my
	 * newer more generic implementation could rely on this example of grammer
	 * definition.
	 * 
	 * on the other hand maybe it wouldn't matter at all and i could just put
	 * object, having that programmer giving the semantics to this Data
	 * structure will have to dynamically cast the terminals
	 * 
	 * I will have to think about it later.
	 * 
	 * @author Tomer
	 *
	 * @param <T>
	 *            - in this case will only be parameterized to String
	 */
	public static abstract class Symbol<T> {
		public final T t;

		public Symbol(final T t) {
			this.t = t;
		}
	}

	public static class Terminal extends Symbol<String> {
		public Terminal(final String term) {
			super(term);
		}


		@Override
		public String toString() {
			return "\"" + t + "\"";
		}
	}

	public static class NonTerminal extends Symbol<String> {
		public NonTerminal(final String term) {
			super(term);
		}


		@Override
		public String toString() {
			return "<" + t + ">";
		}
	}

	public static class BNF {
		Map<NonTerminal, ArrayList<Symbol<?>>> rules;
		Set<NonTerminal> nonTerminals;
		Set<Terminal> terminals;

		public BNF() {
			rules = new HashMap<>();
			nonTerminals = new HashSet<>();
			terminals = new HashSet<>();
		}

		public BNF rule(final NonTerminal nt, final Symbol<?>... symbols) {
			addSymbol(nt);
			ArrayList<Symbol<?>> expression = new ArrayList<>();
			for (Symbol<?> symbol : symbols) {
				expression.add(symbol);
				addSymbol(symbol);
			}
			rules.put(nt, expression);
			return this;
		}

		private void addSymbol(final Symbol<?> symbol) {
			if (symbol.getClass()==NonTerminal.class)
				nonTerminals.add((NonTerminal) symbol);
			else if (symbol.getClass()==Terminal.class)
				terminals.add((Terminal) symbol);
		}

		public String ruleToString(final NonTerminal nt , final ArrayList<Symbol<?>> expr){
			StringBuilder sb = new StringBuilder("<" + nt.t + "> ::= ");
			expr.forEach(symb -> sb.append(symb.toString() + " "));
			return sb.toString();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("Rules:\n");
			rules.forEach((nt, expr) -> sb.append(ruleToString(nt, expr) + "\n"));
			return sb.toString();
		}
	}

	public static Terminal term(final String terminal) {
		return new Terminal(terminal);
	}

	public static NonTerminal nt(final String ntName) {
		return new NonTerminal(ntName);
	}

	public static void BnfAnalyzer(final BNF bnf) {
		System.out.println(bnf.toString());
	}

	public static void main(final String[] args){
		NonTerminal nt = nt("S");
		BNF bnf = new BNF()
				.rule(nt, nt("A"), nt("B"))
				.rule(nt, nt("A"), nt, nt("B"))
				.rule(nt("A"), term("a"))
				.rule(nt("B"), term("b"));
		BnfAnalyzer(bnf);
	}
}
