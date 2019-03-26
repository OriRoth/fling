package fling.languages;

import static fling.automata.DPDA.dpda;
import static fling.generated.LateFailure.__;
import static fling.languages.LateFailure.Q.q0;
import static fling.languages.LateFailure.Q.q1;
import static fling.languages.LateFailure.Q.q2;
import static fling.languages.LateFailure.Q.q3;
import static fling.languages.LateFailure.Γ.E;
import static fling.languages.LateFailure.Σ.a;
import static fling.languages.LateFailure.Σ.b;

import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compiler.api.APICompiler;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.namers.NaiveNamer;

public class LateFailure {
  enum Q implements Named {
    q0, q1, q2, q3
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    E
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q1) //
      .γ0(E) //
      .δ(q0, a, E, q1).δ(q0, b, E, q2, E).δ(q2, b, E, q3, E).go();
  public static final String JavaFluentAPI = new JavaAPIAdapter<Q, Σ, Γ>("fling.generated", "LateFailure", "__", "$",
      new NaiveNamer()) //
          .printFluentAPI(new APICompiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().$();
    __().b().b();
  }
}
