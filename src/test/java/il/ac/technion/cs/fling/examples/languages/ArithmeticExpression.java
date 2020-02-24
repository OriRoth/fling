package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.*;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.*;

public class ArithmeticExpression implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    plus, minus, times, divide, begin, end, v, n
  }

  public enum V implements Variable {
    E, E_, T, T_, F
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }
  @Override public BNF BNF() {
    return bnf(). //
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
  }
}
