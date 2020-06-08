package il.ac.technion.cs.fling;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** An extended Backus-Naur form specification of a formal Language, represented
 * as a set of {@link #R} of extended derivation rules {@link ERule}.
 * 
 * @author Yossi Gil */
public class EBNF {
  @Override public String toString() {
    return "EBNF[Σ=" + Σ + ", Γ=" + Γ + ", ε=" + ε + ", R=" + R + "]";
  }

  /** Tokens' vocabulary */
  public final Set<Token> Σ;
  /** Variables' vocabulary */
  public final Set<Variable> Γ;
  /** Start variable */
  public final Variable ε;
  /** Derivation rules collection */
  public final Set<ERule> R;

  public EBNF(Set<Token> Σ, Set<Variable> Γ, Variable ε, Set<ERule> R) {
    this.Σ = Σ;
    this.Γ = Γ;
    this.ε = ε;
    this.R = R;
    Σ.add(Constants.$$);
    verify();

  }

  void verify() {
    assert R.size() > 0;
    assert Γ.contains(ε);
  }

  /** @return all rules in this instance */
  public Stream<ERule> rules() {
    return R.stream();
  }

  /** @return all rules defining a variable */
  public Stream<ERule> rules(Variable v) {
    return R.stream().filter(r -> r.of(v));
  }

  /** @return stream of all grammar symbols */
  public Stream<Symbol> symbols() {
    return Stream.concat(Σ.stream(), Γ.stream());
  }

  /** @param v a variable
   * @return stream of right hand sides of all its derivation rule */
  public final Stream<Body> bodies(final Variable v) {
    return rules(v).flatMap(ERule::bodies);
  }

  /** @param v a variable
   * @return a list of the right hand side of all its derivation rule */
  public final List<Body> bodiesList(final Variable v) {
    return rules(v).flatMap(ERule::bodies).collect(Collectors.toList());
  }
}
