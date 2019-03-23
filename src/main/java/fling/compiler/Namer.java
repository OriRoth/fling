package fling.compiler;

import java.util.function.Consumer;

import fling.compiler.api.APICompiler;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public interface Namer {
  Symbol abbreviate(Symbol symbol, Consumer<Variable> newVariableCallback, Consumer<DerivationRule> newRuleCallback);
  // TODO add context to variable creation.
  Variable createChild(Variable v);
  void name(ASTCompilationUnitNode compilationUnit);
  <Q extends Named, Σ extends Terminal, Γ extends Named> void name(
      APICompilationUnitNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI);
}
