package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.BNFTest.Γ.X;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
class BNFTest {
  public enum Σ implements Terminal {
    a, b, c, d, e
  }
  public enum Γ implements Variable {
    X, Y, Z
  }
  @Test void test() {
    try (azzert azzert = new azzert()) {
      var b = BNF.from(X);
      azzert.that(b).isNotNull();
      BNF g = b.build();
      azzert.that(g).isNotNull();
      azzert.that(g.Σ).isEmpty();
      azzert.that(g.R).isEmpty();
      azzert.that(g.Γ).contains(X);
      azzert.that(g.Γ).containsExactly(X);
      azzert.that(g.start).isEqualTo(X);
    }
  }
}
