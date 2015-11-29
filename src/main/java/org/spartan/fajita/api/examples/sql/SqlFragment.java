package org.spartan.fajita.api.examples.sql;

import static org.spartan.fajita.api.examples.sql.SqlFragment.NT.*;
import static org.spartan.fajita.api.examples.sql.SqlFragment.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class SqlFragment {
  enum Term implements Terminal {
    select, column, from, all, distinct, table, where, equals, geq, leq, literal;
    @Override public Type type() {
      return new Type();
    }
    @Override public String toString() {
      return methodSignatureString();
    }
  }

  static enum NT implements NonTerminal {
    SELECT_STATEMENT, QUANTIFIER, COLOUMNS, TABLES, WHERE, EXPRESSION, OP;
  }

  public static void buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(SELECT_STATEMENT) //
        //
        .derive(SELECT_STATEMENT).to(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES).and(WHERE) //
        /*                */.or().to(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES) //
        .derive(QUANTIFIER).to(all).or().to(distinct) //
        .derive(COLOUMNS).to(column).or().to(column).and(COLOUMNS)//
        .derive(TABLES).to(table).or().to(table).and(TABLES) //
        .derive(WHERE).to(where).and(EXPRESSION) //
        .derive(EXPRESSION).to(column).and(OP).and(literal) //
        .derive(OP).to(equals).or().to(geq).or().to(leq) //
        .finish();
    System.out.println(b);
  }
}
