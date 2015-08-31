package org.spartan.fajita.api.parser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.ParsingTable.ReduceReduceConflictException;
import org.spartan.fajita.api.parser.ParsingTable.ShiftReduceConflictException;

public class StateGotoTest {
  private enum Term implements Terminal {
    a, b, c, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    S, A;
  }

  @SuppressWarnings("static-method") @Test public void testEmptyNextState()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S) //
        .endConfig() //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getInitialState();
    assertFalse(initialState.isLegalLookahead(Term.c));
    State nextState = parser.gotoTable(initialState, Term.c);
    assertEquals(nextState, null);
  }
  @SuppressWarnings("static-method") @Test public void testNextStateTerminalLookahead()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S) //
        .endConfig() //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getInitialState();
    assertTrue(initialState.isLegalLookahead(Term.a));
    State nextState = parser.gotoTable(initialState, Term.a);
    Item A_Rule = nextState.items.stream().filter(r -> r.rule.lhs.equals(NT.A)).findAny().get();
    assertEquals(1, A_Rule.dotIndex);
    assertEquals(nextState.items, new HashSet<>(Arrays.asList(A_Rule)));
  }
  @SuppressWarnings("static-method") @Test public void testNextStateNonTerminalLookahead()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S) //
        .endConfig() //
        .derive(NT.S).to(NT.A).and(Term.b) //
        .derive(NT.A).to(Term.a).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getInitialState();
    assertTrue(initialState.isLegalLookahead(NT.A));
    State nextState = parser.gotoTable(initialState, NT.A);
    Item S_Rule = nextState.items.stream().filter(r -> r.rule.lhs.equals(NT.S)).findAny().get();
    assertEquals(1, S_Rule.dotIndex);
    assertEquals(nextState.items, new HashSet<>(Arrays.asList(S_Rule)));
  }
  @SuppressWarnings("static-method") @Test public void testUsesClosureInNextState()
      throws ReduceReduceConflictException, ShiftReduceConflictException {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S) //
        .endConfig() //
        .derive(NT.S).to(Term.a).and(NT.A) //
        .derive(NT.A).to(Term.b).and(Term.c) //
        .finish();
    LRParser parser = new LRParser(bnf);
    State initialState = parser.getInitialState();
    State nextState = parser.gotoTable(initialState, Term.a);
    assertEquals(2, nextState.items.size());
  }
}
