package il.ac.technion.cs.fling.internal.grammar.rules;

import il.ac.technion.cs.fling.Named;

/** A component of a body of a {@link ERule}
 * 
 * @author Yossi Gil
 * @since 2020-06-07 */
public interface TempComponent extends Named {
  default boolean isTerminal() {
    return this instanceof Terminal || Constants.$.equals(this);
  }

  default boolean isVariable() {
    return this instanceof Variable || Constants.S.equals(this);
  }

  /** @return true iff receiver is {@Link Token} */
  default boolean isToken() {
    return false;
  }

  default boolean isQuantifier() {
    return this instanceof Quantifier;
  }

  default Terminal asTerminal() {
    return (Terminal) this;
  }

  default Variable asVariable() {
    return (Variable) this;
  }

  default Token asToken() {
    return (Token) this;
  }

  default Quantifier asQuantifier() {
    return (Quantifier) this;
  }

  default boolean isParameterized() {
    return false;
  }

  Component normalize();

}
