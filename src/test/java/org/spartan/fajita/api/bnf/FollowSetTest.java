package org.spartan.fajita.api.bnf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.api.bnf.TestUtils.expectedSet;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class FollowSetTest {
  private enum Term implements Terminal {
    a, b, c, d;
  }

  private enum NT implements NonTerminal {
    S, A, B, AB, C, UNREACHABLE;
  }

  private BNF bnf;
  private BNFAnalyzer analyzer;

  @Before public void initialize() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S) //
        .derive(NT.S).to(NT.A).or(NT.B).or(NT.AB).or(NT.C) //
        .derive(NT.A).to(Term.a) //
        .derive(NT.B).to(Term.b)//
        .derive(NT.AB).to(NT.A).and(NT.B) //
        .derive(NT.C).to(Term.c) //
        .derive(NT.UNREACHABLE).to(Term.d) //
        .go();
    analyzer = new BNFAnalyzer(bnf);
  }
  @Test public void testStartFollowedBy$() {
    assertThat(expectedSet(SpecialSymbols.$), equalTo(analyzer.followSetOf(NT.S)));
  }
  @Test public void testMultipleStartsFollowedBy$() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(NT.S, NT.A) //
        .derive(NT.S).to(NT.B).or(NT.AB) //
        .derive(NT.A).to(NT.C) //
        .derive(NT.B).to(Term.b)//
        .derive(NT.AB).to(NT.A).and(NT.B) //
        .derive(NT.C).to(Term.c)//
        .derive(NT.UNREACHABLE).to(NT.UNREACHABLE) //
        .go();
    analyzer = new BNFAnalyzer(b);
    assertThat(expectedSet(SpecialSymbols.$), equalTo(analyzer.followSetOf(NT.S)));
    assertTrue(analyzer.followSetOf(NT.A).contains(SpecialSymbols.$));
  }
  @Test public void testBasicFollow() {
    assertTrue(analyzer.followSetOf(NT.A).contains(Term.b));
  }
  @Test public void testNTFolloweByNullableContainsLhsFollow() {
    assertTrue(analyzer.followSetOf(NT.C).contains(SpecialSymbols.$));
  }
  @Test public void testEndOfExpressionContainsLhsFollow() {
    assertTrue(analyzer.followSetOf(NT.A).contains(SpecialSymbols.$));
  }
  @Test public void testUnreachableNT() {
    assertEquals(expectedSet(), analyzer.followSetOf(NT.UNREACHABLE));
  }
}
