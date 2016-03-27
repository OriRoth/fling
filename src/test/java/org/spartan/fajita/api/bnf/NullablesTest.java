package org.spartan.fajita.api.bnf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class NullablesTest {
  private enum Term implements Terminal {
    a;
  }

  private enum NT implements NonTerminal {
    Nullable, Nullable2, A;
  }

  private BNFAnalyzer analyzer;

  @Before public void initialize() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(NT.A) //
        .derive(NT.Nullable).to(NT.A).orNone() //
        .derive(NT.Nullable2).to(NT.Nullable).or(NT.A) //
        .derive(NT.A).to(Term.a) //
        .go();
    analyzer = new BNFAnalyzer(bnf);
  }
  @Test public void testEpsilonNullable() {
    assertTrue(analyzer.isNullable(SpecialSymbols.epsilon));
  }
  @Test public void testDirectEpsilon() {
    assertTrue(analyzer.isNullable(NT.Nullable));
  }
  @Test public void testTermNotNullable() {
    assertFalse(analyzer.isNullable(Term.a));
  }
  @Test public void testNTNotNullable() {
    assertFalse(analyzer.isNullable(NT.A));
  }
  @Test public void testIndirectEpsilon() {
    assertTrue(analyzer.isNullable(NT.Nullable2));
  }
  @Test public void testNullableExpression() {
    assertTrue(analyzer.isNullable(NT.Nullable, NT.Nullable2));
  }
  @Test public void testNotNullableExpressionWithTerminal() {
    assertFalse(analyzer.isNullable(NT.Nullable, NT.Nullable2, Term.a));
  }
  @Test public void testNotNullableExpressionWithNonNullableNT() {
    assertFalse(analyzer.isNullable(NT.Nullable, NT.Nullable2, NT.A));
  }
}
