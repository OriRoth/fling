package org.spartan.fajita.simulator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.simulator.EmptyLanguageTest.NT.A;
import static org.spartan.fajita.simulator.EmptyLanguageTest.NT.B;
import static org.spartan.fajita.simulator.EmptyLanguageTest.NT.C;
import static org.spartan.fajita.simulator.EmptyLanguageTest.NT.S;
import static org.spartan.fajita.simulator.EmptyLanguageTest.Term.a;
import static org.spartan.fajita.simulator.EmptyLanguageTest.Term.b;
import static org.spartan.fajita.simulator.EmptyLanguageTest.Term.c;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.jlr.JLRRecognizer;
import org.spartan.fajita.api.jlr.simulator.JLRSimulator;

@SuppressWarnings("static-method") public class EmptyLanguageTest {
  enum Term implements Terminal {
    a, b, c
  }

  enum NT implements NonTerminal {
    A, S, B, C
  }

  private static JLRRecognizer jlr;

  @BeforeClass public static void emptyLanguage() {
    System.out.println("Testing regular language : ε ");
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).toNone() //
        .derive(A).to(a) //
        .derive(B).to(b) //
        .derive(C).to(c) //
        .go();
    jlr = new JLRRecognizer(bnf);
    Main.lrAutomatonVisualisation(bnf);
  }
  @Test public void testEmpty() {
    testAccepts("");
  }
  @Test public void testShort() {
    testRejects("a");
  }
  @Test public void testMedium() {
    testRejects("abcc");
  }
  @Test public void testLong() {
    testRejects("cbccacbcabcba");
  }
  @Test public void testVeryLong() {
    testRejects("bbccacbcabcbabcabcabcbabbcbababcbbacbbcaba");
  }
  public static void testRejects(String input) {
    assertFalse(JLRSimulator.runJLR(jlr, input));
  }
  public static void testAccepts(String input) {
    assertTrue(JLRSimulator.runJLR(jlr, input));
  }
}
