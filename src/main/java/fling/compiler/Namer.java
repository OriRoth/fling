package fling.compiler;

import java.util.function.Consumer;

import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.types.TypeParameter;

public interface Namer {
  Symbol abbreviate(Symbol symbol, Consumer<Variable> newVariableCallback, Consumer<DerivationRule> newRuleCallback);
  // TODO add context to variable creation.
  Variable createChild(Variable v);
  String getClassName(Variable v);
  String getParameterName(Variable v, FieldNode identifier, ClassNode classContainer);
  String getParameterType(TypeParameter t);
  String getParameterName(TypeParameter t, FieldNode identifier, ClassNode classContainer);
}
