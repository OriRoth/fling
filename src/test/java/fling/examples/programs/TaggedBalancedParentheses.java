package fling.examples.programs;

import static fling.examples.generated.TaggedBalancedParentheses.__;
import static fling.examples.generated.TaggedBalancedParentheses.c;
import static fling.examples.generated.TaggedBalancedParentheses.AB.a;
import static fling.examples.generated.TaggedBalancedParentheses.AB.b;

import fling.examples.generated.TaggedBalancedParenthesesAST.AB1;
import fling.examples.generated.TaggedBalancedParenthesesAST.AB2;
import fling.examples.generated.TaggedBalancedParenthesesAST.P;
import fling.examples.generated.TaggedBalancedParenthesesAST.P1;
import fling.examples.generated.TaggedBalancedParenthesesAST.P2;

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
    System.out.println(traverse(parseTree, 0));
    System.out.println(traverse(__().$(), 0));
  }
  private static String traverse(P p, int depth) {
    return p instanceof P1 ? traverse((P1) p, depth) : traverse((P2) p, depth);
  }
  private static String traverse(P1 p, int depth) {
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < depth; ++i)
      $.append('\t');
    $.append(p.c).append('\n');
    $.append(traverse(p.p, depth + 1));
    for (int i = 0; i < depth; ++i)
      $.append('\t');
    if (p.ↄ instanceof AB1)
      $.append("@a").append('\n');
    else
      $.append("@b" + ((AB2) p.ↄ).b).append('\n');
    $.append(traverse(p.p2, depth));
    return $.toString();
  }
  @SuppressWarnings("unused") private static String traverse(P2 p, int depth) {
    // Relax.
    return "";
  }
}
