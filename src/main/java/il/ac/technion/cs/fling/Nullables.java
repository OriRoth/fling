package il.ac.technion.cs.fling;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;
import static java.util.Collections.singleton;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>v ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
public class Nullables extends BNF.Decorator {
  public final Set<Variable> nullables = new LinkedHashSet<>();
  public Nullables(BNF inner) {
    super(inner);
    worklist(() -> variables(), v -> forms(v).anyMatch(this::nullable) && nullables.add(v));
  }
  boolean recursive() {
    return uses(start()).contains(start());
  }
  Stream<Word<Token>> expand(Variable v) {
    return closure(SF.of(v), (Function<SF, Stream<SF>>) sf -> expand(sf)).stream().filter(SF::isGrounded)
        .map(SF::tokens);
  }
  private Stream<SF> expand(SF sf) {
    Stream<SF> $ = Stream.empty();
    for (int i = 0; i < sf.size(); ++i)
      if (sf.get(i) instanceof Variable v) {
        final int j = i;
        $ = Stream.concat($, forms(v).map(f -> sf.replace(j, f)));
      }
    return $;
  }
  boolean nullable(SF ss) {
    return nullable(ss.symbols());
  }
  boolean nullable(Stream<Symbol> ss) {
    return ss.allMatch(this::nullable);
  }
  Set<Variable> uses(Variable v) {
    return closure(v, u -> variables(symbols(u)));
  }
  private Stream<Symbol> symbols(Variable v) {
    return Stream.of(v).flatMap(this::forms).flatMap(SF::symbols);
  }
  static Stream<Variable> variables(Stream<Symbol> ss) {
    return ss.filter(Variable.class::isInstance).map(Variable.class::cast);
  }
  boolean nullable(final Symbol s) {
    if (s instanceof Variable v)
      return nullables.contains(v);
    return false;
  }
  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  boolean nullable(final List<Symbol> ss) {
    return ss.stream().allMatch(this::nullable);
  }
  /** @param symbols sequence of grammar symbols
   * @return whether the sequence is nullable */
  boolean nullable(final Symbol... ss) {
    return nullable(Arrays.asList(ss));
  }
  static <T> Set<T> closure(Set<T> ts, Function<T, Stream<T>> expand) {
    Set<T> $ = new LinkedHashSet<>();
    Set<T> current = ts;
    do
      current = current.stream().flatMap(expand::apply).collect(toSet());
    while ($.addAll(current));
    return $;
  }
  static <T> Set<T> closure(T t, Function<T, Stream<T>> expand) {
    return closure(singleton(t), expand);
  }
  static <T> void worklist(Supplier<Stream<T>> s, Predicate<T> u) {
    while (exists(s.get().filter(u)))
      continue;
  }
  protected static <T> boolean exists(Stream<T> ss) {
    return !ss.collect(toList()).isEmpty();
  }
}
