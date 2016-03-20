package org.spartan.fajita.sll;

import static org.junit.Assert.assertEquals;
import static org.spartan.fajita.sll.SLLtest.NT.A;
import static org.spartan.fajita.sll.SLLtest.NT.B;
import static org.spartan.fajita.sll.SLLtest.NT.C;
import static org.spartan.fajita.sll.SLLtest.NT.D;
import static org.spartan.fajita.sll.SLLtest.NT.S;
import static org.spartan.fajita.sll.SLLtest.Term.c;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.sll.Item;
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
    S,A,B,C,D
  }

  @BeforeClass public static void init() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(B).and(C) //
        .derive(A).to(B) //
//        .derive(B).toNone().or(b) //
        .derive(B).to(D) //
        .derive(C).to(c) //
        .derive(D).toNone() //
        .finish();
    sll = new SLLParser(bnf);
  }
  @Test public void test() {
    List<Verb> input = mapTerminals(c);
    DerivationRule sRule = bnf.getRulesOf(S).get(0);
    DerivationRule cRule = bnf.getRulesOf(C).get(0);

    Stack<Item> result = sll.closure(new Item(sRule,0), input.get(0));
    Item firstPop = result.pop();
    assertEquals(new Item(cRule,1),firstPop);
    Item secondPop = result.pop();
    assertEquals(new Item(sRule,3),secondPop);
  }
}
