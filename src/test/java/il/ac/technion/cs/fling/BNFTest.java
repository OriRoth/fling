package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.BNFTest.Γ.X;
import static il.ac.technion.cs.fling.BNFTest.Γ.Y;
import static il.ac.technion.cs.fling.BNFTest.Γ.Z;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.BNF.Builder;
import il.ac.technion.cs.fling.BNF.SF;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
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
  /*
   * | e T --> FT' T' --> *FT' | e F --> id | (E)
   */
  static Variable v(String s) {
    return Variable.byName(s);
  }
  static Token t(String s) {
    return Token.of(s);
  }
  BNF g = BNF.of(v("E")).//
      derive(v("E")).to(v("T"), v("E'")). // E --> TE'
      derive(v("E'")).to(t("+"), v("T"), v("E'")). // E' --> +TE'
      derive(v("E'")).to(). // E'--> e
      derive(v("T")).to(v("F"), v("T'")). // T --> FT' T' --> *FT' | e F --> id | (E)
      derive(v("T'")).to(t("*"), v("F"), v("T'")). // T --> FT' T' --> *FT' | e F --> id | (E)
      derive(v("T'")).to(). // T' --> | e F --> id | (E)
      derive(v("F")).to(t("id")). //
      derive(v("F")).to(t("("), v("E"), t(")")). //
      build();
  @Test public void test2() {
    try (azzert azzert = new azzert()) {
      azzert.that(g).isNotNull();
      azzert.that(g.tokens()).containsExactly(t("+"), t("id"), t("("), t(")"), t("*"));
      azzert.that(g.variables()).containsExactly(v("E"), v("T"), v("E'"), v("F"), v("T'"));
      azzert.that(g.start()).isEqualTo(v("E"));
      azzert.that(g.forms(v("F"))).contains(SF.of(t("id")));
      azzert.that(g.forms(v("F"))).contains(SF.of(t("("), v("E"), t(")")));
    }
  }
  @Test public void test3() {
    Nullables n = new Nullables(g);
    try (azzert azzert = new azzert()) {
      azzert.that(n.tokens()).containsExactly(t("+"), t("id"), t("("), t(")"), t("*"));
      azzert.that(n.variables()).containsExactly(v("E"), v("T"), v("E'"), v("F"), v("T'"));
      azzert.that(n.start()).isEqualTo(v("E"));
      azzert.that(n.forms(v("F"))).contains(SF.of(t("id")));
      azzert.that(n.forms(v("F"))).contains(SF.of(t("("), v("E"), t(")")));
      azzert.that(n.nullable(v("E'"))).isTrue();
      azzert.that(n.nullable(v("T'"))).isTrue();
      azzert.that(n.nullable(v("E"))).isFalse();
      azzert.that(n.nullable(v("T"))).isFalse();
      azzert.that(n.nullable(v("F"))).isFalse();
    }
  }
  /* https://www.geeksforgeeks.org/construction-of-ll1-parsing-table/ */
  @Test public void test4() {
    Firsts f = new Firsts(g);
    try (azzert azzert = new azzert()) {
      azzert.that(f.firsts(v("E"))).containsExactly(t("id"), t("("));
      azzert.that(f.firsts(v("E'"))).containsExactly(t("+"));
      azzert.that(f.nullable(v("E'"))).isTrue();
      azzert.that(f.firsts(v("T"))).containsExactly(t("id"), t("("));
      azzert.that(f.firsts(v("T'"))).containsExactly(t("*"));
      azzert.that(f.nullable(v("T'"))).isTrue();
      azzert.that(f.firsts(v("F"))).containsExactly(t("id"), t("("));
    }
  }
  @Test public void test5() {
    Firsts f = new Firsts(g);
    try (azzert azzert = new azzert()) {
      azzert.that(f.firsts(v("E"))).containsExactly(t("id"), t("("));
      azzert.that(f.firsts(v("E'"))).containsExactly(t("+"));
      azzert.that(f.nullable(v("E'"))).isTrue();
      azzert.that(f.firsts(v("T"))).containsExactly(t("id"), t("("));
      azzert.that(f.firsts(v("T'"))).containsExactly(t("*"));
      azzert.that(f.nullable(v("T'"))).isTrue();
      azzert.that(f.firsts(v("F"))).containsExactly(t("id"), t("("));
    }
  }
}
