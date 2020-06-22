package il.ac.technion.cs.fling.internal.grammar.rules;
/** Common name of tokens, terminals, and variables in a Fluent grammar
 *
 * @author Yossi Gil */
public interface TempSymbol extends TempComponent {
  @Override Symbol normalize();
}
