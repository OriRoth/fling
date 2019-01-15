package fling.automata;

import static fling.automata.DPDA.dpda;
import static fling.util.Collections.set;

import org.junit.Test;

import fling.automata.DPDA.δ;

import static fling.sententials.Alphabet.ε;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DPDATest {
  DPDA<Integer, Character, String> dpda = //
      dpda(set(0, 1, 2), set('(', ')', ']'), set("g0", "g1")) //
          .q0(0) //
          .F(0) //
          .γ0("g0") //
          .δ(0, '(', "g0", 1, "g0", "g1") //
          .δ(1, '(', "g1", 1, "g1", "g1") //
          .δ(1, ')', "g1", 1) //
          .δ(1, ε(), "g0", 0, "g0") //
          .δ(1, ']', "g1", 2) //
          .δ(2, ε(), "g1", 2) //
          .δ(2, ε(), "g0", 0, "g0") //
          .go();

  @Test public void testTransitionMatching() {
    δ<Integer, Character, String> δ = dpda.δ(0, '(', "g0");
    assertEquals(1, δ.q$.intValue());
    assertEquals(2, δ.α.size());
    assertEquals("g0", δ.α.top());
    assertEquals("g1", δ.α.get(1));
    assertNull(dpda.δ(0, ']', "g0"));
  }
  // TODO Roth: add better consolidation testing
  @Test public void testTransitionConsolidation() {
    δ<Integer, Character, String> δ = dpda.δδ(1, ']', "g1");
    assertEquals(2, δ.q$.intValue());
    assertTrue(δ.α.isEmpty());
  }
}
