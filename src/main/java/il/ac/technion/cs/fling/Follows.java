package il.ac.technion.cs.fling;
import static java.util.stream.Collectors.toList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>v ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
public class Follows extends Firsts {
  /** Maps variables and notations to their follows set */
  public final Map<Variable, Set<Token>> follows = new LinkedHashMap<>();
  public Follows(BNF inner) {
    super(inner);
    variables().forEach(v -> follows.put(v, new LinkedHashSet<>()));
    follows.get(Constants.S).add(Constants.$$);
    worklist(() -> variables(), v -> {
      return forms(v).filter(sf -> {
        boolean $ = false;
        for (int i = 0; i < sf.size(); ++i) {
          if (sf.get(i) instanceof Variable current) {
            final List<Symbol> rest = sf.suffix(i);
            $ |= follows.get(current).addAll(firsts(rest));
            if (nullable(rest))
              $ |= follows.get(v).addAll(follows.get(current));
          }
        }
        return $;
      }).collect(toList()).isEmpty();
    });
  }
}
