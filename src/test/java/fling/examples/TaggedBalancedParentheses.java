package fling.examples;

import static fling.examples.TaggedBalancedParentheses.V.AB;
import static fling.examples.TaggedBalancedParentheses.V.P;
import static fling.examples.TaggedBalancedParentheses.Σ.a;
import static fling.examples.TaggedBalancedParentheses.Σ.b;
import static fling.examples.TaggedBalancedParentheses.Σ.c;
import static fling.examples.TaggedBalancedParentheses.Σ.ↄ;
/**
 * If you see compilation errors in the import section of this file, please run
 * {@link RunMeFirstToGenerateTests} to generate the fluent API that this test
 * checks. After running this file, please refresh the project.
 */
import static fling.generated.TaggedBalancedParentheses.__;
import static fling.generated.TaggedBalancedParentheses.c;
import static fling.generated.TaggedBalancedParentheses.AB.a;
import static fling.generated.TaggedBalancedParentheses.AB.b;
import static fling.grammar.BNF.bnf;

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
    c('a', 'a').ↄ(a()).$();
    c('a', 'a').ↄ(a()).ↄ(a());
    c('a', 'a').c('b').c('c').ↄ(a()).ↄ(a());
    c('a', 'a').c('b').c('c').ↄ(a()).ↄ(b()).ↄ(a()).$();
    c('a', 'a').c('b').ↄ(a()).ↄ(b()).c('e').ↄ(a()).$();
    c('a', 'a').c('b').ↄ(a()).ↄ(b()).c('e');
    __().$();
    // ↄ(a());
  }
  public static void main(String[] args) {
    P parseTree = c('a', 'a').c('b', 'b').ↄ(a()).c('d').ↄ(b()).ↄ(a()).$();
    System.out.println(traverse(parseTree, 0));
    System.out.println(traverse(__().$(), 0));
  }
  private static String traverse(P p, int depth) {
    return (p instanceof P1) ? traverse((P1) p, depth) : traverse((P2) p, depth);
  }
  private static String traverse(P1 p, int depth) {
    String $ = "";
    for (int i = 0; i < depth; ++i)
      $ += '\t';
    $ += p.c;
    $ += traverse(p.p, depth + 1);
    for (int i = 0; i < depth; ++i)
      $ += '\t';
    if (p.AB instanceof AB1)
      $ += "@a";
    else
      $ += "@b";
    $ += traverse(p.p2, depth);
    return $;
  }
  private static String traverse(P2 p, int depth) {
    return "";
  }
}
