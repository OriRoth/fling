package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.examples.BinaryExpressions.and;
import static org.spartan.fajita.api.examples.BinaryExpressions.bool;
import static org.spartan.fajita.api.examples.BinaryExpressions.not;
import static org.spartan.fajita.api.examples.BinaryExpressions.or;
import static org.spartan.fajita.api.uses.ASTViewer.showASTs;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.AND;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.EXPRESSION;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.LITERAL;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.NOT;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.OR;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.S;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.and;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.bool;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.not;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.or;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.examples.BinaryExpressions.Literal;

public class BinaryExpressions {
    public static void expressionBuilder() {

	// top down
	Compound e0 = bool(true);
	Literal e1 = bool(true).or().bool(false);

	Literal e2 = bool(true).or().bool(false).and().bool(false);
	Literal e3 = not().not().bool(true);

	// bottom up
	Compound e4 = not(bool(true));
	Compound e5 = or(bool(true), bool(false));
	Compound e6 = and(bool(false), bool(true));
	Compound e7 = or(or(bool(true), bool(false)), and(bool(true), bool(false)));

	showASTs(e0, e1, e2, e3, e4, e5, e6, e7);
    }

    public enum Term implements Terminal {
	bool, and, or, not;

	@Override
	public Class<?> type() {
	    return null;
	}
    }

    public static enum NT implements NonTerminal {
	S, LITERAL, EXPRESSION, AND, OR, NOT;
    }

    public static void buildBNF() {
	BNF<BinaryExpressions.Term, BinaryExpressions.NT> b = new BNF<>(Term.class, NT.class);

	// define the rules
	b //
		.derive(S).toOneOf(EXPRESSION) //
		.derive(EXPRESSION).toOneOf(OR, AND, LITERAL, NOT) //
		.derive(LITERAL).to(bool)//
		.derive(OR).to(EXPRESSION, or, EXPRESSION)//
		.derive(AND).to(EXPRESSION, and, EXPRESSION)//
		.derive(NOT).to(not, EXPRESSION)//
		.finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }
}
