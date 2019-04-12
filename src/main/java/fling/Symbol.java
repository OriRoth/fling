package fling;

import fling.internal.grammar.sententials.*;
import fling.internal.grammar.sententials.notations.*;

public interface Symbol extends Named {
  default boolean isTerminal() {
    return this instanceof Terminal || Constants.$.equals(this);
  }
  default boolean isVariable() {
    return this instanceof Variable || Constants.S.equals(this);
  }
  default boolean isVerb() {
    return this instanceof Verb || Constants.$$.equals(this);
  }
  default boolean isNotation() {
    return this instanceof Notation;
  }
  default Terminal asTerminal() {
    return (Terminal) this;
  }
  default Variable asVariable() {
    return (Variable) this;
  }
  default Verb asVerb() {
    return (Verb) this;
  }
  default Notation asNotation() {
    return (Notation) this;
  }
  static NoneOrMore noneOrMore(final Symbol symbol) {
    return new NoneOrMore(!symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal()));
  }
  static OneOrMore oneOrMore(final Symbol symbol) {
    return new OneOrMore(!symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal()));
  }
}
