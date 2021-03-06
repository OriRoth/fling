package il.ac.technion.cs.fling.internal.grammar.rules;
import static java.util.Objects.requireNonNull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
/** Represents a <strong>token</strong> of the fluent API grammar, which has two
 * components: a {@link Terminal}, which is the function name, and the possibly
 * empty list of arguments.
 *
 * @see Terminal
 * @author Yossi Gil
 * @since 2020-06-07 */
public final class Token implements Symbol {
  public final Terminal terminal;
  public final Parameter[] parameters;
  /** POJO instantiation of this class */
  public Token(final Terminal terminal, final Parameter... parameters) {
    this.terminal = requireNonNull(terminal);
    this.parameters = parameters;
  }
  @Override public boolean isParameterized() {
    return parameters.length != 0;
  }
  public Stream<Parameter> parameters() {
    return Stream.of(parameters);
  }
  @Override public String name() {
    return terminal.name();
  }
  @Override public int hashCode() {
    return 31 * Arrays.deepHashCode(parameters) + Objects.hash(terminal);
  }
  @Override public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Token))
      return false;
    final Token other = (Token) obj;
    return Arrays.equals(parameters, other.parameters) && Objects.equals(terminal, other.terminal);
  }
  @Override public String toString() {
    String $ = name();
    if (parameters.length != 0)
      $ += String.format("<%s>", parameters().map(Object::toString).collect(Collectors.joining(", ")));
    return $;
  }
  @Override public boolean isToken() {
    return true;
  }
}
