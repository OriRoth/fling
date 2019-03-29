package fling.compiler.api;

import fling.compiler.api.nodes.APICompilationUnitNode;

public interface PolymorphicLanguageAPIAdapter {
  String printFluentAPI(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI);
}
