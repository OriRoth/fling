package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.SqlFragment.NT.*;
import static org.spartan.fajita.api.uses.SqlFragment.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SqlFragment {

    private static void expressionBuilder() {
    }

    public enum Term implements Terminal {
	select, column, from, all, distinct, table, where, equals, geq, leq, literal, epsilon;
	@Override
	public Class<?> type() {
	    return null;
	}
    }

    public static enum NT implements NonTerminal {
	SELECT_STATEMENT, QUANTIFIER, ALL, DISTINCT, COLOUMNS, COLOUMNS_OPT, TABLES, TABLES_OPT, WHERE_OPT, WHERE, EXPRESSION, OP, EQUALS, GEQ, LEQ, LITERAL, EPSILON;
    }

    public static void buildBNF() {
	BNF<Term, NT> b = new BNF<>(Term.class, NT.class);

	// define the rules
	b //
		.derive(SELECT_STATEMENT).to(select, QUANTIFIER, COLOUMNS, from, TABLES, WHERE_OPT) //
		.derive(QUANTIFIER).toOneOf(ALL, DISTINCT) //
		.derive(ALL).to(all)//
		.derive(DISTINCT).to(distinct)//
		.derive(COLOUMNS).to(column, COLOUMNS_OPT)//
		.derive(COLOUMNS_OPT).toOneOf(COLOUMNS, EPSILON)//
		.derive(TABLES).to(table, TABLES_OPT) //
		.derive(TABLES_OPT).toOneOf(TABLES, EPSILON) //
		.derive(WHERE_OPT).toOneOf(WHERE, EPSILON) //
		.derive(WHERE).to(where, EXPRESSION) //
		.derive(EXPRESSION).to(column, OP, LITERAL) //
		.derive(OP).toOneOf(EQUALS, GEQ, LEQ) //
		.derive(EQUALS).to(equals) //
		.derive(GEQ).to(geq) //
		.derive(LEQ).to(leq) //
		.derive(LITERAL).to(literal) //
		.derive(EPSILON).to(epsilon).finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }

}
