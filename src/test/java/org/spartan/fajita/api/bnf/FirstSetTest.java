package org.spartan.fajita.api.bnf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class FirstSetTest {
  private enum Term implements Terminal {
    a, b, c, d;
  }

  private enum NT implements NonTerminal {
    A, B, AB, C, D, REDUNDANT;
  }

  private BNF bnf;
  private BNFAnalyzer analyzer;

  @Before public void initialize() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.A) //
        .derive(NT.A).to(Term.a) //
        .derive(NT.B).to(Term.b) //
        .derive(NT.AB).to(Term.a).or(Term.b) //
        .derive(NT.C).to(NT.AB) //
        .derive(NT.D).to(Term.d) //
        .derive(NT.REDUNDANT).to(NT.REDUNDANT) //
        .go();
    analyzer = new BNFAnalyzer(bnf);
  }
  @Test public void testNT() {
    assertEquals(Term.a.name(), analyzer.firstSetOf(NT.A).iterator().next().name());
  }
  @Test public void testExpressionWithNoNullables() {
    assertEquals(Term.a.name(), analyzer.firstSetOf(NT.A, NT.B).iterator().next().name());
  }
  @Test public void testRedundantNT() {
    assertTrue(analyzer.firstSetOf(NT.REDUNDANT).isEmpty());
  }
}
