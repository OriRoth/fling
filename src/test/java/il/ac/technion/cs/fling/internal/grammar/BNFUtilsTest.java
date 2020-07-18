package il.ac.technion.cs.fling.internal.grammar;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.*;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.*;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
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
    final EBNF x = new Q().BNF();
    try (final azzert azzert = new azzert()) {
      azzert.that(x).isNotNull();
      azzert.that(x.Γ).contains(Γ.X);
      azzert.that(x.Γ).contains(Γ.Y);
      azzert.that(x.Σ).contains(Token.of(Σ.a).with(int.class));
      azzert.that(x.Σ).contains(Token.of(Σ.b).with(int.class));
      azzert.that(x.Σ).doesNotContain(Token.of(Σ.d).with(byte.class));
      azzert.that(x.Σ).doesNotContain(Token.of(Σ.d));
    }
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.expandQuantifiers(FancyEBNF.from(x))));
  }
  @Test public void test1() {
    final EBNF x = new Q1().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.expandQuantifiers(FancyEBNF.from(x))));
  }
  @Test public void test2() {
    final EBNF x = new Q2().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(r(FancyEBNF.from(x)));
    System.out.println(r(r(FancyEBNF.from(x))));
  }
  private EBNF r(final EBNF b) {
    return BNFUtils.reduce(b);
  }
  @Test public void test3() {
    final EBNF x = new Q2().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(r(FancyEBNF.from(x)));
    System.out.println(r(r(FancyEBNF.from(x))));
  }
  @Test public void test4() {
    final EBNF x = new Q2().BNF();
    final EBNF y = FancyEBNF.from(x);
    assertThat(r(r(y))).isEqualTo(r(y));
  }
  @Test public void test5() {
    final EBNF x = new Q3().BNF();
    final FancyEBNF y = FancyEBNF.from(x);
    System.out.println(x);
    System.out.println(y);
    System.out.println(r(y));
    System.out.println(r(r(y)));
    assertThat(r(r(y))).isEqualTo(r(y));
  }
}
