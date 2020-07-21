package il.ac.technion.cs.fling._;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form {@code v ::= w X | Y z.}, augmented with lots of
 * services which found shelter in this class.
 *
 * @author Ori Roth */
public class Follows extends Firsts {
  /** Maps variables and notations to their follows set */
  private final Map<Symbol, Set<Token>> follows = new LinkedHashMap<>();
  public Follows(final BNF inner) {
    super(inner);
    symbols().forEach(v -> follows.put(v, new LinkedHashSet<>()));
    follows(start()).add(Token.$);
    worklist(this::variables, v -> exists(forms(v).filter(sf -> follows(v, sf))));
  }
  private boolean follows(final Variable v, final SF sf) {
    var $ = false;
    for (var i = 0; i < sf.size(); ++i) {
      final var u = sf.get(i);
      final List<Symbol> rest = sf.suffix(i + 1);
      $ |= follows(u).addAll(firsts(rest));
      if (nullable(rest))
        $ |= follows(u).addAll(follows(v));
    }
    return $;
  }
  Set<Token> follows(final Symbol s) {
    return follows.get(s);
  }
}
