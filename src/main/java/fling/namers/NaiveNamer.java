package fling.namers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import fling.compiler.Namer;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.types.TypeParameter;

public class NaiveNamer implements Namer {
  Map<Variable, Integer> childrenCounter = new HashMap<>();
  private final Map<ClassNode, Map<String, Integer>> usedNames = new LinkedHashMap<>();
  private final Map<FieldNode, String> selectedNames = new LinkedHashMap<>();

  @SuppressWarnings("unused") @Override public Symbol abbreviate(Symbol symbol, Consumer<Variable> newVariableCallback,
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
  @Override public String getClassName(Variable v) {
    return v.name();
  }
  @Override public String getParameterName(Variable v, FieldNode identifier, ClassNode classContainer) {
    return getName(lowerCamelCase(v.name()), identifier, classContainer);
  }
  @Override public String getParameterType(TypeParameter t) {
    return t.typeName();
  }
  @Override public String getParameterName(TypeParameter t, FieldNode identifier, ClassNode classContainer) {
    return getName(t.parameterName(), identifier, classContainer);
  }
  private String getName(String baseName, FieldNode identifier, ClassNode classContainer) {
    if (selectedNames.containsKey(identifier))
      return selectedNames.get(identifier);
    if (!usedNames.containsKey(classContainer))
      usedNames.put(classContainer, new LinkedHashMap<>());
    Map<String, Integer> usedNamesInContext = usedNames.get(classContainer);
    if (!usedNamesInContext.containsKey(baseName)) {
      usedNamesInContext.put(baseName, 2);
      selectedNames.put(identifier, baseName);
      return baseName;
    }
    int position = usedNamesInContext.put(baseName, usedNamesInContext.get(baseName) + 1);
    String name = baseName + position;
    selectedNames.put(identifier, name);
    return name;
  }
  private static String lowerCamelCase(String string) {
    if (string.isEmpty())
      return string;
    return Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }
}
