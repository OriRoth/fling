package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
@SuppressWarnings("static-method") public class MethodTest {
  @Test public void test() {
    var m1 = Method.termination();
    var m2 = Method.termination();
    assertThat(m1).isEqualTo(m2);
  }
}
