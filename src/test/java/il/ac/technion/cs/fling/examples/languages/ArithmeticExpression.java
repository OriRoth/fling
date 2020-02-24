package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.*;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;

public class ArithmeticExpression {
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
      "il.ac.technion.cs.fling.examples.generated", "ArithmeticExpression", Σ.class);
}
