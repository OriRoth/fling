package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.junit.Assert.assertEquals;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.adapters.CLikeGenerator;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
public class TypeNameRendererTest {
  private final Named q = Named.by("Q");
  private final Named q1 = Named.by("Q1");
  private final Named q2 = Named.by("Q2");
  private final Named q3 = Named.by("Q3");
  private final Type.Name.q n = Type.Name.q(q);
  private final Word<Named> α = Word.of(q, q1);
  final Type.Name.q.α nα = n.α(α);
  private final Word<Named> α1 = Word.of(q3, q1, q2);
  private final Set<Named> β = new LinkedHashSet<>(α1);
  final Type.Name.q.α.β nαβ = n.α(α).β(β);
  final CLikeGenerator g = new CPPGenerator(null);
  @Test public void test1() {
    assertEquals(g.render(n), "Q");
  }
  @Test public void test2() {
    assertEquals(g.render(nα), "Q_QQ1");
  }
  @Test public void test3() {
    assertEquals(g.render(nαβ), "Q_QQ1_Q3Q1Q2");
  }
  @Test public void test4() {
    assertEquals(g.render(Type.Name.TOP), g.topName());
  }
  @Test public void test5() {
    assertEquals(g.render(Type.Name.BOTTOM), g.bottomName());
  }
  @Test public void test6() {
    assertEquals(g.render(Type.Name.END), "void");
  }
}
