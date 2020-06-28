package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.BNFTest.Γ.*;
import static il.ac.technion.cs.fling.BNFTest.Σ.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static java.util.stream.Collectors.toList;
import il.ac.technion.cs.fling.BNF.Builder;
import il.ac.technion.cs.fling.BNF.SF;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
@SuppressWarnings("static-method") public class BNFTest {
  /** https://www.geeksforgeeks.org/construction-of-ll1-parsing-table/ */
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
  /**
   * <pre>
  S --> A | a
  A --> a
   * </pre>
   */
  @Test public void example2geeks() {
    Follows grammar = new Follows(BNF.of(S).//
        derive(S).to(A).or(a). //
        derive(A).to(a). //
        build());
    try (azzert azzert = new azzert()) {
      azzert.that(grammar.firsts(S)).containsExactly(Token.of(a));
      azzert.that(grammar.firsts(A)).containsExactly(Token.of(a));
      azzert.that(grammar.follows(S)).containsExactly(Token.$);
      azzert.that(grammar.follows(A)).containsExactly(Token.$);
      azzert.that(grammar.recursive()).isFalse();
    }
  }
  @Test public void example2geeksA() {
    Follows grammar = new Follows(BNF.of(S).//
        derive(S).to(A). //
        derive(S).to(a). //
        derive(A).to(a). //
        build());
    assertThat(grammar.follows(S)).containsExactly(Token.$);
    assertThat(grammar.recursive()).isFalse();
  }
  @Test public void arithmeticalExpression1Structure() {
    try (azzert azzert = new azzert()) {
      azzert.that(arithemeticalExpression).isNotNull();
      azzert.that(arithemeticalExpression.tokens()).containsExactly(t("+"), t("id"), t("("), t(")"), t("*"));
      azzert.that(arithemeticalExpression.variables()).containsExactly(v("E"), v("T"), v("E'"), v("F"), v("T'"));
      azzert.that(arithemeticalExpression.start()).isEqualTo(v("E"));
      azzert.that(arithemeticalExpression.forms(v("F"))).contains(SF.of(t("id")));
      azzert.that(arithemeticalExpression.forms(v("F"))).contains(SF.of(t("("), v("E"), t(")")));
    }
  }
  @Test public void arithmeticalExpression2Nullables() {
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
      azzert.that(n.recursive()).isTrue();
    }
  }
  @Test public void arithmeticalExpression3Firsts() {
    Firsts grammar = new Firsts(arithemeticalExpression);
    try (azzert azzert = new azzert()) {
      azzert.that(grammar.firsts(v("E"))).containsExactly(t("id"), t("("));
      azzert.that(grammar.firsts(v("E'"))).containsExactly(t("+"));
      azzert.that(grammar.nullable(v("E'"))).isTrue();
      azzert.that(grammar.firsts(v("T"))).containsExactly(t("id"), t("("));
      azzert.that(grammar.firsts(v("T'"))).containsExactly(t("*"));
      azzert.that(grammar.nullable(v("T'"))).isTrue();
      azzert.that(grammar.firsts(v("F"))).containsExactly(t("id"), t("("));
    }
  }
  @Test public void arithmeticalExpression3Follows() {
    Follows grammar = new Follows(arithemeticalExpression);
    try (azzert azzert = new azzert()) {
      azzert.that(grammar.follows(v("E"))).containsExactly(Token.$, t(")"));
      azzert.that(grammar.follows(v("E'"))).containsExactly(Token.$, t(")"));
      azzert.that(grammar.follows(v("T"))).containsExactly(t("+"), Token.$, t(")"));
      azzert.that(grammar.follows(v("T'"))).containsExactly(t("+"), Token.$, t(")"));
      azzert.that(grammar.follows(v("F"))).containsExactly(t("*"), t("+"), Token.$, t(")"));
    }
  }
  @Test public void problem1() {
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
      azzert.that(grammar.uses(S)).containsExactly(A, B, A1);
      azzert.that(grammar.uses(A)).containsExactly(B, A1);
      azzert.that(grammar.uses(B)).isEmpty();
      azzert.that(grammar.uses(A1)).containsExactly(A1);
      azzert.that(grammar.uses(C)).isEmpty();
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
  @Test public void problem3() {
    /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
     * 
     * <pre>
    S → (L) / a
    L → SL’
    L’ → ,SL’ / ∈
     * </pre>
     */
    Follows grammar = new Follows(BNF.of(S).//
        derive(S).to(t("("), L, t(")")). //
        derive(S).to(a). //
        derive(L).to(S, L1). //
        derive(L1).toNothingOr(t(","), S, L1). //
        build());
    try (azzert azzert = new azzert()) {
      /**
       * <pre>
      First(S) = { ( , a }
      First(L) = First(S) = { ( , a }
      First(L’) = { , , ∈ }
       * </pre>
       */
      azzert.that(grammar.variables()).containsExactly(S, L, L1);
      azzert.that(grammar.nullable(S)).isFalse();
      azzert.that(grammar.nullable(L)).isFalse();
      azzert.that(grammar.nullable(L1)).isTrue();
      azzert.that(grammar.firsts(S)).containsExactly(t("("), t(a));
      azzert.that(grammar.firsts(L)).containsExactly(t("("), t(a));
      azzert.that(grammar.firsts(L1)).containsExactly(t(","));
      /**
       * <pre>
      Follow(S) = { $ } ∪ { First(L’) – ∈ } ∪ Follow(L) ∪ Follow(L’) = { $ , , , ) }
      Follow(L) = { ) }
      Follow(L’) = Follow(L) = { ) }
       * </pre>
       */
      azzert.that(grammar.follows(S)).containsExactly(Token.$, t(","), t(")"));
      azzert.that(grammar.follows(L)).containsExactly(t(")"));
      azzert.that(grammar.follows(L1)).containsExactly(t(")"));
    }
  }
  @Test public void problem4() {
    /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
     * 
     * <pre>
    S → AaAb / BbBa
    A → ∈
    B → ∈
     * </pre>
     */
    Follows grammar = new Follows(BNF.of(S).//
        derive(S).to(A, a, A, b).or(B, b, B, a). //
        epsilon(A, B). //
        build());
    try (azzert azzert = new azzert()) {
      /**
       * <pre>
      First(S) = { First(A) – ∈ } ∪ First(a) ∪ { First(B) – ∈ } ∪ First(b) = { a , b }
      First(A) = { ∈ }
      First(B) = { ∈ }
       * </pre>
       */
      azzert.that(grammar.variables()).containsExactly(S, A, B);
      azzert.that(grammar.tokens()).containsExactly(t(a), t(b));
      azzert.that(grammar.nullable(S)).isFalse();
      azzert.that(grammar.nullable(A)).isTrue();
      azzert.that(grammar.nullable(B)).isTrue();
      azzert.that(grammar.firsts(S)).containsExactly(t(a), t(b));
      /**
       * <pre>
      Follow(S) = { $ }
      Follow(A) = First(a) ∪ First(b) = { a , b }
      Follow(B) = First(b) ∪ First(a) = { a , b }
       * </pre>
       */
      azzert.that(grammar.follows(S)).containsExactly(Token.$);
      azzert.that(grammar.follows(A)).containsExactly(t(a), t(b));
      azzert.that(grammar.follows(B)).containsExactly(t(b), t(a));
    }
  }
  @Test public void problem6() {
    /** https://www.gatevidyalay.com/first-and-follow-compiler-design/
     * 
     * <pre>
    S → ACB / CbB / Ba
    A → da / BC
    B → g / ∈
    C → h / ∈
     * </pre>
     */
    Follows grammar = new Follows(BNF.of(S).//
        derive(S).to(A, C, B).or(C, b, B).or(B, a). //
        derive(A).to(d, a).or(B, C). //
        derive(B).to(g). //
        derive(C).to(h). //
        epsilon(B, C). //
        build());
    System.out.println(grammar.expand(S).collect(toList()));
    try (azzert azzert = new azzert()) {
      azzert.that(grammar.recursive()).isFalse();
      azzert.that(grammar.variables()).containsExactly(S, A, C, B);
      azzert.that(grammar.tokens()).containsExactly(t(b), t(a), t(d), t(h), t(g));
      /**
       * <pre>
      First(S) = { First(A) – ∈ }  ∪ { First(C) – ∈ } ∪ First(B) ∪ First(b) ∪ { First(B) – ∈ } ∪ First(a) = { d , g , h , ∈ , b , a }
      First(A) = First(d) ∪ { First(B) – ∈ } ∪ First(C) = { d , g , h , ∈ }
      First(B) = { g , ∈ }
      First(C) = { h , ∈ }
       * </pre>
       */
      azzert.that(grammar.nullable(S)).isTrue();
      azzert.that(grammar.nullable(A)).isTrue();
      azzert.that(grammar.nullable(B)).isTrue();
      azzert.that(grammar.nullable(C)).isTrue();
      azzert.that(grammar.firsts(S)).containsExactly(t(b), t(a), t(d), t(h), t(g));
      azzert.that(grammar.firsts(A)).containsExactly(t(d), t(g), t(h));
      azzert.that(grammar.firsts(B)).containsExactly(t(g));
      azzert.that(grammar.firsts(C)).containsExactly(t(h));
      /**
       * <pre>
      Follow(S) = { $ }
      Follow(A) = { First(C) – ∈ } ∪ { First(B) – ∈ } ∪ Follow(S) = { h , g , $ }
      Follow(B) = Follow(S) ∪ First(a) ∪ { First(C) – ∈ } ∪ Follow(A) = { $ , a , h , g }
      Follow(C) = { First(B) – ∈ } ∪ Follow(S) ∪ First(b) ∪ Follow(A) = { g , $ , b , h }
       * </pre>
       */
      azzert.that(grammar.follows(S)).containsExactly(Token.$);
      azzert.that(grammar.follows(A)).containsExactly(t(h), t(g), Token.$);
      azzert.that(grammar.follows(B)).containsExactly(Token.$, t(a), t(h), t(g));
      azzert.that(grammar.follows(C)).containsExactly(t(g), Token.$, t(b), t(h));
    }
  }
  @Test public void start0() {
    BNF bnf = BNF.of(S).derive(A).to(a, b, c).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.start()).isEqualTo(S);
      azzert.that(bnf.variables()).contains(S);
      azzert.that(new Nullables(bnf).recursive()).isFalse();
    }
  }
  @Test public void start1() {
    try (azzert azzert = new azzert()) {
      Builder builder = BNF.of(X);
      azzert.that(builder).isNotNull();
      BNF grammar = builder.build();
      azzert.that(grammar).isNotNull();
      azzert.that(grammar.tokens()).isEmpty();
      azzert.that(grammar.forms(X)).isEmpty();
      azzert.that(grammar.variables()).contains(X);
      azzert.that(grammar.variables()).containsExactly(X);
      azzert.that(grammar.start()).isEqualTo(X);
      azzert.that(new Nullables(grammar).recursive()).isFalse();
    }
  }
  @Test public void start2() {
    try (azzert azzert = new azzert()) {
      BNF grammar = BNF.of(X).derive(X).to(Y, Z).build();
      azzert.that(grammar).isNotNull();
      azzert.that(grammar.tokens()).isEmpty();
      azzert.that(grammar.variables()).containsExactly(X, Y, Z);
      azzert.that(grammar.start()).isEqualTo(X);
      azzert.that(new Nullables(grammar).recursive()).isFalse();
    }
  }
  @Test public void variables1() {
    BNF bnf = BNF.of(S).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.start()).isEqualTo(S);
      azzert.that(bnf.variables()).contains(S);
      azzert.that(new Nullables(bnf).recursive()).isFalse();
    }
  }
  @Test public void variables2() {
    BNF bnf = BNF.of(S).build();
    try (azzert azzert = new azzert()) {
      azzert.that(bnf.variables()).contains(S);
      azzert.that(bnf.forms(S)).isEmpty();
      azzert.that(bnf.forms(bnf.start())).isEmpty();
      azzert.that(bnf.forms(bnf.start())).isNotNull();
      azzert.that(bnf.variables()).contains(S);
      azzert.that(new Nullables(bnf).recursive()).isFalse();
    }
  }
  static Token t(String s) {
    return Token.of(s);
  }
  static Token t(Terminal t) {
    return Token.of(t);
  }
  /*
   * | e T --> FT' T' --> *FT' | e F --> id | (E)
   */
  static Variable v(String s) {
    return Variable.byName(s);
  }
  public enum Γ implements Variable {
    A, A1, B, C, D, E, F, S, L, L1, X, Y, Z
  }
  public enum Σ implements Terminal {
    a, b, c, d, e, f, g, h
  }
}