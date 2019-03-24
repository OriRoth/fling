package fling.languages;

import static fling.generated.BalancedParentheses.__;
import static fling.grammar.BNF.bnf;
import static fling.languages.BalancedParentheses.V.P;
import static fling.languages.BalancedParentheses.Σ.c;
import static fling.languages.BalancedParentheses.Σ.ↄ;

import fling.adapters.JavaCompleteAdapter;
import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.ast.ASTCompiler;
import fling.grammar.BNF;
import fling.grammar.LL1;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import fling.namers.NaiveNamer;

public class BalancedParentheses {
  enum Σ implements Terminal {
    c, ↄ
  }

  enum V implements Variable {
    P
  }

  public static final BNF bnf = bnf(Σ.class, V.class). //
      start(P). //
      derive(P, c, P, ↄ, P). //
      derive(P). //
      build();
  private static final Namer namer = new NaiveNamer();
  private static final LL1 grammar = new LL1(bnf, namer);
  private static final JavaCompleteAdapter<Named, Terminal, Named> adapter = new JavaCompleteAdapter<>("fling.generated",
      "BalancedParentheses", "__", "$", namer);
  public static final String astClasses = adapter.printASTClass(new ASTCompiler(grammar.normalizedBNF).compileAST());
  public static final String fluentAPI = adapter.printFluentAPI(new APICompiler<>(grammar.toDPDA()).compileFluentAPI());

  public static void compilationTest() {
    __().c().ↄ().$();
    __().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ().ↄ().$();
    __().c().c().ↄ().ↄ().c().ↄ().$();
    __().c().c().ↄ().ↄ().c();
    __().ↄ();
  }
  public static void main(String[] args) {
    System.out.println(astClasses);
  }
}
