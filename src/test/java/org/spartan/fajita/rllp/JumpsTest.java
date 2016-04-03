package org.spartan.fajita.rllp;

import static org.junit.Assert.*;
import static org.spartan.fajita.rllp.JumpsTest.NT.*;
import static org.spartan.fajita.rllp.JumpsTest.Term.*;

import java.util.Deque;
import java.util.function.Predicate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.RLLP;

@SuppressWarnings("static-method") public class JumpsTest {
  static enum Term implements Terminal {
    a, b, c, d;
  }

  static enum NT implements NonTerminal {
    S, A, B, C, D
  }

  private static BNF bnf;
  private static RLLP rllp;

  @BeforeClass public static void init() {
    bnf = new BNFBuilder(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(B).and(C).and(D) //
        .derive(A).to(a)//
        .derive(B).to(b).orNone() //
        .derive(C).to(c).orNone() //
        .derive(D).to(d) //
        .go();
    rllp = new RLLP(bnf);
  }
  @Test public void testWideJump() {
    Item Sitem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 0);
    Item Sitem3 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 3);
    Item Ditem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 1);
    Deque<Item> jumps = rllp.jumps(Sitem0, new Verb(d));
    assertEquals(Ditem1, jumps.removeFirst());
    assertEquals(Sitem3, jumps.removeFirst());
    assertTrue(jumps.isEmpty());
  }
  @Test public void testNarrowJump() {
    Item Sitem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 0);
    Item Sitem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 1);
    Item Bitem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == B && i.dotIndex == 1);
    Deque<Item> jumps = rllp.jumps(Sitem0, new Verb(b));
    assertEquals(Bitem1, jumps.removeFirst());
    assertEquals(Sitem1, jumps.removeFirst());
    assertTrue(jumps.isEmpty());
  }
  @Test(expected = IllegalStateException.class) public void testNoJumps() {
    Item Aitem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == A && i.dotIndex == 0);
    rllp.jumps(Aitem0, new Verb(a));
  }
  static Item getAnyMatchingItem(RLLP parser, Predicate<Item> predicate) {
    return parser.items.stream().filter(predicate).findAny().get();
  }
}
