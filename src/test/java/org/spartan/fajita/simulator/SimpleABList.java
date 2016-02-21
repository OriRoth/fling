package org.spartan.fajita.simulator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.simulator.SimpleABList.NT.A;
import static org.spartan.fajita.simulator.SimpleABList.NT.B;
import static org.spartan.fajita.simulator.SimpleABList.NT.S;
import static org.spartan.fajita.simulator.SimpleABList.Term.a;
import static org.spartan.fajita.simulator.SimpleABList.Term.b;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.jlr.JLRRecognizer;
import org.spartan.fajita.api.jlr.simulator.JLRSimulator;

@SuppressWarnings("static-method") public class SimpleABList {
  enum Term implements Terminal {
    a, b, c
  }

  enum NT implements NonTerminal {
    A, S, B, C
  }

  private static JLRRecognizer jlr;

  @BeforeClass public static void abList() {
    System.out.println("Testing regular language : a+b+ ");
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(B) //
        .derive(A).to(A).and(a).or(a) //
        .derive(B).to(B).and(b).or(b)//
        .finish();
    jlr = new JLRRecognizer(bnf);
    Main.lrAutomatonVisualisation(bnf);
  }
  @Test public void testEmpty() {
    testRejects("");
  }
  @Test public void test_b() {
    testRejects("b");
  }
  @Test public void test_a() {
    testRejects("a");
  }
  @Test public void test_ab() {
    testAccepts("ab");
  }
  @Test public void testLong() {
    testAccepts("aaaaabbbbbbbbbbbbbbbb");
  }
  @Test public void test_repeating_seq() {
    testRejects("aaaaaaabbbbbbaaaaaabbbbbb");
  }
  public static void testRejects(String input) {
    System.out.println("Testing "+input+"...");
    assertFalse(JLRSimulator.runJLR(jlr, input));
  }
  public static void testAccepts(String input) {
    System.out.println("Testing "+input+"...");
    assertTrue(JLRSimulator.runJLR(jlr, input));
  }
}
