package il.ac.technion.cs.fling.examples.automata;
import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Q.q0;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Q.q1;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Q.q2;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Γ.γ0;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Γ.γ1;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Σ.c;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Σ.Ↄ;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Σ.ↄ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
public class DPDATest {
  enum Q {
    q0, q1, q2
  }
  enum Σ {
    c, ↄ, Ↄ
  }
  enum Γ {
    γ0, γ1
  }
  final DPDA<Q, Σ, Γ> dpda = //
      dpda(Q.class, Σ.class, Γ.class) //
          .q0(q0) //
          .F(q0) //
          .γ0(γ0) //
          .δ(q0, c, γ0, q1, γ0, γ1) //
          .δ(q1, c, γ1, q1, γ1, γ1) //
          .δ(q1, ↄ, γ1, q1) //
          .δ(q1, ε(), γ0, q0, γ0) //
          .δ(q1, Ↄ, γ1, q2) //
          .δ(q2, ε(), γ1, q2) //
          .δ(q2, ε(), γ0, q0, γ0) //
          .go();
  @Test public void testTransitionMatching() {
    final δ<Q, Σ, Γ> δ = dpda.δ(q0, c, γ0);
    assertEquals(q1, δ.q$);
    assertEquals(2, δ.getΑ().size());
    assertEquals(new Word<>(γ0, γ1), δ.getΑ());
    assertNull(dpda.δ(q0, Ↄ, γ0));
  }
  // TODO Roth: add better consolidation testing
  @Test public void testTransitionConsolidation() {
    final δ<Q, Σ, Γ> δ = dpda.δδ(q1, Ↄ, γ1);
    assertEquals(q2, δ.q$);
    assertTrue(δ.getΑ().isEmpty());
    assertThat(δ.getΑ()).isEmpty();
    assertThat(q2).isEqualTo(δ.q$);
  }
}
