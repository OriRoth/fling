package fling.languages;

import static fling.generated.TaggedBalancedParentheses.__;
import static fling.grammar.BNF.bnf;
import static fling.languages.TaggedBalancedParentheses.V.P;
import static fling.languages.TaggedBalancedParentheses.Σ.c;
import static fling.languages.TaggedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaCompleteAdapter;
import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.ast.ASTCompiler;
import fling.compiler.ast.ASTParserCompiler;
import fling.generated.TaggedBalancedParenthesesAST.P;
import fling.generated.TaggedBalancedParenthesesAST.P1;
import fling.generated.TaggedBalancedParenthesesAST.P2;
import fling.grammar.BNF;
import fling.grammar.LL1;
import fling.grammar.LL1JavaASTParserCompiler;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import fling.namers.NaiveNamer;

public class TaggedBalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ
  }

  public enum V implements Variable {
    P
  }

  public static final BNF bnf = bnf(V.class). //
      start(P). //
      derive(P, c.many(char.class), P, ↄ.with(char.class), P). //
      derive(P). //
      build();
  public static final Namer namer = new NaiveNamer();
  public static final LL1 grammar = new LL1(bnf, namer);
  public static final ASTParserCompiler astParserCompiler = new LL1JavaASTParserCompiler<>(grammar.normalizedBNF, Σ.class,
      "TaggedBalancedParenthesesAST", "fling.generated", "TaggedBalancedParenthesesCompiler");
  public static final JavaCompleteAdapter adapter = new JavaCompleteAdapter("fling.generated", "TaggedBalancedParentheses", "__",
      "$", namer, Σ.class, astParserCompiler);
  public static final String astClasses = adapter.printASTClass(new ASTCompiler(grammar.normalizedBNF).compileAST());
  public static final String fluentAPI = adapter.printFluentAPI(new APICompiler(grammar.toDPDA()).compileFluentAPI());
  public static final String astParser = adapter.printASTParser();

  public static void compilationTest() {
    __().c('a').ↄ('b').$();
    __().c('a').ↄ('b').ↄ('c');
    __().c('a').c('b').c('c').ↄ('d').ↄ('e');
    __().c('a').c('b').c('c').ↄ('d').ↄ('e').ↄ('f').$();
    __().c('a').c('b').ↄ('c').ↄ('d').c('e').ↄ('f').$();
    __().c('a').c('b').ↄ('c').ↄ('d').c('e');
    __().ↄ('a');
  }
  public static void main(String[] args) {
    P parseTree = __().c('a', 'a').c('b').ↄ('c').c('d').ↄ('e').ↄ('f').$();
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
    System.out.println(p.c2);
    traverse(p.p2, depth);
  }
  @SuppressWarnings("unused") private static void traverse(P2 p, int depth) {
    // Relax.
  }
}
