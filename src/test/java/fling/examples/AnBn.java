package fling.examples;

/**
 * If you see compilation errors in the import section of this file, please run
 * {@link RunMeFirstToGenerateTests} to generate the fluent API that this test
 * checks. After running this file, please refresh the project.
 */
import static fling.DPDA.dpda;
import static fling.examples.AnBn.Q.q0;
import static fling.examples.AnBn.Q.q1;
import static fling.examples.AnBn.Q.q2;
import static fling.examples.AnBn.Γ.E;
import static fling.examples.AnBn.Γ.X;
import static fling.examples.AnBn.Σ.a;
import static fling.examples.AnBn.Σ.b;
import static fling.generated.AnBn.a;
import static fling.grammar.sententials.Alphabet.ε;

import fling.DPDA;
import fling.adapters.CppAPIAdapter;
import fling.adapters.JavaAPIAdapter;
import fling.compiler.api.APICompiler;
import fling.grammar.Grammar;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Verb;
import fling.namers.NaiveNamer;

/**
 * @author yogi
 */
public class AnBn {
  enum Q implements Named {
    q0, q1, q2
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    E, X
  }

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go());
  public static final String JavaFluentAPI = new JavaAPIAdapter("fling.generated", "AnBn", "$",
      new NaiveNamer("fling.generated", "AnBn")) //
          .printFluentAPI(new APICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("fling.generated", "AnBn")) //
      .printFluentAPI(new APICompiler(dpda).compileFluentAPI());

  public static void compilationTest() {
    a().a().a().b().b().b().$();
    a().a().a().b().b();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
