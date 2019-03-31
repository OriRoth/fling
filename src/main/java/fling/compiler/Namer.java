package fling.compiler;

import fling.compiler.api.APICompiler;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.grammar.sententials.Variable;

public interface Namer {
  // TODO add context to variable creation.
  Variable createChild(Variable v);
  String headVariableClassName(Variable variable);
  String headVariableConclusionTypeName();
  String getASTClassName(Variable v);
  void name(ASTCompilationUnitNode compilationUnit);
  void name(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI);
}
