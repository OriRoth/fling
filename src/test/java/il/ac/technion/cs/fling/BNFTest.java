package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.BNFTest.Γ.X;
import static il.ac.technion.cs.fling.BNFTest.Γ.Y;
import static il.ac.technion.cs.fling.BNFTest.Γ.Z;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.BNF.Builder;
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
      Builder b = BNF.of(X);
      azzert.that(b).isNotNull();
      BNF g = b.build();
      azzert.that(g).isNotNull();
      azzert.that(g.tokens()).isEmpty();
      azzert.that(g.forms(X)).isEmpty();
      azzert.that(g.variables()).contains(X);
      azzert.that(g.variables()).containsExactly(X);
      azzert.that(g.start()).isEqualTo(X);
    }
  }
  @Test void test1() {
    try (azzert azzert = new azzert()) {
      BNF g = BNF.of(X).derive(X).to(Y, Z).build();
      azzert.that(g).isNotNull();
      azzert.that(g.tokens()).isEmpty();
      azzert.that(g.variables()).containsExactly(X, Y, Z);
      azzert.that(g.start()).isEqualTo(X);
    }
  }
}
