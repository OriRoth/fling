package fling.languages;

import static fling.generated.BalancedParentheses.__;
import static fling.grammar.BNF.bnf;
import static fling.languages.BalancedParentheses.V.P;
import static fling.languages.BalancedParentheses.Σ.c;
import static fling.languages.BalancedParentheses.Σ.ↄ;

import fling.compiler.Compiler;
import fling.compiler.JavaAdapter;
import fling.grammar.BNF;
import fling.grammar.LL1;
import fling.grammar.NaiveNamer;
import fling.sententials.Terminal;
import fling.sententials.Variable;

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
  public static final String fluentAPI = new JavaAdapter<>("fling.generated", "BalancedParentheses", "__",
      "$").printFluentAPI(new Compiler<>(new LL1(bnf, new NaiveNamer()).toDPDA()).compileFluentAPI());

  public static void compilationTest() {
    __().c().ↄ().$();
    __().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ().ↄ().$();
    __().c().c().ↄ().ↄ().c().ↄ().$();
    __().c().c().ↄ().ↄ().c();
  }
}
