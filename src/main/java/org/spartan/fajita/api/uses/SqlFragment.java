package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.SqlFragment.NT.*;
import static org.spartan.fajita.api.uses.SqlFragment.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SqlFragment {

    private static void expressionBuilder() {
    }

    public enum Term implements Terminal {
	select, column, from, all, distinct, table, where, equals, geq, leq, literal, epsilon;
	@Override
	public Class<?> type() {
	    return Void.class;
	}
    }

    public static enum NT implements NonTerminal {
	SELECT_STATEMENT, QUANTIFIER, ALL, DISTINCT, COLOUMNS, COLOUMNS_OPT, TABLES, TABLES_OPT, WHERE_OPT, WHERE, EXPRESSION, OP, EQUALS, GEQ, LEQ, LITERAL, EPSILON;
    }

    public static void buildBNF() {

	BNF<Term, NT> b = new BNFBuilder<>(Term.class, NT.class) //
		.setApiName("SqlFragment") //
		.derive(SELECT_STATEMENT).to(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES).and(WHERE_OPT) //
		.derive(QUANTIFIER).toOneOf(ALL).or(DISTINCT) //
		.derive(ALL).to(all)//
		.derive(DISTINCT).to(distinct)//
		.derive(COLOUMNS).to(column).and(COLOUMNS_OPT)//
		.derive(COLOUMNS_OPT).toOneOf(COLOUMNS).or(EPSILON)//
		.derive(TABLES).to(table).and(TABLES_OPT) //
		.derive(TABLES_OPT).toOneOf(TABLES).or(EPSILON) //
		.derive(WHERE_OPT).toOneOf(WHERE).or(EPSILON) //
		.derive(WHERE).to(where).and(EXPRESSION) //
		.derive(EXPRESSION).to(column).and(OP).and(LITERAL) //
		.derive(OP).toOneOf(EQUALS).or(GEQ).or(LEQ) //
		.derive(EQUALS).to(equals) //
		.derive(GEQ).to(geq) //
		.derive(LEQ).to(leq) //
		.derive(LITERAL).to(literal) //
		.derive(EPSILON).to(epsilon) //
		.finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }

}
