package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.BNFTest.Γ.*;
import static il.ac.technion.cs.fling.BNFTest.Σ.*;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.BNF.Builder;
import il.ac.technion.cs.fling.BNF.SF;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
class BNFTest {
  public enum Σ implements Terminal {
    a, b, c, d, e, f, g, h
  }
  public enum Γ implements Variable {
    X, Y, Z, S, A, A1, B, C, D, E, F
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
  BNF arithemeticalExpression = BNF.of(v("E")).//
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
      azzert.that(arithemeticalExpression).isNotNull();
      azzert.that(arithemeticalExpression.tokens()).containsExactly(t("+"), t("id"), t("("), t(")"), t("*"));
      azzert.that(arithemeticalExpression.variables()).containsExactly(v("E"), v("T"), v("E'"), v("F"), v("T'"));
      azzert.that(arithemeticalExpression.start()).isEqualTo(v("E"));
      azzert.that(arithemeticalExpression.forms(v("F"))).contains(SF.of(t("id")));
      azzert.that(arithemeticalExpression.forms(v("F"))).contains(SF.of(t("("), v("E"), t(")")));
    }
  }
  @Test public void test3() {
    Nullables n = new Nullables(arithemeticalExpression);
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
    Firsts f = new Firsts(arithemeticalExpression);
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
    Follows f = new Follows(arithemeticalExpression);
    try (azzert azzert = new azzert()) {
      azzert.that(f.follows(v("E"))).containsExactly(Token.$, t(")"));
      azzert.that(f.follows(v("E'"))).containsExactly(Token.$, t(")"));
      azzert.that(f.follows(v("T"))).containsExactly(t("+"), Token.$, t(")"));
      azzert.that(f.follows(v("T'"))).containsExactly(t("+"), Token.$, t(")"));
      azzert.that(f.follows(v("F"))).containsExactly(t("*"), t("+"), Token.$, t(")"));
    }
  }
  /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
   * 
   * <pre>
   * 
  S → aBDh
  B → cC
  C → bC / ∈
  D → EF
  E → g / ∈
  F → f / ∈
   * </pre>
   */
  BNF problem1 = BNF.of(S). //
      derive(S).to(a, B, D, h).//
      derive(B).to(c, C).//
      derive(C).toNothingOr(b, C).//
      derive(D).to(E, F).//
      derive(E).toNothingOr(g).//
      derive(F).toNothingOr(f).//
      build();
  @Test public void problem1() {
    Follows grammar = new Follows(problem1);
    try (azzert azzert = new azzert()) {
      /**
       * <pre>
      First(S) = { a }
      First(B) = { c }
      First(C) = { b , ∈ }
      First(D) = { First(E) – ∈ } ∪ First(F) = { g , f , ∈ }
      First(E) = { g , ∈ }
      First(F) = { f , ∈ }
       * </pre>
       */
      azzert.that(grammar.nullable(S)).isFalse();
      azzert.that(grammar.nullable(B)).isFalse();
      azzert.that(grammar.nullable(C)).isTrue();
      azzert.that(grammar.nullable(D)).isTrue();
      azzert.that(grammar.nullable(E)).isTrue();
      azzert.that(grammar.firsts(S)).containsExactly(Token.of(a));
      azzert.that(grammar.firsts(B)).containsExactly(Token.of(c));
      azzert.that(grammar.firsts(C)).containsExactly(Token.of(b));
      azzert.that(grammar.firsts(D)).containsExactly(Token.of(g), Token.of(f));
      azzert.that(grammar.firsts(D)).containsExactly(Token.of(g), Token.of(f));
      azzert.that(grammar.firsts(E)).containsExactly(Token.of(g));
      azzert.that(grammar.firsts(F)).containsExactly(Token.of(f));
      azzert.that(grammar.nullable(F)).isTrue();
      /**
       * <pre>
        Follow(S) = { $ }
       Follow(B) = { First(D) – ∈ } ∪ First(h) = { g , f , h }
       Follow(C) = Follow(B) = { g , f , h }
       Follow(D) = First(h) = { h }
       Follow(E) = { First(F) – ∈ } ∪ Follow(D) = { f , h }
       Follow(F) = Follow(D) = { h }
       * </pre>
       */
      azzert.that(grammar.follows(S)).containsExactly(Token.$);
      azzert.that(grammar.follows(B)).containsExactly(Token.of(g), Token.of(f), Token.of(h));
      azzert.that(grammar.follows(C)).containsExactly(Token.of(g), Token.of(f), Token.of(h));
      azzert.that(grammar.follows(D)).containsExactly(Token.of(h));
      azzert.that(grammar.follows(E)).containsExactly(Token.of(f), Token.of(h));
      azzert.that(grammar.follows(F)).containsExactly(Token.of(h));
    }
  }
  /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
   * 
   * <pre>
   * 
  S → A
  A → aBA’
  A’ → dA’ / ∈
  B → b
  C → g
   * </pre>
   */
  @Test public void test6() {
    BNF bnf = BNF.of(S).derive(A).to(a, b, c).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.start()).isEqualTo(S);
      azzert.that(bnf.variables()).contains(S);
    }
  }
  @Test public void test7() {
    BNF bnf = BNF.of(S).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.start()).isEqualTo(S);
      azzert.that(bnf.variables()).contains(S);
    }
  }
  @Test public void test8() {
    BNF bnf = BNF.of(S).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.variables()).contains(S);
      azzert.that(bnf.forms(S)).isEmpty();
      azzert.that(bnf.forms(bnf.start())).isEmpty();
      azzert.that(bnf.forms(bnf.start())).isNotNull();
      azzert.that(bnf.variables()).contains(S);
    }
  }
  @Test public void problem2() {
    /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
     * 
     * <pre>
     S → A 
     A → aBA’
     A’ → dA’ / ∈
     B → b
     C → g
     * </pre>
     */
    BNF problem2 = BNF.of(S).//
        derive(S).to(A). //
        derive(A).to(a, B, A1). //
        derive(A1).toNothingOr(d, A1). //
        derive(B).to(b). //
        derive(C).to(g). //
        build();
    Follows grammar = new Follows(problem2);
    try (azzert azzert = new azzert()) {
      /**
       * <pre>
      First(S) = First(A) = { a }
      First(A) = { a }
      First(A’) = { d , ∈ }
      First(B) = { b }
      First(C) = { g }
       * </pre>
       */
      azzert.that(grammar.variables()).containsExactly(S, A, B, A1, C);
      azzert.that(grammar.nullable(S)).isFalse();
      azzert.that(grammar.nullable(A)).isFalse();
      azzert.that(grammar.nullable(A1)).isTrue();
      azzert.that(grammar.nullable(B)).isFalse();
      azzert.that(grammar.nullable(C)).isFalse();
      azzert.that(grammar.firsts(S)).isEqualTo(grammar.firsts(A));
      azzert.that(grammar.firsts(S)).containsExactly(Token.of(a));
      azzert.that(grammar.firsts(A)).containsExactly(Token.of(a));
      azzert.that(grammar.firsts(A1)).containsExactly(Token.of(d));
      azzert.that(grammar.firsts(B)).containsExactly(Token.of(b));
      azzert.that(grammar.firsts(C)).containsExactly(Token.of(g));
      /**
       * <pre>
      Follow(S) = { $ }
      Follow(A) = Follow(S) = { $ }
      Follow(A’) = Follow(A) = { $ }
      Follow(B) = { First(A’) – ∈ } ∪ Follow(A) = { d , $ }
      Follow(C) = NA
       * </pre>
       */
      azzert.that(grammar.follows(S)).containsExactly(Token.$);
      azzert.that(grammar.follows(A)).containsExactly(Token.$);
      azzert.that(grammar.follows(A1)).containsExactly(Token.$);
      azzert.that(grammar.follows(B)).containsExactly(Token.of(d), Token.$);
      azzert.that(grammar.follows(C)).isEmpty();
    }
  }
}