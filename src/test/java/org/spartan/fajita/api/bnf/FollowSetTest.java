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
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.LRParser;

public class FollowSetTest {
  private enum Term implements Terminal {
    a, b, c, d;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    S, A, B, AB, C, UNREACHABLE;
  }

  private BNF bnf;
  private LRParser parser;

  @Before public void initialize() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S) //
        .endConfig() //
        .derive(NT.S).to(NT.A).or().to(NT.B).or().to(NT.AB).or().to(NT.C) //
        .derive(NT.A).to(Term.a) //
        .derive(NT.B).to(Term.b)//
        .derive(NT.AB).to(NT.A).and(NT.B) //
        .derive(NT.C).to(Term.c) //
        .derive(NT.UNREACHABLE).to(Term.d) //
        .finish();
    parser = new LRParser(bnf);
  }
  @Test public void testStartFollowedBy$() {
    assertThat(expectedSet(SpecialSymbols.$), equalTo(parser.followSetOf(NT.S)));
  }
  @Test public void testMultipleStartsFollowedBy$() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S, NT.A) //
        .endConfig() //
        .derive(NT.S).to(NT.B).or().to(NT.AB) //
        .derive(NT.A).to(NT.C) //
        .derive(NT.B).to(Term.b)//
        .derive(NT.AB).to(NT.A).and(NT.B) //
        .derive(NT.C).to(Term.c)//
        .derive(NT.UNREACHABLE).to(NT.UNREACHABLE) //
        .finish();
    parser = new LRParser(b);
    assertThat(expectedSet(SpecialSymbols.$), equalTo(parser.followSetOf(NT.S)));
    assertTrue(parser.followSetOf(NT.A).contains(SpecialSymbols.$));
  }
  @Test public void testBasicFollow() {
    assertTrue(parser.followSetOf(NT.A).contains(Term.b));
  }
  @Test public void testNTFolloweByNullableContainsLhsFollow() {
    assertTrue(parser.followSetOf(NT.C).contains(SpecialSymbols.$));
  }
  @Test public void testEndOfExpressionContainsLhsFollow() {
    assertTrue(parser.followSetOf(NT.A).contains(SpecialSymbols.$));
  }
  @Test public void testUnreachableNT() {
    assertEquals(expectedSet(), parser.followSetOf(NT.UNREACHABLE));
  }
}
