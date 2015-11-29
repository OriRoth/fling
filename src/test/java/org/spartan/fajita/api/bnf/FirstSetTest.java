package org.spartan.fajita.api.bnf;

import static org.junit.Assert.*;
import static org.spartan.fajita.api.bnf.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.LRParser;

public class FirstSetTest {
  private enum Term implements Terminal {
    a, b, c, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    A, B, AB, C, D, REDUNDANT;
  }

  private BNF bnf;
  private LRParser parser;

  @Before public void initialize() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .start(NT.A) //
        .endConfig() //
        .derive(NT.A).to(Term.a) //
        .derive(NT.B).to(Term.b) //
        .derive(NT.AB).to(Term.a).or().to(Term.b) //
        .derive(NT.C).to(NT.AB) //
        .derive(NT.D).to(Term.d) //
        .derive(NT.REDUNDANT).to(NT.REDUNDANT) //
        .finish();
    parser = new LRParser(bnf);
  }
  @Test public void testTerminal() {
    assertEquals(expectedSet(Term.a), parser.firstSetOf(Term.a));
  }
  @Test public void testNT() {
    assertEquals(expectedSet(Term.a), parser.firstSetOf(NT.A));
  }
  @Test public void testInheritedNT() {
    assertEquals(expectedSet(Term.a, Term.b), parser.firstSetOf(NT.AB));
  }
  @Test public void testExpressionWithNoNullables() {
    assertEquals(expectedSet(Term.a), parser.firstSetOf(NT.A, NT.B));
  }
  @Test public void testRedundantNT() {
    assertTrue(parser.firstSetOf(NT.REDUNDANT).isEmpty());
  }
}
