package il.ac.technion.cs.fling.examples.automata;
import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Q.*;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Γ.*;
import static il.ac.technion.cs.fling.examples.automata.DPDATest.Σ.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.DPDA;
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
  private final DPDA<Q, Σ, Γ> dpda = //
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
    final var δ = dpda.δ(q0, γ0, c);
    assertThat(q1).isEqualTo(δ.q$);
    assertThat(2).isEqualTo(δ.α.size());
    assertThat(new Word<>(γ0, γ1)).isEqualTo(δ.α);
    assertThat(dpda.δ(q0, γ0, Ↄ)).isNull();
  }
  // TODO Roth: add better consolidation testing
  @Test public void testTransitionConsolidation() {
    final var δ = dpda.δδ(q1, γ1, Ↄ);
    assertThat(q2).isEqualTo(δ.q$);
    assertThat(δ.α).isEmpty();
    assertThat(δ.α).isEmpty();
    assertThat(q2).isEqualTo(δ.q$);
  }
  @Test public void testRun() {
    assertThat(dpda.run()).isTrue();
    assertThat(dpda.run(c, c, ↄ, ↄ)).isTrue();
    assertThat(dpda.run(c, c, c, ↄ, ↄ)).isFalse();
    assertThat(dpda.run(c, c, ↄ, ↄ, ↄ)).isFalse();
    assertThat(dpda.run(c, c, Ↄ)).isTrue();
    assertThat(dpda.run(c, c, Ↄ, Ↄ)).isFalse();
    assertThat(dpda.run(c, c, c, ↄ, Ↄ, c, Ↄ)).isTrue();
  }
}
