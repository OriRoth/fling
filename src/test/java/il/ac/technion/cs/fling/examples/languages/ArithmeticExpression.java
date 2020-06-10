package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.E;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.E_;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.F;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.T;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V.T_;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.begin;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.divide;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.end;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.minus;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.n;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.plus;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.times;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.v;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.V;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

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

  @Override public il.ac.technion.cs.fling.EBNF BNF() {
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
