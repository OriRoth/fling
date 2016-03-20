package org.spartan.fajita.sll;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.ll.LLtest.NT.F;
import static org.spartan.fajita.ll.LLtest.NT.S;
import static org.spartan.fajita.ll.LLtest.Term.lp;
import static org.spartan.fajita.ll.LLtest.Term.plus;
import static org.spartan.fajita.ll.LLtest.Term.rp;
import static org.spartan.fajita.ll.LLtest.Term.x;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.ll.LLParser;
import org.spartan.fajita.api.sll.SLLParser;

@SuppressWarnings("static-method") public class SLLtest {
  private static BNF bnf;
  private static SLLParser sll;

  public static List<Verb> mapTerminals(Terminal... terminals) {
    return Arrays.asList(terminals).stream()
        .map(term -> bnf.getVerbs().stream().filter(verb -> term.name().equals(verb.name())).findAny().get()).collect(Collectors.toList());
  }

  static enum Term implements Terminal {
    b,c
  }

  static enum NT implements NonTerminal {
    S,A,B,C
  }

  @BeforeClass public static void init() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(F).to(x) //
        .derive(S).to(F) //
        .or(lp).and(S).and(plus).and(F).and(rp).finish();
    sll = new LLParser(bnf);
  }
  @Test public void test1() {
    List<Verb> input = mapTerminals(lp);
    assertFalse(sll.parse(input));
  }
  @Test public void test2() {
    List<Verb> input = mapTerminals(lp, rp);
    assertFalse(sll.parse(input));
  }
  @Test public void test3() {
    List<Verb> input = mapTerminals(x);
    assertTrue(sll.parse(input));
  }
  @Test public void test4() {
    List<Verb> input = mapTerminals(lp, x, plus, x, rp);
    assertTrue(sll.parse(input));
  }
  @Test public void test5() {
    List<Verb> input = mapTerminals(lp, lp, x, plus, x, rp, plus, x, rp);
    assertTrue(sll.parse(input));
  }
}
