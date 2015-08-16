package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.examples.BinaryExpressions.and;
import static org.spartan.fajita.api.examples.BinaryExpressions.bool;
import static org.spartan.fajita.api.examples.BinaryExpressions.not;
import static org.spartan.fajita.api.examples.BinaryExpressions.or;
import static org.spartan.fajita.api.uses.ASTViewer.showASTs;
import static org.spartan.fajita.api.uses.BinaryExpressions.NT.*;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.and;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.bool;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.not;
import static org.spartan.fajita.api.uses.BinaryExpressions.Term.or;

import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
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

    enum Term implements Terminal {
	bool(boolean.class), and, or, not;

	private final Type type;

	Term(final Class<?> c1, final Class<?>... classes) {
	    type = new Type(c1, classes);
	}

	Term() {
	    type = new Type();
	}

	@Override
	public Type type() {
	    return type;
	}

	@Override
	public String toString() {
	    return methodSignatureString();
	}
    }

    static enum NT implements NonTerminal {
	S, LITERAL, EXPRESSION, AND, OR, NOT;
    }

    public static void buildBNF() {
	// define the rules
	BNF b = new BNFBuilder(Term.class, NT.class) //
		//
		.startConfig() //
		.setApiNameTo("Boolean expression builder") //
		.setStartSymbols(S) //
		.overload(bool).with(Void.class) //
		.endConfig() //
		//
		.derive(S).to(EXPRESSION) //
		.derive(EXPRESSION).to(OR).or(AND).or(LITERAL).or(NOT) //
		.derive(LITERAL).to(bool)//
		.derive(OR).to(EXPRESSION).and(or).and(EXPRESSION)//
		.derive(AND).to(EXPRESSION).and(and).and(EXPRESSION)//
		.derive(NOT).to(not).and(EXPRESSION)//
		.finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }
}
