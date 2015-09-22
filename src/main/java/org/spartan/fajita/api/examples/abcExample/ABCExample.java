package org.spartan.fajita.api.examples.abcExample;

import static org.spartan.fajita.api.examples.abcExample.ABCExample.NT.A;
import static org.spartan.fajita.api.examples.abcExample.ABCExample.NT.START;
import static org.spartan.fajita.api.examples.abcExample.ABCExample.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.ActionTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ActionTable.ShiftReduceConflictException;
import org.spartan.fajita.api.parser.LRParser;

public class ABCExample {
  @SuppressWarnings("unused") public static void expressionBuilder() {
    //
  }

  public static enum Term implements Terminal {
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

  public static enum NT implements NonTerminal {
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
