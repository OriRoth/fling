package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Sql.NT.*;

import static org.spartan.fajita.api.examples.Sql.Term.*;
import static org.spartan.fajita.api.junk.Sql.select;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Sql {
  private static final String apiName = "Sql";

  enum Term implements Terminal {
    select, column, from, all, distinct, table, where, equals, geq, leq, literal;
  }

  static enum NT implements NonTerminal {
    STMNT, QUANTIFIER, COLOUMNS, WHERE, EXPRESSION, OP;
  }

  public static String buildBNF() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
        .start(STMNT) //
        .derive(STMNT)//
        .to(select).and(QUANTIFIER).and(column).and(COLOUMNS).and(from).and(table).and(WHERE) //
        .derive(QUANTIFIER).to(all).or(distinct) //
        .derive(COLOUMNS).to(column).and(COLOUMNS)//
        .orNone()//
        .derive(WHERE).to(where).and(EXPRESSION) //
        .orNone() //
        .derive(EXPRESSION).to(column).and(OP).and(literal) //
        .derive(OP).to(equals).or(geq).or(leq) //
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildBNF());
  }
  void compiles() {
    select().all().column().from().table().$();
    select().distinct().column().column().column().column().from().table().where().column().geq().literal().$();
    select().all().column().column().column().from().table().$();
    select().all().column().from().table().where().column().equals().literal().$();
  }
  void doesnt_compile() {
    all().$();
    from().$();
    select().from().$();
    select().all().column().from().$();
  }
}
