package il.ac.technion.cs.fling.internal.grammar.rules;
/** Common name of tokens and variables in a Fluent grammar
 *
 * @author Yossi Gil */
public interface Symbol extends TempSymbol, Component, Named {
  @Override default Symbol normalize() {
    return this;
  }
}
