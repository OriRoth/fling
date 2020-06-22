package il.ac.technion.cs.fling.internal.grammar;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.X;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.Y;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.Z;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.a;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.b;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.c;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.d;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.e;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.oneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
@SuppressWarnings("static-method") class BNFUtilsTest {
  public enum Σ implements Terminal {
    a, b, c, d, e
  }
  public enum Γ implements Variable {
    X, Y, Z
  }
  static class Q implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(oneOrMore(a.with(int.class)), //
              noneOrMore(b.with(int.class)), //
              optional(Y), //
              optional(e.with(int.class)))
          . //
          derive(Y).to(c.with(int.class)). //
          or(d.with(int.class)). //
          build();
    }
  }
  static class Q1 implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(a, oneOrMore(b.with(byte.class)), c).derive(X).to(c, b, a).build();
    }
  }
  static class Q2 implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(a, b, c).//
          derive(X).to(a, b, c).//
          derive(Y).to(a, b, c).//
          specialize(Y).into(Y, X).//
          specialize(X).into(Y, X).//
          specialize(Z).into(X, Y, Z, X).//
          derive(Z).to(b, a).//
          build();
    }
  }
  static class Q3 implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(a, b, c).//
          derive(X).to(a, b).//
          derive(Y).to(a).//
          derive(Z).to(b, a).//
          build();
    }
  }
  @Test void test() {
    EBNF x = new Q().BNF();
    final SoftAssertions softly = new SoftAssertions();
    softly.assertThat(x).isNotNull();
    softly.assertThat(x.Γ).contains(Γ.X);
    softly.assertThat(x.Γ).contains(Γ.Y);
    softly.assertThat(x.Σ).contains(Token.of(Σ.a).with(int.class));
    softly.assertThat(x.Σ).contains(Token.of(Σ.b).with(int.class));
    softly.assertThat(x.Σ).doesNotContain(Token.of(Σ.d).with(byte.class));
    softly.assertThat(x.Σ).doesNotContain(Token.of(Σ.d));
    softly.assertAll();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.expandQuantifiers(FancyEBNF.from(x))));
  }
  @Test public void test1() {
    EBNF x = new Q1().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.expandQuantifiers(FancyEBNF.from(x))));
  }
  @Test public void test2() {
    EBNF x = new Q2().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(r(FancyEBNF.from(x)));
    System.out.println(r(r(FancyEBNF.from(x))));
  }
  EBNF r(EBNF b) {
    return BNFUtils.reduce(b);
  }
  @Test public void test3() {
    EBNF x = new Q2().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(r(FancyEBNF.from(x)));
    System.out.println(r(r(FancyEBNF.from(x))));
  }
  @Test public void test4() {
    EBNF x = new Q2().BNF();
    EBNF y = FancyEBNF.from(x);
    assertThat(r(r(y))).isEqualTo(r(y));
  }
  @Test public void test5() {
    EBNF x = new Q3().BNF();
    FancyEBNF y = FancyEBNF.from(x);
    System.out.println(x);
    System.out.println(y);
    System.out.println(r(y));
    System.out.println(r(r(y)));
    assertThat(r(r(y))).isEqualTo(r(y));
  }
}
