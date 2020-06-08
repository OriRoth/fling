package il.ac.technion.cs.fling.examples.languages;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;

@SuppressWarnings({ "boxing", "static-method" }) class BalancedParenthesesTest {

  @Test void test0() {
    new BalancedParentheses().BNF();
  }

  @Test void test1() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Γ.size(), equalTo(2));
  }

  @Test void test2() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.ε, equalTo(BalancedParentheses.V.P));
  }

  @Test void test3() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ, hasItem(BalancedParentheses.Σ.c.normalize()));
  }

  @Test void test3a() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ, hasItem(BalancedParentheses.Σ.ↄ.normalize()));
  }

  @Test void test3b() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ, hasItem(Constants.$$));
  }

  @Test void test4() {
    FancyEBNF b = new BalancedParentheses().BNF();
    assertThat(b + "", is("EBNF[Σ=[c, ↄ, $], Γ=[P, S], ε=P, R=[P->cPↄP, P->ε, S->P]]"));
  }

  @Test void test5() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ.size(), equalTo(3));
  }

  @Test void test6() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Γ, hasItems(BalancedParentheses.V.values()));
  }

  @Test void test7() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ, hasItem(Constants.$$));
  }

  @Test void test8() {
    EBNF b = new BalancedParentheses().BNF();
    assertThat(b.Σ.size(), is(BalancedParentheses.Σ.values().length + 1));
  }
}