package fling.languages.examples;

import static fling.generated.TaggedBalancedParentheses.__;
import static fling.generated.TaggedBalancedParentheses.c;
import static fling.generated.TaggedBalancedParentheses.AB.a;
import static fling.generated.TaggedBalancedParentheses.AB.b;

import fling.generated.TaggedBalancedParenthesesAST.AB1;
import fling.generated.TaggedBalancedParenthesesAST.AB2;
import fling.generated.TaggedBalancedParenthesesAST.P;
import fling.generated.TaggedBalancedParenthesesAST.P1;
import fling.generated.TaggedBalancedParenthesesAST.P2;

public class TaggedBalancedParentheses {
  public static void compilationTest() {
    c('a', 'a').ↄ(a()).$();
    // c('a', 'a').ↄ(a()).ↄ(a());
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
    if (p.ↄ instanceof AB1)
      System.out.println("@a");
    else
      System.out.println("@b" + ((AB2) p.ↄ).b);
    traverse(p.p2, depth);
  }
  @SuppressWarnings("unused") private static void traverse(P2 p, int depth) {
    // Relax.
  }
}
