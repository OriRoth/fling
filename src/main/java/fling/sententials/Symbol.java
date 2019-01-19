package fling.sententials;

import java.util.function.Consumer;

import fling.grammar.Namer;

public interface Symbol {
  default boolean isTerminal() {
    return this instanceof Terminal || Constants.$.equals(this);
  }
  default boolean isVariable() {
    return this instanceof Variable || Constants.S.equals(this);
  }
  default Terminal asTerminal() {
    return (Terminal) this;
  }
  default Variable asVariable() {
    return (Variable) this;
  }
  @SuppressWarnings("unused") default Symbol expand(Namer namer, Consumer<Variable> newVariableCallback,
      Consumer<DerivationRule> newRuleCallback) {
    return this;
  }
}
