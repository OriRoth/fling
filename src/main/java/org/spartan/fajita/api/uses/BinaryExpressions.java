package org.spartan.fajita.api.uses;



import static org.spartan.fajita.api.examples.BinaryExpressions.and;
import static org.spartan.fajita.api.examples.BinaryExpressions.bool;
import static org.spartan.fajita.api.examples.BinaryExpressions.or;
import static org.spartan.fajita.api.uses.ASTViewer.showASTs;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.BinaryExpressions.Expression;
import org.spartan.fajita.api.examples.BinaryExpressions.Literal;

public class BinaryExpressions {
	public static void expressionBuilder(){
				
		// top down
		Literal e0 = new Expression().bool(true);
		Literal e1 = new Expression().bool(true).or().bool(false);
		
		Literal e2 = new Expression().bool(true).or().bool(false).and().bool(false);
		Literal e3 = new Expression().not().bool(true);
		
		// bottom up
		Compound e4 = new Expression().or(bool(true),bool(false));
		Compound e5 = new Expression().and(bool(false), bool(true));
		
		Compound e6 = new Expression().or(or(bool(true),bool(false)), and(bool(true),bool(false)));
		
		showASTs(e0,e1,e2,e3,e4,e5,e6);
	}
	
	public static void buildBNF() {
		BNF b = new BNF();

		// defining the nonterminals
		
		NonTerminal S = b.addNT("S"), 
				LITERAL = b.addNT("LITERAL"),
				EXPRESSION = b.addNT("EXPRESSION"),
				AND = b.addNT("AND"),
				OR = b.addNT("OR"),
				NOT = b.addNT("NOT");

		// i can't add 1 or 0 as terminals because there are no such types
		
		// defining the terminals
		Terminal bool = b.addTerm("boolean"),
				and = b.addTerm("and"),
				or = b.addTerm("or"),
				not = b.addTerm("not");

		// define the rules
		b		.derive(S).toOneOf(EXPRESSION)
				.derive(EXPRESSION).toOneOf(OR, AND, LITERAL, NOT)
				.derive(LITERAL).to(bool)
				.derive(OR).to(EXPRESSION, or, EXPRESSION)
				.derive(AND).to(EXPRESSION, and, EXPRESSION)
				.derive(NOT).to(not, EXPRESSION)
				.finish();
		
		System.out.println(b);
	}
	
	public static void main(final String[] args) {
		buildBNF();
		expressionBuilder();
	}
}
