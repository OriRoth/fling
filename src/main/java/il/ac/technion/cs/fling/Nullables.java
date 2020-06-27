package il.ac.technion.cs.fling;
import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>v ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
public class Nullables extends BNF.Decorator {
  public final Set<Variable> nullables = new LinkedHashSet<>();
  public Nullables(BNF inner) {
    super(inner);
    workset(() -> variables(), v -> forms(v).anyMatch(this::nullable));
  }
  boolean nullable(SF ss) {
    return nullable(ss.symbols());
  }
  boolean nullable(Stream<Symbol> ss) {
    return ss.allMatch(this::nullable);
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
  protected static <T> void workset(Supplier<Stream<T>> source, Predicate<T> u) {
    while (!source.get().filter(u).collect(toList()).isEmpty())
      continue;
  }
}
