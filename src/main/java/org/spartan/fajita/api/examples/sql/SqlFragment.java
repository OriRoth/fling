package org.spartan.fajita.api.examples.sql;

import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.COLOUMNS;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.EXPRESSION;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.OP;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.QUANTIFIER;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.SELECT_STATEMENT;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.TABLES;
import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.WHERE;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.all;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.column;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.distinct;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.equals;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.from;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.geq;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.leq;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.literal;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.select;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.table;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.where;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SqlFragment {
  enum Term implements Terminal {
    select, column, from, all, distinct, table, where, equals, geq, leq, literal;
  }

  static enum NT implements NonTerminal {
    SELECT_STATEMENT, QUANTIFIER, COLOUMNS, TABLES, WHERE, EXPRESSION, OP;
  }

  public static void buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(SELECT_STATEMENT) //
        //
        .derive(SELECT_STATEMENT).to(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES).and(WHERE) //
        /*                */.or(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES) //
        .derive(QUANTIFIER).to(all).or(distinct) //
        .derive(COLOUMNS).to(column).or(column).and(COLOUMNS)//
        .derive(TABLES).to(table).or(table).and(TABLES) //
        .derive(WHERE).to(where).and(EXPRESSION) //
        .derive(EXPRESSION).to(column).and(OP).and(literal) //
        .derive(OP).to(equals).or(geq).or(leq) //
        .finish();
    System.out.println(b);
  }
}
