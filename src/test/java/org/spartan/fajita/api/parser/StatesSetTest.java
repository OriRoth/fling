package org.spartan.fajita.api.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.ParsingTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ParsingTable.ShiftReduceConflictException;

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
        .setStartSymbols(NT.S)//
        .endConfig() //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a) //
        .finish();
    statesSet = new LRParser(bnf).states;
  }
  @Test public void testNumberOfStates() {
    assertEquals(6, statesSet.size());
  }
  @Test public void testHasAcceptState() {
    assertTrue(statesSet.contains(new AcceptState(bnf, 0)));
  }
}
