package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Name;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
class TypeNameTest {
  private final Named q = Named.by("Q");
  private final Named qa = Named.by("Q");
  private final Named qb = Named.by("Q");
  private final Named q1 = Named.by("Q1");
  private final Named q2 = Named.by("Q2");
  private final Type.Name.q n = Type.Name.q(q);
  private final Type.Name.q na = Type.Name.q(q);
  private final Type.Name.q nb = Type.Name.q(Named.by("Q"));
  private final Word<Named> α = Word.of(q, q1);
  private final Word<Named> αa = Word.of(q, Named.by("Q1"));
  private final Word<Named> αb = Word.of(q, q1);
  private final Type.Name.q.α nα = n.α(α);
  private final Type.Name.q.α nαa = n.α(Word.of(q, q1));
  private final Type.Name.q.α nαb = nb.α(Word.of(Named.by("Q"), Named.by("Q1")));
  private final Type.Name.q.α nαc = na.α(αa);
  private final Type.Name.q.α nαd = nb.α(αb);
  private final Word<Named> α1 = Word.of(q1, q2);
  private final Set<Named> β = new LinkedHashSet<>(α1);
  private final Set<Named> βa = new LinkedHashSet<>(α1);
  private final List<Named> q1q2 = Arrays.asList(q1, q2);
  private final Set<Named> βb = new LinkedHashSet<>(q1q2);
  private final Type.Name.q.α.β nαβ = n.α(α).β(β);
  private final Type.Name.q.α.β nαβa = n.α(α).β(β);
  private final Type.Name.q.α.β nαβb = n.α(α).β(βa);
  private final Type.Name.q.α.β nαβc = n.α(α).β(βb);
  private final Name TOP = Type.Name.TOP;
  private final Type topType = Type.named(TOP);
  private final Name BOTTOM = Type.Name.BOTTOM;
  private final Type bottomType = Type.named(BOTTOM);
  Type.Name[] names = { Type.Name.TOP, Type.Name.BOTTOM, Type.Name.END, n, nα, nαβ };
  @Test public void test00() {
    assertThat(Type.Name.q(q)).isNotNull();
    assertThat(Type.Name.q(q1)).isNotNull();
    assertThat(Type.Name.q(q2)).isNotNull();
  }
  @Test public void test01() {
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(qa));
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(qb));
    assertThat(Type.Name.q(qa)).isEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(qb)).isEqualTo(Type.Name.q(q));
  }
  @Test public void test02() {
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(q).hashCode());
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(qa).hashCode());
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(qb).hashCode());
  }
  @Test public void test03() {
    assertThat(Type.Name.q(q)).isNotEqualTo(Type.Name.q(q1));
    assertThat(Type.Name.q(q2).hashCode()).isNotEqualTo(Type.Name.q(q1).hashCode());
    assertThat(Type.Name.q(q)).isNotEqualTo(Type.Name.q(q2));
    assertThat(Type.Name.q(q1)).isNotEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q2)).isNotEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q1)).isNotEqualTo(Type.Name.q(q2));
    assertThat(Type.Name.q(q2)).isNotEqualTo(Type.Name.q(q1));
  }
  @Test public void test04() {
    assertThat(Type.Name.q(q).hashCode()).isNotEqualTo(Type.Name.q(q1).hashCode());
  }
  @Test public void test05() {
    assertThat(nα).isEqualTo(nαa);
  }
  @Test public void test06() {
    final var softly = new SoftAssertions();
    softly.assertThat(nα).isEqualTo(nα);
    softly.assertThat(nα).isEqualTo(nαa);
    softly.assertThat(nα.hashCode()).isEqualTo(nαa.hashCode());
    softly.assertThat(nα).isEqualTo(nαb);
    softly.assertThat(nα).isEqualTo(nαc);
    softly.assertThat(nα).isEqualTo(nαd);
    softly.assertThat(nαa).isEqualTo(nα);
    softly.assertThat(nαa).isEqualTo(nαa);
    softly.assertThat(nαa).isEqualTo(nαb);
    softly.assertThat(nαa).isEqualTo(nαc);
    softly.assertThat(nαa).isEqualTo(nαd);
    softly.assertThat(nαb).isEqualTo(nα);
    softly.assertThat(nαb).isEqualTo(nαa);
    softly.assertThat(nαb).isEqualTo(nαb);
    softly.assertThat(nαb).isEqualTo(nαc);
    softly.assertThat(nαb).isEqualTo(nαd);
    softly.assertThat(nαc).isEqualTo(nα);
    softly.assertThat(nαc).isEqualTo(nαa);
    softly.assertThat(nαc).isEqualTo(nαb);
    softly.assertThat(nαc).isEqualTo(nαc);
    softly.assertThat(nαc).isEqualTo(nαd);
    softly.assertThat(nαd).isEqualTo(nα);
    softly.assertThat(nαd).isEqualTo(nαa);
    softly.assertThat(nαd).isEqualTo(nαb);
    softly.assertThat(nαd).isEqualTo(nαc);
    softly.assertThat(nαd).isEqualTo(nαd);
    softly.assertThat(nαd).isEqualTo(nα);
    softly.assertThat(nα).isEqualTo(nαa);
    softly.assertThat(nα).isEqualTo(nαb);
    softly.assertThat(nα).isEqualTo(nαc);
    softly.assertThat(nα).isEqualTo(nαd);
    softly.assertAll();
  }
  @Test public void test07() {
    assertThat(nαβb).isEqualTo(nαβ);
  }
  @Test public void test08() {
    assertThat(nαβ).isEqualTo(nαβa);
  }
  @Test public void test09() {
    assertThat(nαβ).isEqualTo(nαβb);
  }
  @Test public void test10() {
    assertThat(nαβ).isEqualTo(nαβc);
  }
  @Test public void test11() {
    assertThat(nαβ.hashCode()).isEqualTo(nαβc.hashCode());
  }
  @Test public void test12() {
    final Set<Type> types = new LinkedHashSet<>();
    types.add(topType);
    types.add(Type.named(Type.Name.TOP));
    types.add(topType);
    assertThat(types.size()).isEqualTo(1);
    types.add(bottomType);
    types.add(Type.named(Type.Name.BOTTOM));
    types.add(bottomType);
    assertThat(types.size()).isEqualTo(2);
  }
}
