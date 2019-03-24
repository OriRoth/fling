package fling.adapters;

import fling.compiler.api.APICompiler;
import fling.compiler.api.PolymorphicLanguageAPIAdapter;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.ast.PolymorphicLanguageASTAdapter;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;

public class JavaCompleteAdapter<Q extends Named, Σ extends Terminal, Γ extends Named>
    implements PolymorphicLanguageAPIAdapter<Q, Σ, Γ>, PolymorphicLanguageASTAdapter {
  private boolean afterASTProcessing = false;

  @Override public String printASTClass(ASTCompilationUnitNode compilationUnit) {
    // TODO Auto-generated method stub
    afterASTProcessing = true;
    return null;
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    if (!afterASTProcessing)
      throw new IllegalStateException("cannot produce API types before creating AST classes");
    // TODO Auto-generated method stub
    return null;
  }
}
