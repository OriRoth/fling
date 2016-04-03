package org.spartan.fajita.rllp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.*;
import static org.spartan.fajita.rllp.ConsolidateTest.Term.c;
import static org.spartan.fajita.rllp.ConsolidateTest.Term.d;

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

@SuppressWarnings("static-method") public class ConsolidateTest {
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
        .derive(S).to(A).and(D) //
        .derive(A).to(B) //
        .derive(B).to(C) //
        .derive(C).to(c).orNone() //
        .derive(D).to(d) //
        .go();
    rllp = new RLLP(bnf);
  }
  @Test public void testTrivialConsolidation() {
    Item Ditem0 = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 0);
    Item Ditem1 = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 1);
    Deque<Item> consolidation = rllp.consolidate(Ditem0, new Verb(d));
    assertEquals(Ditem1, consolidation.removeFirst());
    assertTrue(consolidation.isEmpty());
  }
  @Test public void testWideConsolidation() {
    Item Sitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 1);
    Item Ditem = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 1);
    Item start = rllp.getStartItem(new Verb(d));
    Deque<Item> consolidation = rllp.consolidate(start, new Verb(d));
    assertEquals(Ditem, consolidation.removeFirst());
    assertEquals(Sitem, consolidation.removeFirst());
    assertTrue(consolidation.isEmpty());
  }
  @Test public void testDeepConsolidation() {
    Item Sitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 0);
    Item Aitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == A && i.dotIndex == 0);
    Item Bitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == B && i.dotIndex == 0);
    Item Citem = getAnyMatchingItem(rllp, i -> i.rule.lhs == C && i.dotIndex == 1);
    Item start = rllp.getStartItem(new Verb(c));
    Deque<Item> consolidation = rllp.consolidate(start, new Verb(c));
    assertEquals(Citem, consolidation.removeFirst());
    assertEquals(Bitem, consolidation.removeFirst());
    assertEquals(Aitem, consolidation.removeFirst());
    assertEquals(Sitem, consolidation.removeFirst());
    assertTrue(consolidation.isEmpty());
  }
  static Item getAnyMatchingItem(RLLP parser, Predicate<Item> predicate) {
    return parser.items.stream().filter(predicate).findAny().get();
  }
}
