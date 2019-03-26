package fling.adapters;

import java.util.LinkedList;
import java.util.List;

import fling.compiler.Namer;
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
  private final JavaAPIAdapter<Q, Σ, Γ> apiAdapter;
  private final JavaInterfacesASTAdapter astAdapter;
  private final Class<? extends Σ> Σ;

  public JavaCompleteAdapter(String packageName, String className, String apiStartMethodName, String apiTerminationMethodName,
      Namer namer, Class<? extends Σ> Σ) {
    this.apiAdapter = new JavaAPIAdapter<Q, Σ, Γ>(packageName, className, apiStartMethodName, apiTerminationMethodName, namer) {
      @Override public String printConcreteImplementationClassBody() {
        return JavaCompleteAdapter.this.printConcreteImplementationClassBody();
      }
      @Override public String printConcreteImplementationMethodBody(Σ σ) {
        return JavaCompleteAdapter.this.printConcreteImplementationMethodBody(σ);
      }
      @Override public String printTerminationMethodReturnType() {
        return JavaCompleteAdapter.this.printTerminationMethodReturnType();
      }
      @Override public String printTerminationMethodConcreteBody() {
        return JavaCompleteAdapter.this.printTerminationMethodConcreteBody();
      }
    };
    this.astAdapter = new JavaInterfacesASTAdapter(packageName, className + "AST", namer);
    this.Σ = Σ;
  }
  @Override public String printASTClass(ASTCompilationUnitNode compilationUnit) {
    String output = astAdapter.printASTClass(compilationUnit);
    //
    afterASTProcessing = true;
    return output;
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    if (!afterASTProcessing)
      throw new IllegalStateException("cannot produce API types before creating AST classes");
    return apiAdapter.printFluentAPI(fluentAPI);
  }
  public String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        List.class.getCanonicalName(), //
        Σ.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }
  public String printConcreteImplementationMethodBody(Σ σ) {
    return String.format("this.w.add(%s.%s);", //
        Σ.getCanonicalName(), //
        σ.name());
  }
  public String printTerminationMethodReturnType() {
    return "void";
  }
  public String printTerminationMethodConcreteBody() {
    return "";
  }
}
