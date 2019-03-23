package fling.languages;

import static fling.generated.BalancedParentheses.__;
import static fling.grammar.BNF.bnf;
import static fling.languages.BalancedParentheses.V.P;
import static fling.languages.BalancedParentheses.Σ.c;
import static fling.languages.BalancedParentheses.Σ.ↄ;

import fling.adapters.JavaAPIAdapter;
import fling.adapters.JavaInterfacesASTAdapter;
import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.ast.ASTCompiler;
import fling.grammar.BNF;
import fling.grammar.Grammar;
import fling.grammar.LL1;
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
  private static Namer namer = new NaiveNamer();
  public static final String fluentAPI = new JavaAPIAdapter<>("fling.generated", "BalancedParentheses", "__", "$", namer)
      .printFluentAPI(new APICompiler<>(new LL1(bnf, namer).toDPDA()).compileFluentAPI());

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
    Grammar g = new LL1(bnf, namer);
    System.out.println(new JavaInterfacesASTAdapter("fling.generated", "BalancedParenthesesAST", namer)
        .printASTClass(new ASTCompiler(g.normalizedBNF).compileAST()));
  }
}
