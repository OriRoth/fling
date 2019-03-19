package fling.grammar;

import java.util.function.Consumer;

import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;

public interface Namer {
  Symbol abbreviate(Symbol symbol, Consumer<Variable> newVariableCallback, Consumer<DerivationRule> newRuleCallback);
  // TODO add context to variable creation.
  Variable createChild(Variable v);
}
