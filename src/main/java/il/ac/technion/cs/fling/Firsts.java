package il.ac.technion.cs.fling;
import java.util.*;
import static java.util.Collections.singleton;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
class Firsts extends Nullables {
  /** Maps variables and notations to their firsts set */
  private final Map<Symbol, Set<Token>> firsts = new LinkedHashMap<>();
  public Firsts(final BNF inner) {
    super(inner);
    tokens().forEach(t -> firsts.put(t, singleton(t)));
    variables().forEach(v -> firsts.put(v, new LinkedHashSet<>()));
    worklist(this::variables, v -> exists(forms(v).filter(sf -> firsts(v).addAll(firsts(sf.inner())))));
  }
  Set<Token> firsts(final Iterable<Symbol> symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Symbol s : symbols) {
      $.addAll(firsts(s));
      if (!nullable(s))
        break;
    }
    return $;
  }
  private Set<Token> firsts(final Symbol s) {
    return firsts.get(s);
  }
  public Set<Token> firsts(final Variable v) {
    return firsts.get(v);
  }
}
