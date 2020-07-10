package il.ac.technion.cs.fling.internal.grammar.rules;
/** A component of a body of a {@link ERule}
 *
 * @author Yossi Gil
 * @since 2020-06-07 */
public interface Component extends TempComponent {
  @Override default Component normalize() {
    return this;
  }
  @Override default boolean isTerminal() {
    return this instanceof Terminal || Terminal.$.equals(this);
  }
  @Override default boolean isVariable() {
    return this instanceof Variable || Constants.S.equals(this);
  }
  /** @return true iff receiver is {@Link Token} */
  @Override default boolean isToken() {
    return false;
  }
  @Override default boolean isQuantifier() {
    return this instanceof Quantifier;
  }
  @Override default Variable asVariable() {
    return (Variable) this;
  }
  @Override default boolean isParameterized() {
    return false;
  }
}
