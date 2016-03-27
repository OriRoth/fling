package org.spartan.fajita.ll;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.ll.LLRecognizerTest.NT.F;
import static org.spartan.fajita.ll.LLRecognizerTest.NT.S;
import static org.spartan.fajita.ll.LLRecognizerTest.Term.lp;
import static org.spartan.fajita.ll.LLRecognizerTest.Term.plus;
import static org.spartan.fajita.ll.LLRecognizerTest.Term.rp;
import static org.spartan.fajita.ll.LLRecognizerTest.Term.x;

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
import org.spartan.fajita.api.ll.LLRecognizer;
import org.spartan.fajita.api.ll.generation.LLRecognizerGenerator;

@SuppressWarnings("static-method") public class LLRecognizerTest {
  private static BNF bnf;
  private static LLRecognizer ll;

  public static List<Verb> mapTerminals(Terminal... terminals) {
    return Arrays.asList(terminals).stream()
        .map(term -> bnf.getVerbs().stream().filter(verb -> term.name().equals(verb.name())).findAny().get()).collect(Collectors.toList());
  }

  static enum Term implements Terminal {
    lp, rp, plus, x;
  }

  static enum NT implements NonTerminal {
    F, S
  }

  @BeforeClass public static void init() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(F).to(x) //
        .derive(S).to(F) //
        .or(lp).and(S).and(plus).and(F).and(rp).go();
    ll = new LLRecognizer(bnf);
  }
  
  @BeforeClass public static void generationCode(){
    System.out.println(LLRecognizerGenerator.generate(bnf));
  }
  @Test public void test1() {
    List<Verb> input = mapTerminals(lp);
    assertFalse(ll.recognize(input));
  }
  @Test public void test2() {
    List<Verb> input = mapTerminals(lp, rp);
    assertFalse(ll.recognize(input));
  }
  @Test public void test3() {
    List<Verb> input = mapTerminals(x);
    assertTrue(ll.recognize(input));
  }
  @Test public void test4() {
    List<Verb> input = mapTerminals(lp, x, plus, x, rp);
    assertTrue(ll.recognize(input));
  }
  @Test public void test5() {
    List<Verb> input = mapTerminals(lp, lp, x, plus, x, rp, plus, x, rp);
    assertTrue(ll.recognize(input));
  }
}
