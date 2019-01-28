package fling.sententials;

import java.util.function.Consumer;

import fling.grammar.Namer;

public interface Variable extends Symbol {
  @SuppressWarnings("unused") @Override default Variable abbreviate(Namer namer, Consumer<Variable> newVariableCallback,
      Consumer<DerivationRule> newRuleCallback) {
    return this;
  }
}
