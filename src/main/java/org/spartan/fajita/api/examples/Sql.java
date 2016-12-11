package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Sql.NT.*;
import static org.spartan.fajita.api.examples.Sql.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Sql {
  enum Term implements Terminal {
    select, column, from, all, distinct, table, where, equals, geq, leq, literal;
  }

  static enum NT implements NonTerminal {
    SELECT_STATEMENT, QUANTIFIER, COLOUMNS, WHERE, EXPRESSION, OP;
  }

  public static BNF buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(SELECT_STATEMENT) //
        //
        .derive(SELECT_STATEMENT)//
        .to(select).and(QUANTIFIER).and(column).and(COLOUMNS).and(from).and(table).and(WHERE) //
        .derive(QUANTIFIER).to(all).or(distinct) //
        .derive(COLOUMNS).to(column).and(COLOUMNS)//
        .orNone()//
        .derive(WHERE).to(where).and(EXPRESSION) //
        .orNone() //
        .derive(EXPRESSION).to(column).and(OP).and(literal) //
        .derive(OP).to(equals).or(geq).or(leq) //
        .go();
    return b;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
//  void compiles(augS_0_32a x) {
//    x.select().all().column().from().table().$();
//    x.select().distinct().column().column().column().column().from().table().where().column().geq().literal().$();
//    x.select().all().column().column().column().from().table().$();
//    x.select().all().column().from().table().where().column().equals().literal().$();
//  }
//  void doesnt_compile(augS_0_32a x) {
//    x.all().$();
//    x.from().$();
//    x.select().from().$();
//    x.select().all().column().from().$();
//  }
}
