package il.ac.technion.cs.fling;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class Firsts extends Nullables {
  /** Maps variables and notations to their firsts set */
  public final Map<Symbol, Set<Token>> firsts = new LinkedHashMap<>();
  public Firsts(BNF inner) {
    super(inner);
    tokens().forEach(t -> firsts.put(t, singleton(t)));
    variables().forEach(v -> firsts.put(v, new LinkedHashSet<>()));
    workset(() -> variables(), v -> exists(forms(v).filter(sf -> addFirsts(v, sf))));
  }
  private boolean addFirsts(Variable v, SF sf) {
    boolean $ = false;
    for (final Symbol s : sf.inner()) {
      $ |= firsts.get(v).addAll(firsts.get(s));
      if (!nullable(s))
        break;
    }
    return $;
  }
  static <T> boolean exists(Stream<T> ss) {
    return ss.collect(toList()).isEmpty();
  }
  Set<Token> firsts(final Collection<Symbol> symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!nullable(s))
        break;
    }
    return unmodifiableSet($);
  }
}
