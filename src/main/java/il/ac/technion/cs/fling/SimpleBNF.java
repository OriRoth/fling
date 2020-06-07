package il.ac.technion.cs.fling;

import static java.util.Collections.unmodifiableSet;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import il.ac.technion.cs.fling.internal.grammar.sententials.Constants;
import il.ac.technion.cs.fling.internal.grammar.sententials.DerivationRule;
import il.ac.technion.cs.fling.internal.grammar.sententials.ExtendedSententialForm;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;

public class SimpleBNF {
  /** Verbs collection */
  public final Set<Verb> Σ;
  /** Variables collection */
  public final Set<Variable> Γ;
  /** Start variable */
  public final Variable ε;
  /** Derivation rules collection */
  public final Set<DerivationRule> R;
  public SimpleBNF(Set<Verb> Σ, Set<Variable> Γ, Variable ε, Set<DerivationRule> R) {
    this.Σ = Σ;
    this.Γ = Γ;
    this.ε = ε;
    this.R = R;
    Σ.add(Constants.$$);
  }
  /** @return all grammar symbols */
  public Set<GeneralizedSymbol> symbols() {
    final Set<GeneralizedSymbol> $ = new LinkedHashSet<>();
    $.addAll(Σ);
    $.addAll(Γ);
    return unmodifiableSet($);
  }
  /**
   * @param v a variable
   * @return the right hand side of its derivation rule
   */
  public List<ExtendedSententialForm> rhs(final Variable v) {
    return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
  }
}
