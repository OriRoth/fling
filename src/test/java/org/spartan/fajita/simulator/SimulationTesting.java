package org.spartan.fajita.simulator;

import static org.junit.Assert.assertEquals;
import static org.spartan.fajita.simulator.SimulationTesting.NT.A;
import static org.spartan.fajita.simulator.SimulationTesting.NT.B;
import static org.spartan.fajita.simulator.SimulationTesting.NT.C;
import static org.spartan.fajita.simulator.SimulationTesting.NT.S;
import static org.spartan.fajita.simulator.SimulationTesting.Term.a;
import static org.spartan.fajita.simulator.SimulationTesting.Term.b;
import static org.spartan.fajita.simulator.SimulationTesting.Term.c;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.jlr.JLRRecognizer;
import org.spartan.fajita.api.jlr.simulator.JLRSimulator;

@SuppressWarnings("static-method") public class SimulationTesting {
  enum Term implements Terminal {
    a, b, c
  }

  enum NT implements NonTerminal {
    A, S, B, C
  }

  private static JLRRecognizer ab_list_jlr;

  public void emptyLanguage() {
    System.out.println("Testing regular language : ε ");
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).toNone() //
        .derive(A).to(a) //
        .derive(B).to(b) //
        .derive(C).to(c) //
        .finish();
    JLRRecognizer jlr = new JLRRecognizer(bnf);
    test(jlr, "ab", false);
    test(jlr, "a", false);
    test(jlr, "", true);
  }
  @BeforeClass public static void ab_list() {
    System.out.println("Testing regular language: a*b*");
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(A) //
        .derive(S).to(A).and(B) //
        .derive(B).to(B).and(b).orNone() // Left recursive
        .derive(A).to(a).and(A).orNone() // Right recursive
        // .derive(C).to(c) //
        .finish();
    ab_list_jlr = new JLRRecognizer(bnf);
    Main.lrAutomatonVisualisation(bnf);
  }
  @Test public void epsilon() {
    test_ab( "", true);
  }
  @Test public void single_a() {
    test_ab( "a", true);
  }
  @Test public void single_b() {
    test_ab( "b", true);
  }
  @Test public void ab() {
    test_ab( "ab", true);
  }
  @Test public void illegal_ba() {
    test_ab( "ba", false);
  }
  @Test public void b_sequence() {
    test_ab( "bbbbbbbbbbbbbbbb", true);
  }
  @Test public void a_sequence() {
    test_ab( "aaaaaaaaa", true);
  }
  @Test public void long_ab() {
    test_ab( "aaaaaaaabbbbbbbbbbbbbbbb", true);
  }
  public static void test_ab(String input, boolean expected) {
    test(ab_list_jlr, input, expected);
  }
  public static void test(JLRRecognizer jlr, String input, boolean expected) {
    assertEquals(JLRSimulator.runJLR(jlr, input) ,  expected);
  }
}
