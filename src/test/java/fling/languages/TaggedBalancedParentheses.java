package fling.languages;

import static fling.generated.TaggedBalancedParentheses._1;
import static fling.generated.TaggedBalancedParentheses.AB._2;
import static fling.grammar.BNF.bnf;
import static fling.languages.TaggedBalancedParentheses.V.AB;
import static fling.languages.TaggedBalancedParentheses.V.P;
import static fling.languages.TaggedBalancedParentheses.Σ.a;
import static fling.languages.TaggedBalancedParentheses.Σ.b;
import static fling.languages.TaggedBalancedParentheses.Σ.c;
import static fling.languages.TaggedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaMediator;
import fling.generated.TaggedBalancedParenthesesAST.AB1;
import fling.generated.TaggedBalancedParenthesesAST.P;
import fling.generated.TaggedBalancedParenthesesAST.P1;
import fling.generated.TaggedBalancedParenthesesAST.P2;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class TaggedBalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }

  public enum V implements Variable {
    P, AB
  }

  public static final BNF bnf = bnf(V.class). //
      start(P). //
      derive(P, c.many(char.class), P, ↄ.with(AB), P). //
      derive(P). //
      derive(AB, a). //
      derive(AB, b). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "TaggedBalancedParentheses", Σ.class);

  public static void compilationTest() {
    _1().c('a', 'a').ↄ(_2().a()).$();
    _1().c('a', 'a').ↄ(_2().a()).ↄ(_2().a());
    _1().c('a', 'a').c('b').c('c').ↄ(_2().a()).ↄ(_2().a());
    _1().c('a', 'a').c('b').c('c').ↄ(_2().a()).ↄ(_2().b()).ↄ(_2().a()).$();
    _1().c('a', 'a').c('b').ↄ(_2().a()).ↄ(_2().b()).c('e').ↄ(_2().a()).$();
    _1().c('a', 'a').c('b').ↄ(_2().a()).ↄ(_2().b()).c('e');
    _1().ↄ(_2().a());
  }
  public static void main(String[] args) {
    P parseTree = _1().c('a', 'a').c('b', 'b').ↄ(_2().a()).c('d').ↄ(_2().b()).ↄ(_2().a()).$();
    traverse(parseTree, 0);
  }
  private static void traverse(P p, int depth) {
    if (p instanceof P1)
      traverse((P1) p, depth);
    else
      traverse((P2) p, depth);
  }
  private static void traverse(P1 p, int depth) {
    for (int i = 0; i < depth; ++i)
      System.out.print('\t');
    System.out.println(p.c);
    traverse(p.p, depth + 1);
    for (int i = 0; i < depth; ++i)
      System.out.print('\t');
    if (p.AB instanceof AB1)
      System.out.println("@a");
    else
      System.out.println("@b");
    traverse(p.p2, depth);
  }
  @SuppressWarnings("unused") private static void traverse(P2 p, int depth) {
    // Relax.
  }
}
