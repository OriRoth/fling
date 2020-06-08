package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.internal.grammar.sententials.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.notations.*;

public interface Symbol extends Named {
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

  static Optional optional(final Symbol symbol) {
    return new Optional(!symbol.isTerminal() ? symbol : new Token(symbol.asTerminal()));
  }

  static NoneOrMore noneOrMore(final Symbol symbol) {
    return new NoneOrMore(!symbol.isTerminal() ? symbol : new Token(symbol.asTerminal()));
  }

  static OneOrMore oneOrMore(final Symbol symbol) {
    return new OneOrMore(!symbol.isTerminal() ? symbol : new Token(symbol.asTerminal()));
  }

  default boolean isParameterized() {
    return false;
  }

  default Symbol normalize() {
    return this;
  }
}
