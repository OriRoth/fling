package il.ac.technion.cs.fling;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** An extended Backus-Naur form specification of formal Language, collection of
 * derivation rules of the form <code>v ::= w X | Y z.</code>, augmented with
 * lots of services which found shelter in this class.
 *
 * @author Ori Roth */
public class Follows extends Firsts {
  /** Maps variables and notations to their follows set */
  public final Map<Symbol, Set<Token>> follows = new LinkedHashMap<>();
  public Follows(BNF inner) {
    super(inner);
    variables().forEach(v -> follows.put(v, new LinkedHashSet<>()));
    tokens().forEach(v -> follows.put(v, new LinkedHashSet<>()));
    follows(start()).add(Token.$);
    worklist(() -> variables(), v -> exists(forms(v).filter(sf -> follows(v, sf))));
  }
  private boolean follows(Variable v, SF sf) {
    boolean $ = false;
    for (int i = 0; i < sf.size(); ++i) {
      Symbol u = sf.get(i);
      final List<Symbol> rest = sf.suffix(i + 1);
      $ |= follows(u).addAll(firsts(rest));
      if (nullable(rest))
        $ |= follows(u).addAll(follows(v));
    }
    return $;
  }
  Set<Token> follows(Symbol s) {
    return follows.get(s);
  }
}
