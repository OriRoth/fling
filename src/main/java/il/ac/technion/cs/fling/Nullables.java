package il.ac.technion.cs.fling;
import java.util.*;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form {@code v ::= w X | Y z.}, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
class Nullables extends BNF.Decorator {
  private final Set<Variable> nullables = new LinkedHashSet<>();
  public Nullables(final BNF inner) {
    super(inner);
    worklist(this::variables, v -> forms(v).anyMatch(this::nullable) && nullables.add(v));
  }
  private boolean nullable(final SF ss) {
    return nullable(ss.symbols());
  }
  private boolean nullable(final Stream<Symbol> ss) {
    return ss.allMatch(this::nullable);
  }
  boolean nullable(final Symbol s) {
    if (s instanceof Variable v)
      return nullables.contains(v);
    return false;
  }
  /** @param ss sequence of grammar symbols
   * @return whether the sequence, as a whole is nullable */
  boolean nullable(final List<Symbol> ss) {
    return ss.stream().allMatch(this::nullable);
  }
  /** @param ss sequence of grammar symbols
   * @return whether the sequence as a whole is nullable */
  boolean nullable(final Symbol... ss) {
    return nullable(Arrays.asList(ss));
  }
}
