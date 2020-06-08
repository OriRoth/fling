package il.ac.technion.cs.fling.internal.grammar.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ERule {
  public final Variable variable;
  private final List<Body> bodies = new ArrayList<>();

  public boolean of(Variable v) {
    return v.equals(variable);
  }

  /** Construct an epsilon rule */
  public ERule(final Variable variable) {
    Objects.requireNonNull(variable);
    this.variable = variable;
    bodies.add(new Body());
  }

  /** Construct an ordinary rule */
  public ERule(final Variable variable, final Body body) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(body);
    this.variable = variable;
    bodies.add(body);
  }

  public ERule(final Variable variable, final Collection<Body> forms) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(forms);
    this.variable = variable;
    this.bodies.addAll(forms);
    assert this.bodies.size() > 0;
  }

  public Stream<Body> bodies() {
    return bodies.stream();
  }

  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ERule))
      return false;
    final ERule other = (ERule) o;
    return variable.equals(other.variable) && bodies.equals(other.bodies);
  }

  @Override public int hashCode() {
    int $ = 1;
    $ = $ * 31 + variable.hashCode();
    $ = $ * 31 + bodies.hashCode();
    return $;
  }

  private Stream<Component> quantifiedSymbols() {
    return quantifiers().flatMap(Quantifier::symbols);
  }

  private Stream<Quantifier> quantifiers() {
    return components().filter(Component::isQuantifier).map(Component::asQuantifier);
  }

  public Iterable<Body> bodiesList() {
    return bodies;
  }

  @Override public String toString() {
    return String.format("%s->%s", variable,
        String.join("|", bodies().map(b -> b.isEmpty() ? "ε" : b.toString()).collect(Collectors.toList())));
  }

  public Stream<Variable> variables() {
    return Stream.concat(variables(components()), variables(quantifiedSymbols()));
  }

  public Stream<Token> tokens() {
    return Stream.concat(tokens(components()), tokens(quantifiedSymbols()));
  }

  private Stream<Component> components() {
    return bodies().flatMap(Collection::stream);
  }

  private static Stream<Token> tokens(Stream<Component> symbols) {
    return symbols.filter(Component::isToken).map(Component::asToken);
  }

  private static Stream<Variable> variables(Stream<Component> symbols) {
    return symbols.filter(Component::isVariable).map(Component::asVariable);
  }
}
