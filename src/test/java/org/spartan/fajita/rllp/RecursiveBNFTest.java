package org.spartan.fajita.rllp;

import static org.spartan.fajita.rllp.RecursiveBNFTest.NT.A;
import static org.spartan.fajita.rllp.RecursiveBNFTest.Term.a;
import static org.spartan.fajita.rllp.RecursiveBNFTest.Term.b;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;

public class RecursiveBNFTest {
  private static BNF recursiveBNF;
  private static RLLP rllp;
  private JSM jsm;
  public Rule rule;

  static enum Term implements Terminal {
    a,b;
  }

  static enum NT implements NonTerminal {
    A
  }

  @BeforeClass public static void initBNF() {
    recursiveBNF = new BNFBuilder(Term.class, NT.class) //
        .start(A) //
        .derive(A).to(a).and(A) //
        /********/
        .or(b).go();
    rllp = new RLLP(recursiveBNF);
  }
  @Before public void initJSM() {
    final Set<Verb> verbs = recursiveBNF.getVerbs();
    jsm = new JSM(verbs, rllp.jumpsTable);
  }
  @Test(timeout = 1000) public void testNoInfiniteRecursion() {
    Item i1 = getAnyMatchingItem(i -> i.rule.lhs.equals(A) && i.dotIndex == 1);
    List<Item> toPush = Arrays.asList(i1);
    toPush.forEach(i -> jsm.push(i));
  }
  static Item getAnyMatchingItem(Predicate<Item> predicate) {
    return rllp.items.stream().filter(predicate).findAny().get();
  }
}
