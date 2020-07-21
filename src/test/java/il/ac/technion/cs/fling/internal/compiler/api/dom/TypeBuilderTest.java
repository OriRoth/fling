package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.internal.compiler.api._.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
@SuppressWarnings("static-method") class TypeBuilderTest {
  private final Named n1 = Named.by("TypeNameT1");
  private final Named n2 = Named.by("World");
  @Test public void test0() {
    Type.named(n1);
  }
  @Test public void test1() {
    assertThat(Type.named(n1)).isNotNull();
  }
  @Test public void test2() {
    assertThat(Type.named(n1)).isInstanceOf(Type.class);
  }
  @Test public void test3() {
    assertThat(Type.named(n1)).isEqualTo(Type.named(n1));
  }
  @Test public void test4() {
    assertThat(Type.named(n1).hashCode()).isEqualTo(Type.named(n1).hashCode());
  }
  @Test public void test5() {
    assertThat(Type.named(n1)).isNotEqualTo(Type.named(n2));
  }
  @Test public void test6() {
    assertThat(Type.named(n1).hashCode()).isNotEqualTo(Type.named(n2).hashCode());
  }
  @Test public void test7() {
    assertThat(Type.named(n1).hashCode()).isNotEqualTo(Type.named(n2).hashCode());
  }
  @Test public void test8() {
    final var t = Type.named(n1);
    final var softly = new SoftAssertions();
    softly.assertThat(t.parameters()).isEmpty();
    softly.assertThat(t.methods()).isEmpty();
    softly.assertThat(t.isAccepting).isFalse();
    softly.assertAll();
  }
  @Test public void test9() {
    final var t = Type.named(Type.Name.BOTTOM);
    final var softly = new SoftAssertions();
    softly.assertThat(t.parameters()).isEmpty();
    softly.assertThat(t.methods()).isEmpty();
    softly.assertThat(t.isAccepting).isFalse();
    softly.assertAll();
  }
  @Test public void test10() {
    final var t = Type.named(Type.Name.TOP).accepting();
    final var softly = new SoftAssertions();
    softly.assertThat(t.parameters()).isEmpty();
    softly.assertThat(t.methods()).isEmpty();
    softly.assertThat(t.isAccepting).isTrue();
    softly.assertAll();
  }
}
