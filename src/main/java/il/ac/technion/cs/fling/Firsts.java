package il.ac.technion.cs.fling;
import java.util.*;
import static java.util.Collections.singleton;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
public class Firsts extends Nullables {
  /** Maps variables and notations to their firsts set */
  public final Map<Symbol, Set<Token>> firsts = new LinkedHashMap<>();
  public Firsts(BNF inner) {
    super(inner);
    tokens().forEach(t -> firsts.put(t, singleton(t)));
    variables().forEach(v -> firsts.put(v, new LinkedHashSet<>()));
    worklist(() -> variables(), v -> exists(forms(v).filter(sf -> firsts(v).addAll(firsts(sf.inner())))));
  }
  Set<Token> firsts(final Collection<Symbol> symbols) {
    final Set<Token> $ = new LinkedHashSet<>();
    for (final Symbol s : symbols) {
      $.addAll(firsts(s));
      if (!nullable(s))
        break;
    }
    return $;
  }
  Set<Token> firsts(Symbol s) {
    return firsts.get(s);
  }
  public Set<Token> firsts(Variable v) {
    return firsts.get(v);
  }
}
