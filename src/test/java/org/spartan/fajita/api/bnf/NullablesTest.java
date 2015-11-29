package org.spartan.fajita.api.bnf;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;
import org.spartan.fajita.api.parser.LRParser;

public class NullablesTest {
  private enum Term implements Terminal {
    a;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  private enum NT implements NonTerminal {
    Nullable, Nullable2, A;
  }

  private LRParser parser;

  @Before public void initialize() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.A) //
        .derive(NT.Nullable).to(SpecialSymbols.epsilon).or().to(NT.A) //
        .derive(NT.Nullable2).to(NT.Nullable).or().to(NT.A) //
        .derive(NT.A).to(Term.a) //
        .finish();
    parser = new LRParser(bnf);
  }
  @Test public void testEpsilonNullable() {
    assertTrue(parser.isNullable(SpecialSymbols.epsilon));
  }
  @Test public void testDirectEpsilon() {
    assertTrue(parser.isNullable(NT.Nullable));
  }
  @Test public void testTermNotNullable() {
    assertFalse(parser.isNullable(Term.a));
  }
  @Test public void testNTNotNullable() {
    assertFalse(parser.isNullable(NT.A));
  }
  @Test public void testIndirectEpsilon() {
    assertTrue(parser.isNullable(NT.Nullable2));
  }
  @Test public void testNullableExpression() {
    assertTrue(parser.isNullable(NT.Nullable, NT.Nullable2));
  }
  @Test public void testNotNullableExpressionWithTerminal() {
    assertFalse(parser.isNullable(NT.Nullable, NT.Nullable2, Term.a));
  }
  @Test public void testNotNullableExpressionWithNonNullableNT() {
    assertFalse(parser.isNullable(NT.Nullable, NT.Nullable2, NT.A));
  }
}
