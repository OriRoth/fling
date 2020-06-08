package il.ac.technion.cs.fling.internal.grammar.sententials;

import java.util.*;
import java.util.stream.*;

import il.ac.technion.cs.fling.*;

public class ERule {
  public final Variable variable;
  private final List<ExtendedSententialForm> forms = new ArrayList<ExtendedSententialForm>();

  private static Stream<Variable> variables(Stream<Symbol> symbols) {
    return symbols.filter(Symbol::isVariable).map(Symbol::asVariable);
  }

  public boolean of(Variable v) {
    return v.equals(variable);
  }

  /** Construct an epsilon rule */
  public ERule(final Variable variable) {
    Objects.requireNonNull(variable);
    this.variable = variable;
    forms.add(new ExtendedSententialForm());
  }


  /** Construct an ordinary rule */
  public ERule(final Variable variable, final ExtendedSententialForm form) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(form);
    this.variable = variable;
    forms.add(form);
  }

  public ERule(final Variable variable, final Collection<ExtendedSententialForm> forms) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(forms);
    this.variable = variable;
    this.forms.addAll(forms);
    assert this.forms.size() > 0;
  }

  public Stream<ExtendedSententialForm> forms() {
    return forms.stream();
  }

  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ERule))
      return false;
    final ERule other = (ERule) o;
    return variable.equals(other.variable) && forms.equals(other.forms);
  }

  @Override public int hashCode() {
    int $ = 1;
    $ = $ * 31 + variable.hashCode();
    $ = $ * 31 + forms.hashCode();
    return $;
  }

  public Variable lhs() {
    return variable;
  }

  private Stream<Symbol> quantifiedSymbols() {
    return quantifiers().flatMap(Quantifier::symbols);
  }

  private Stream<Quantifier> quantifiers() {
    return symbols().filter(Symbol::isQuantifier).map(Symbol::asQuantifier);
  }

  public Iterable<ExtendedSententialForm> rhs() {
    return forms;
  }

  private Stream<Symbol> symbols() {
    return forms.stream().flatMap(Collection::stream);
  }

  @Override public String toString() {
    return String.format("%s::=%s", variable,
        String.join("|", forms.stream().map(sf -> sf.isEmpty() ? "ε" : sf.toString()).collect(Collectors.toList())));
  }

  public Stream<Variable> variables() {
    return Stream.concat(variables(symbols()), variables(quantifiedSymbols()));
  }

  public Stream<Token> tokens() {
    return Stream.concat(tokens(symbols()), tokens(quantifiedSymbols()));
  }

  private static Stream<Token> tokens(Stream<Symbol> symbols) {
    return symbols.filter(Symbol::isToken).map(Symbol::asToken);
  }
}
