package org.spartan.fajita.rllp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.rllp.JumpsTest.NT.A;
import static org.spartan.fajita.rllp.JumpsTest.NT.B;
import static org.spartan.fajita.rllp.JumpsTest.NT.C;
import static org.spartan.fajita.rllp.JumpsTest.NT.D;
import static org.spartan.fajita.rllp.JumpsTest.NT.S;
import static org.spartan.fajita.rllp.JumpsTest.Term.a;
import static org.spartan.fajita.rllp.JumpsTest.Term.b;
import static org.spartan.fajita.rllp.JumpsTest.Term.c;
import static org.spartan.fajita.rllp.JumpsTest.Term.d;

import java.util.List;
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
    Item Sitem4 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 4);
    Item Ditem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 1);
    List<Item> jumps = rllp.jumps(Sitem0, new Verb(d));
    assertEquals(Ditem1, jumps.remove(0));
    assertEquals(Sitem4, jumps.remove(0));
    assertTrue(jumps.isEmpty());
  }
  @Test public void testNarrowJump() {
    Item Sitem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 0);
    Item Sitem2 = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 2);
    Item Bitem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == B && i.dotIndex == 1);
    List<Item> jumps = rllp.jumps(Sitem0, new Verb(b));
    assertEquals(Bitem1, jumps.remove(0));
    assertEquals(Sitem2, jumps.remove(0));
    assertTrue(jumps.isEmpty());
  }
  @Test public void testTrivialJumps() {
    Item Aitem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == A && i.dotIndex == 0);
    List<Item> jumps = rllp.jumps(Aitem0, new Verb(a));
    assertNull(jumps);
  }
  static Item getAnyMatchingItem(RLLP parser, Predicate<Item> predicate) {
    return parser.items.stream().filter(predicate).findAny().get();
  }
}
