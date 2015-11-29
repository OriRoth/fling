package org.spartan.fajita.api.parser;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.ActionTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ActionTable.ShiftReduceConflictException;

public class StatesSetTest {
  private List<State> statesSet;
  private BNF bnf;

  private enum Term implements Terminal {
    a, b, c, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    S, A;
  }

  @Before public void initializeStates() throws ReduceReduceConflictException, ShiftReduceConflictException {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig()//
        .setApiNameTo("TEST")//
        .start(NT.S)//
        .endConfig() //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a) //
        .finish();
    statesSet = new LRParser(bnf).getStates();
  }
  @Test public void testNumberOfStates() {
    assertEquals(5, statesSet.size());
  }
}
