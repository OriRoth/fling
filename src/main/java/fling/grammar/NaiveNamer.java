package fling.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;

public class NaiveNamer implements Namer {
  Map<Variable, Integer> childrenCounter = new HashMap<>();

  @Override public Symbol abbreviate(Symbol symbol, Consumer<Variable> newVariableCallback,
      Consumer<DerivationRule> newRuleCallback) {
    // TODO support regular expressions.
    return symbol;
  }
  @Override public Variable createChild(Variable v) {
    if (!childrenCounter.containsKey(v))
      childrenCounter.put(v, 1);
    String name = v.name() + childrenCounter.put(v, childrenCounter.get(v) + 1);
    return new Variable() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (!(obj instanceof Variable))
          return false;
        Variable other = (Variable) obj;
        return name().equals(other.name());
      }
    };
  }
}
