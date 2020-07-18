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
  public static final Token $ = Token.of(Terminal.$);
  private final Terminal terminal;
  public final Parameter[] parameters;
  public static Token of(final Terminal t) {
    return new Token(t);
  }
  public static Token of(final String s) {
    return of(Terminal.of(s));
  }
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
  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Token other))
      return false;
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
  public Token with(final Class<?> c) {
    return new Token(terminal, Parameter.of(c));
  }
}
