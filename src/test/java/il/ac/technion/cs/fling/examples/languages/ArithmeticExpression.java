package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Γ.*;
import static il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Γ;
import il.ac.technion.cs.fling.examples.languages.ArithmeticExpression.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class ArithmeticExpression implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    plus, minus, times, divide, begin, end, v, n
  }
  public enum Γ implements Variable {
    E, E_, T, T_, F
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
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
