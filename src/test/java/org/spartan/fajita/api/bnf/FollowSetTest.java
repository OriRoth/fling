package org.spartan.fajita.api.bnf;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.api.bnf.TestUtils.expectedSet;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

@SuppressWarnings("static-method") //
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
  }
  @Test public void testStartFollowedBy$() {
    assertThat(expectedSet(Terminal.$), equalTo(bnf.followSetOf(NT.S)));
  }
  @Test public void testMultipleStartsFollowedBy$() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .startConfig() //
        .setApiNameTo("TEST") //
        .setStartSymbols(NT.S, NT.A) //
        .endConfig() //
        .derive(NT.S).to(NT.B).or().to(NT.AB) //
        .derive(NT.A).to(NT.C).or().to(NT.AB) //
        .derive(NT.B).to(Term.b)//
        .derive(NT.AB).to(NT.A).and(NT.B) //
        .derive(NT.C).to(Term.c)//
        .derive(NT.UNREACHABLE).to(Term.d) //
        .finish();
    assertThat(expectedSet(Terminal.$), equalTo(b.followSetOf(NT.S)));
    assertTrue(b.followSetOf(NT.A).contains(Terminal.$));
  }
  @Test public void testBasicFollow() {
    assertTrue(bnf.followSetOf(NT.A).contains(Term.b));
  }
  @Test public void testNTFolloweByNullableContainsLhsFollow() {
    assertTrue(bnf.followSetOf(NT.C).contains(Terminal.$));
  }
  @Test public void testEndOfExpressionContainsLhsFollow() {
    assertTrue(bnf.followSetOf(NT.A).contains(Terminal.$));
  }
  @Test public void testUnreachableNT() {
    assertEquals(expectedSet(), bnf.followSetOf(NT.UNREACHABLE));
  }
}
