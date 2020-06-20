package il.ac.technion.cs.fling.internal.grammar.rules;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
@SuppressWarnings("static-method") public class NamedTest {
  Named n1 = Named.by("Hello");
  Named n2 = Named.by("World");
  @Test void test0() {
    assertThat(n1).isInstanceOf(Named.class);
    assertThat(n2).isInstanceOf(Named.class);
  }
  @Test void test1() {
    assertThat(n1.name()).isEqualTo("Hello");
    assertThat(n2.name()).isEqualTo("World");
  }
  @Test void test2() {
    assertThat(Named.by("Hello").name()).isEqualTo("Hello");
  }
  @Test void test3() {
    assertThat(Named.by("Hello")).isEqualTo(Named.by("Hello"));
  }
  @Test void test4() {
    assertThat(Named.by("Hello")).isNotEqualTo(Named.by("World"));
  }
  @Test void test5() {
    assertThat(Named.by("Hello").hashCode()).isEqualTo(Named.by("Hello").hashCode());
  }
  @Test void test6() {
    assertThat(Named.by("Hello").hashCode()).isNotEqualTo(Named.by("World").hashCode());
  }
}
