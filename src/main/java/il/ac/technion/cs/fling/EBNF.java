package il.ac.technion.cs.fling;

import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.sententials.Constants;
import il.ac.technion.cs.fling.internal.grammar.sententials.ERule;
import il.ac.technion.cs.fling.internal.grammar.sententials.ExtendedSententialForm;
import il.ac.technion.cs.fling.internal.grammar.sententials.Token;

/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>V ::= w X | Y z.</code>
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

  /** @return all grammar symbols */
  public Stream<Symbol> symbols() {
    return Stream.concat(Σ.stream(), Γ.stream());
  }

  /** @param v a variable
   * @return stream of right hand sides of all its derivation rule */
  public final Stream<ExtendedSententialForm> forms(final Variable v) {
    return rules(v).flatMap(ERule::forms);
  }

  /** @param v a variable
   * @return a list of the right hand side of all its derivation rule */
  public final List<ExtendedSententialForm> formsList(final Variable v) {
    return rules(v).flatMap(ERule::forms).collect(Collectors.toList());
  }
}
