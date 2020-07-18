package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.adapters.CLikeGenerator;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
class TypeNameRendererTest {
  private final Named q = Named.by("Q");
  private final Named q1 = Named.by("Q1");
  private final Named q2 = Named.by("Q2");
  private final Named q3 = Named.by("Q3");
  private final Type.Name.q n = Type.Name.q(q);
  private final Word<Named> α = Word.of(q, q1);
  private final Type.Name.q.α nα = n.α(α);
  private final Word<Named> α1 = Word.of(q3, q1, q2);
  private final Set<Named> β = new LinkedHashSet<>(α1);
  private final Type.Name.q.α.β nαβ = n.α(α).β(β);
  private final CLikeGenerator g = new CPPGenerator(null);
  @Test public void test1() {
    assertThat(g.render(n)).isEqualTo("Q");
  }
  @Test public void test2() {
    assertThat(g.render(nα)).isEqualTo("Q_QQ1");
  }
  @Test public void test3() {
    assertThat(g.render(nαβ)).isEqualTo("Q_QQ1_Q3Q1Q2");
  }
  @Test public void test4() {
    assertThat(g.render(Type.Name.TOP)).isEqualTo(g.topName());
  }
  @Test public void test5() {
    assertThat(g.render(Type.Name.BOTTOM)).isEqualTo(g.bottomName());
  }
  @Test public void test6() {
    assertThat(g.render(Type.Name.END)).isEqualTo("void");
  }
}
