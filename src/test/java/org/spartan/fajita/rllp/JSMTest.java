package org.spartan.fajita.rllp;

import static org.junit.Assert.assertEquals;
import static org.spartan.fajita.rllp.JSMTest.NT.*;
import static org.spartan.fajita.rllp.JSMTest.Term.*;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;

public class JSMTest {
  private static BNF bnf;
  private static RLLP rllp;
  private JSM jsm;

  static enum Term implements Terminal {
    a, b, c, d;
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  @BeforeClass public static void initBNF() {
    bnf = new Fajita(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(D) //
        .derive(A).to(B) //
        .derive(B).to(C).and(b) //
        .derive(C).to(c).orNone() //
        .derive(D).to(d) //
        .go();
    rllp = new RLLP(bnf);
  }
  @Before public void initJSM() {
    final Set<Verb> verbs = bnf.getVerbs();
    jsm = new JSM(verbs, rllp.jumpsTable);
  }
  @Test(expected = IllegalStateException.class) public void testIllegal() {
    jsm.jump(bnf.getVerbs().iterator().next());
  }
  @Test public void testTrivialPush() {
    Item S0item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 0);
    jsm.push(S0item);
    assertEquals(S0item, jsm.peek());
  }
  @Test(expected = IllegalStateException.class) public void testTrivialPop() {
    Item S0item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 0);
    jsm.push(S0item);
    jsm.pop();
    jsm.jump(new Verb(d));
  }
  @Test public void testTrivialJump() {
    Item S0item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 0);
    Item S1item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 1);
    Item D1item = getAnyMatchingItem(i -> i.rule.lhs == D && i.dotIndex == 1);
    jsm.push(S0item);
    jsm.jump(new Verb(d));
    assertEquals(D1item, jsm.pop());
    assertEquals(S1item, jsm.pop());
  }
  @Test public void testDoubleJump() {
    // initialize JSM state
    Item S0item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 0);
    rllp.consolidate(S0item, new Verb(c)).descendingIterator().forEachRemaining(i -> jsm.push(i));
    // test first jump
    Item D1item = getAnyMatchingItem(i -> i.rule.lhs == D && i.dotIndex == 1);
    Item B2item = getAnyMatchingItem(i -> i.rule.lhs == B && i.dotIndex == 2);
    jsm.jump(new Verb(b));
    assertEquals(B2item, jsm.peek());
    // test second jump
    Item S1item = getAnyMatchingItem(i -> i.rule.lhs == S && i.dotIndex == 1);
    jsm.jump(new Verb(d));
    assertEquals(D1item, jsm.pop());
    assertEquals(S1item, jsm.pop());
  }
  static Item getAnyMatchingItem(Predicate<Item> predicate) {
    return rllp.items.stream().filter(predicate).findAny().get();
  }
}
