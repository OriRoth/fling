package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.__;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.c;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.AB.a;
import static il.ac.technion.cs.fling.examples.generated.TaggedBalancedParentheses.AB.b;
import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB1;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.AB2;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P1;
import il.ac.technion.cs.fling.examples.generated.TaggedBalancedParenthesesAST.P2;
/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 *
 * @author Yossi Gil
 * @since April 2019 */
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
    $.append("\t".repeat(Math.max(0, depth)));
    $.append(p.c).append('\n');
    $.append(traverse(p.p, depth + 1));
    $.append("\t".repeat(Math.max(0, depth)));
    if (p.ↄ instanceof AB1)
      $.append("@a").append('\n');
    else
      $.append("@b").append(((AB2) p.ↄ).b).append('\n');
    $.append(traverse(p.p2, depth));
    return $.toString();
  }
  @SuppressWarnings("unused") private static String traverse(final P2 p, final int depth) {
    // Relax.
    return "";
  }
}
