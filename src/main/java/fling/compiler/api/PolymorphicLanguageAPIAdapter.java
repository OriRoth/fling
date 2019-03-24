package fling.compiler.api;

import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;

public interface PolymorphicLanguageAPIAdapter<Q extends Named, Σ extends Terminal, Γ extends Named> {
  String printFluentAPI(
      APICompilationUnitNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI);
}
