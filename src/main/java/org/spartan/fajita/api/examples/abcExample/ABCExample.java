package org.spartan.fajita.api.examples.abcExample;

import static org.spartan.fajita.api.examples.abcExample.ABCExample.NT.A;
import static org.spartan.fajita.api.examples.abcExample.ABCExample.NT.START;
import static org.spartan.fajita.api.examples.abcExample.ABCExample.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.examples.abcExample.states.Q0;
import org.spartan.fajita.api.examples.abcExample.states.Q1;
import org.spartan.fajita.api.examples.abcExample.states.Q2;
import org.spartan.fajita.api.examples.abcExample.states.Q3;
import org.spartan.fajita.api.examples.abcExample.states.Q4;
import org.spartan.fajita.api.examples.abcExample.states.Q5;
import org.spartan.fajita.api.parser.ActionTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ActionTable.ShiftReduceConflictException;
import org.spartan.fajita.api.parser.LRParser;

public class ABCExample {
  @SuppressWarnings("unused") public static void expressionBuilder() {
    Q0 q0 = new Q0();
    Q3<Q0, Q2<Q0, Q1<Q0>>, Q4<Q2<Q0, Q1<Q0>>, Q0, Q1<Q0>>> q3 = q0.a();
    Q4<Q2<Q0, Q1<Q0>>, Q0, Q1<Q0>> q4 = q3.b();
    Q5<Q4<Q2<Q0, Q1<Q0>>, Q0, Q1<Q0>>, Q0, Q1<Q0>> q5 = q4.c();
    String result = q5.$();
    new Q0().a().b().c().$();
  }

  static enum Term implements Terminal {
    a, b, c;
    final Type t;

    Term(final Class<?> clzz) {
      t = new Type(clzz);
    }
    Term() {
      t = Type.VOID;
    }
    @Override public Type type() {
      return t;
    }
  }

  static enum NT implements NonTerminal {
    START, A;
  }

  /**
   * @throws ShiftReduceConflictException
   * @throws ReduceReduceConflictException
   */
  public static void buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("Super basic example") //
        .setStartSymbols(START) //
        .endConfig() //
        .derive(START).to(A).and(b).and(c) //
        .derive(A).to(a) //
        .finish();
    System.out.println(bnf);
    LRParser parser = new LRParser(bnf);
    System.out.println(parser.states);
    System.out.println(parser);
  }
  public static void main(final String[] args) {
    buildBNF();
  }
}
