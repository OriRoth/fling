package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
public class TypeNameTest {
  private Named q = Named.by("Q");
  private Named qa = Named.by("Q");
  private Named qb = Named.by("Q");
  private Named q1 = Named.by("Q1");
  private Named q2 = Named.by("Q2");
  private Type.Name.q n = Type.Name.q(q);
  private Type.Name.q na = Type.Name.q(q);
  private Type.Name.q nb = Type.Name.q(Named.by("Q"));
  private Word<Named> α = Word.of(q, q1);
  private Word<Named> αa = Word.of(q, Named.by("Q1"));
  private Word<Named> αb = Word.of(q, q1);
  Type.Name.q.α nα = n.α(α);
  Type.Name.q.α nαa = n.α(Word.of(q, q1));
  Type.Name.q.α nαb = nb.α(Word.of(Named.by("Q"), Named.by("Q1")));
  Type.Name.q.α nαc = na.α(αa);
  Type.Name.q.α nαd = nb.α(αb);
  private Word<Named> α1 = Word.of(q1, q2);
  private Set<Named> β = new LinkedHashSet<>(α1);
  private Set<Named> βa = new LinkedHashSet<>(α1);
  List<Named> q1q2 = Arrays.asList(q1, q2);
  private Set<Named> βb = new LinkedHashSet<>(q1q2);
  Type.Name.q.α.β nαβ = n.α(α).β(β);
  Type.Name.q.α.β nαβa = n.α(α).β(β);
  Type.Name.q.α.β nαβb = n.α(α).β(βa);
  Type.Name.q.α.β nαβc = n.α(α).β(βb);
  Type.Name names[] = { Type.Name.TOP, Type.Name.BOTTOM, Type.Name.END, n, nα, nαβ };
  @Test void test00() {
    assertThat(Type.Name.q(q)).isNotNull();
    assertThat(Type.Name.q(q1)).isNotNull();
    assertThat(Type.Name.q(q2)).isNotNull();
  }
  @Test void test01() {
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(qa));
    assertThat(Type.Name.q(q)).isEqualTo(Type.Name.q(qb));
    assertThat(Type.Name.q(qa)).isEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(qb)).isEqualTo(Type.Name.q(q));
  }
  @Test void test02() {
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(q).hashCode());
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(qa).hashCode());
    assertThat(Type.Name.q(q).hashCode()).isEqualTo(Type.Name.q(qb).hashCode());
  }
  @Test void test03() {
    assertThat(Type.Name.q(q)).isNotEqualTo(Type.Name.q(q1));
    assertThat(Type.Name.q(q2).hashCode()).isNotEqualTo(Type.Name.q(q1).hashCode());
    assertThat(Type.Name.q(q)).isNotEqualTo(Type.Name.q(q2));
    assertThat(Type.Name.q(q1)).isNotEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q2)).isNotEqualTo(Type.Name.q(q));
    assertThat(Type.Name.q(q1)).isNotEqualTo(Type.Name.q(q2));
    assertThat(Type.Name.q(q2)).isNotEqualTo(Type.Name.q(q1));
  }
  @Test void test04() {
    assertThat(Type.Name.q(q).hashCode()).isNotEqualTo(Type.Name.q(q1).hashCode());
  }
  @Test void test05() {
    assertThat(nα).isEqualTo(nαa);
  }
  @Test void test06() {
    SoftAssertions softly = new SoftAssertions();
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
  @Test void test07() {
    assertThat(nαβb).isEqualTo(nαβ);
  }
  @Test void test08() {
    assertThat(nαβ).isEqualTo(nαβa);
  }
  @Test void test09() {
    assertThat(nαβ).isEqualTo(nαβb);
  }
  @Test void test10() {
    assertThat(nαβ).isEqualTo(nαβc);
  }
  @Test void test11() {
    assertThat(nαβ.hashCode()).isEqualTo(nαβc.hashCode());
  }
}
