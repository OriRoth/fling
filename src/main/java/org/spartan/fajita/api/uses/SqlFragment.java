package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.SqlFragment.NT.*;
import static org.spartan.fajita.api.uses.SqlFragment.Term.*;

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
    SELECT_STATEMENT, QUANTIFIER, ALL, DISTINCT, COLOUMNS, COLOUMNS_OPT, TABLES, TABLES_OPT, WHERE_OPT, WHERE, EXPRESSION, OP, EQUALS, GEQ, LEQ, LITERAL;
  }

  public static void buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("SqlFragment") //
        .setStartSymbols(SELECT_STATEMENT) //
        .endConfig() //
        //
        .derive(SELECT_STATEMENT).to(select).and(QUANTIFIER).and(COLOUMNS).and(from).and(TABLES).and(WHERE_OPT) //
        .derive(QUANTIFIER).to(ALL).or(DISTINCT) //
        .derive(ALL).to(all)//
        .derive(DISTINCT).to(distinct)//
        .derive(COLOUMNS).to(column).and(COLOUMNS_OPT)//
        .derive(COLOUMNS_OPT).to(COLOUMNS).or(NonTerminal.EPSILON)//
        .derive(TABLES).to(table).and(TABLES_OPT) //
        .derive(TABLES_OPT).to(TABLES).or(NonTerminal.EPSILON) //
        .derive(WHERE_OPT).to(WHERE).or(NonTerminal.EPSILON) //
        .derive(WHERE).to(where).and(EXPRESSION) //
        .derive(EXPRESSION).to(column).and(OP).and(LITERAL) //
        .derive(OP).to(EQUALS).or(GEQ).or(LEQ) //
        .derive(EQUALS).to(equals) //
        .derive(GEQ).to(geq) //
        .derive(LEQ).to(leq) //
        .derive(LITERAL).to(literal) //
        .finish();
    System.out.println(b);
  }
  public static void main(final String[] args) {
    buildBNF();
  }
}
