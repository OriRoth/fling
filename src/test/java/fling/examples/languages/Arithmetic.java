package fling.examples.languages;

import static fling.examples.languages.Arithmetic.V.*;
import static fling.examples.languages.Arithmetic.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.*;

public class Arithmetic {
  public enum Σ implements Terminal {
    plus, minus, times, divide, begin, end, v, n
  }

  public enum V implements Variable {
    E, E_, T, T_, F
  }

  public static final BNF bnf = bnf(). //
      start(E). //
      derive(E).to(T, E_). //
      derive(E_).to(plus, T, E_). //
      derive(E_).to(minus, T, E_). //
      derive(E_).toEpsilon(). //
      derive(T).to(F, T_). //
      derive(T_).to(times, F, T_). //
      derive(T_).to(divide, F, T_). //
      derive(T_).toEpsilon(). //
      derive(F).to(begin, E, end). //
      derive(F).to(v.with(String.class)). //
      derive(F).to(n.with(double.class)). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "Arithmetic", Σ.class);
}
