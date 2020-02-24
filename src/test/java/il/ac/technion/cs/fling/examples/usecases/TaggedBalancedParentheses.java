package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.*;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.AB.*;

import il.ac.technion.cs.fling.examples.ExamplesMainRunMeFirst;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.*;
/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
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
  public static void main(final String[] args) {
    final P parseTree = c('a', 'a').c('b', 'b').ↄ(a()).c('d').ↄ(b(1).b(2)).ↄ(a()).$();
    System.out.println(traverse(parseTree, 0));
    System.out.println(traverse(__().$(), 0));
  }
  private static String traverse(final P p, final int depth) {
    return p instanceof P1 ? traverse((P1) p, depth) : traverse((P2) p, depth);
  }
  private static String traverse(final P1 p, final int depth) {
    final StringBuilder $ = new StringBuilder();
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
  @SuppressWarnings("unused") private static String traverse(final P2 p, final int depth) {
    // Relax.
    return "";
  }
}
