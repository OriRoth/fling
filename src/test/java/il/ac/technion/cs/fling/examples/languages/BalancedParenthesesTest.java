package il.ac.technion.cs.fling.examples.languages;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
@SuppressWarnings({ "boxing", "static-method" }) class BalancedParenthesesTest {
  @Test void test0() {
    new BalancedParentheses().BNF();
  }
  @Test void test1() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Γ.size()).isEqualTo(2);
  }
  @Test void test2() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.ε).isEqualTo(BalancedParentheses.Γ.P);
  }
  @Test void test3() {
    assertThat(new BalancedParentheses().BNF().Σ).contains(BalancedParentheses.Σ.c.normalize());
  }
  @Test void test3a() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Σ).contains(BalancedParentheses.Σ.ↄ.normalize());
  }
  @Test void test3b() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Σ).contains(Constants.$$);
  }
  @Test void test4() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b + "").isEqualTo("EBNF[Σ=[c, ↄ, $], Γ=[P, S], ε=P, R=[P->cPↄP, P->ε, S->P]]");
  }
  @Test void test5() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Σ.size()).isEqualTo(3);
  }
  @Test void test6() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Γ).contains(BalancedParentheses.Γ.values());
  }
  @Test void test7() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Σ).contains(Constants.$$);
  }
  @Test void test8() {
    final var b = new BalancedParentheses().BNF();
    assertThat(b.Σ.size()).isEqualTo(BalancedParentheses.Σ.values().length + 1);
  }
}