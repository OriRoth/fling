package fling.languages;

import static fling.generated.TaggedBalancedParentheses.__;
import static fling.generated.TaggedBalancedParentheses.c;
import static fling.generated.TaggedBalancedParentheses.AB.a;
import static fling.generated.TaggedBalancedParentheses.AB.b;
import static fling.grammar.BNF.bnf;
import static fling.grammar.sententials.Notation.oneOrMore;
import static fling.languages.TaggedBalancedParentheses.V.AB;
import static fling.languages.TaggedBalancedParentheses.V.P;
import static fling.languages.TaggedBalancedParentheses.Σ.a;
import static fling.languages.TaggedBalancedParentheses.Σ.b;
import static fling.languages.TaggedBalancedParentheses.Σ.c;
import static fling.languages.TaggedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaMediator;
import fling.generated.TaggedBalancedParenthesesAST.AB1;
import fling.generated.TaggedBalancedParenthesesAST.AB2;
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
      derive(AB, oneOrMore(b.with(int.class))). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "TaggedBalancedParentheses", Σ.class);

  public static void compilationTest() {
    c('a', 'a').ↄ(a()).$();
    c('a', 'a').ↄ(a()).ↄ(a());
    c('a', 'a').c('b').c('c').ↄ(a()).ↄ(a());
    c('a', 'a').c('b').c('c').ↄ(a()).ↄ(b(1).b(2)).ↄ(a()).$();
    c('a', 'a').c('b').ↄ(a()).ↄ(b(1).b(2)).c('e').ↄ(a()).$();
    c('a', 'a').c('b').ↄ(a()).ↄ(b(1).b(2)).c('e');
    __().$();
    // ↄ(a());
  }
  public static void main(String[] args) {
    P parseTree = c('a', 'a').c('b', 'b').ↄ(a()).c('d').ↄ(b(1).b(2)).ↄ(a()).$();
    traverse(parseTree, 0);
    traverse(__().$(), 0);
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
      System.out.println("@b" + ((AB2) p.AB).i);
    traverse(p.p2, depth);
  }
  @SuppressWarnings("unused") private static void traverse(P2 p, int depth) {
    // Relax.
  }
}
