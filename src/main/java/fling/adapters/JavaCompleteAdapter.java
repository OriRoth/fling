package fling.adapters;

import java.util.LinkedList;
import java.util.List;

import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.api.PolymorphicLanguageAPIAdapter;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.ast.ASTParserCompiler;
import fling.compiler.ast.PolymorphicLanguageASTAdapter;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Verb;

public class JavaCompleteAdapter implements PolymorphicLanguageAPIAdapter, PolymorphicLanguageASTAdapter {
  private boolean afterASTProcessing = false;
  private final JavaAPIAdapter apiAdapter;
  private final JavaInterfacesASTAdapter astAdapter;
  private final Class<? extends Terminal> Σ;
  private final ASTParserCompiler parserCompiler;

  public JavaCompleteAdapter(String packageName, String className, String apiStartMethodName, String apiTerminationMethodName,
      Namer namer, Class<? extends Terminal> Σ, ASTParserCompiler parserCompiler) {
    this.apiAdapter = new JavaAPIAdapter(packageName, className, apiStartMethodName, apiTerminationMethodName, namer) {
      @Override public String printConcreteImplementationClassBody() {
        return JavaCompleteAdapter.this.printConcreteImplementationClassBody();
      }
      @Override public String printConcreteImplementationMethodBody(Verb σ) {
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
    this.parserCompiler = parserCompiler;
  }
  @Override public String printASTClass(ASTCompilationUnitNode compilationUnit) {
    String output = astAdapter.printASTClass(compilationUnit);
    //
    afterASTProcessing = true;
    return output;
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    if (!afterASTProcessing)
      throw new IllegalStateException("cannot produce API types before creating AST classes");
    return apiAdapter.printFluentAPI(fluentAPI);
  }
  @Override public String printASTParser() {
    return parserCompiler.printParserClass();
  }
  public String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        List.class.getCanonicalName(), //
        Σ.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }
  public String printConcreteImplementationMethodBody(Verb σ) {
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
