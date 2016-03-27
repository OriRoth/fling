package org.spartan.fajita.rllp;

import static org.junit.Assert.*;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.A;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.B;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.C;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.D;
import static org.spartan.fajita.rllp.ConsolidateTest.NT.S;
import static org.spartan.fajita.rllp.ConsolidateTest.Term.c;
import static org.spartan.fajita.rllp.ConsolidateTest.Term.d;

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
  @Test public void testWideConsolidation() {
    Item Sitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 2);
    Item Ditem = getAnyMatchingItem(rllp, i -> i.rule.lhs == D && i.dotIndex == 1);
    Item start = rllp.getStartItem(new Verb(d));
    List<Item> consolidation = rllp.consolidate(start, new Verb(d));
    assertEquals(Sitem, consolidation.remove(0));
    assertEquals(Ditem, consolidation.remove(0));
    assertTrue(consolidation.isEmpty());
  }
  @Test public void testDeepConsolidation() {
    Item Sitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == S && i.dotIndex == 1);
    Item Aitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == A && i.dotIndex == 1);
    Item Bitem = getAnyMatchingItem(rllp, i -> i.rule.lhs == B && i.dotIndex == 1);
    Item Citem = getAnyMatchingItem(rllp, i -> i.rule.lhs == C && i.dotIndex == 1);
    Item start = rllp.getStartItem(new Verb(c));
    List<Item> consolidation = rllp.consolidate(start, new Verb(c));
    assertEquals(Sitem, consolidation.remove(0));
    assertEquals(Aitem, consolidation.remove(0));
    assertEquals(Bitem, consolidation.remove(0));
    assertEquals(Citem, consolidation.remove(0));
    assertTrue(consolidation.isEmpty());
  }
  static Item getAnyMatchingItem(RLLP parser, Predicate<Item> predicate) {
    return parser.items.stream().filter(predicate).findAny().get();
  }
}
