package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.LinkedList;
import java.util.List;

import fling.compiler.Assignment;
import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.api.APICompiler.ParameterFragment;
import fling.compiler.ast.ASTCompiler;
import fling.compiler.ast.ASTParserCompiler;
import fling.grammar.BNF;
import fling.grammar.LL1;
import fling.grammar.LL1JavaASTParserCompiler;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.namers.NaiveNamer;

public class JavaMediator {
  final LL1 ll1;
  private final Namer namer;
  final String packageName;
  final String apiName;
  private final JavaAPIAdapter apiAdapter;
  private final JavaInterfacesASTAdapter astAdapter;
  private final Class<? extends Terminal> Σ;
  private final ASTParserCompiler parserCompiler;
  public final String apiClass;
  public final String astClass;
  public final String astCompilerClass;

  public <Σ extends Enum<Σ> & Terminal> JavaMediator(BNF bnf, String packageName, String apiName, Class<Σ> Σ) {
    this.namer = new NaiveNamer(packageName, apiName);
    this.ll1 = new LL1(bnf, namer);
    this.packageName = packageName;
    this.apiName = apiName;
    this.apiAdapter = new JavaAPIAdapter(packageName, apiName, "__", "$", namer) {
      @Override public String printConcreteImplementationClassBody() {
        return JavaMediator.this.printConcreteImplementationClassBody();
      }
      @Override public String printConcreteImplementationMethodBody(Verb σ, List<ParameterFragment> parameters) {
        return JavaMediator.this.printConcreteImplementationMethodBody(σ, parameters);
      }
      @Override public String printTerminationMethodReturnType() {
        return JavaMediator.this.printTerminationMethodReturnType(ll1.normalizedBNF.startVariable);
      }
      @Override public String printTerminationMethodConcreteBody() {
        return JavaMediator.this.printTerminationMethodConcreteBody(ll1.normalizedBNF.startVariable);
      }
      @Override protected String printAdditionalDeclarations() {
        return JavaMediator.this.printAdditionalDeclarations();
      }
    };
    this.astAdapter = new JavaInterfacesASTAdapter(packageName, apiName + "AST", namer);
    this.Σ = Σ;
    this.parserCompiler = new LL1JavaASTParserCompiler<>(ll1.normalizedBNF, Σ, namer, packageName, apiName + "Compiler",
        apiName + "AST");
    this.astClass = astAdapter.printASTClass(new ASTCompiler(ll1.normalizedBNF).compileAST());
    this.apiClass = apiAdapter.printFluentAPI(new APICompiler(ll1.toDPDA()).compileFluentAPI());
    this.astCompilerClass = parserCompiler.printParserClass();
  }
  @SuppressWarnings("static-method") String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        List.class.getCanonicalName(), //
        Assignment.class.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }
  String printConcreteImplementationMethodBody(Verb σ, List<ParameterFragment> parameters) {
    return String.format("this.w.add(new %s(%s.%s%s%s));", //
        Assignment.class.getCanonicalName(), //
        Σ.getCanonicalName(), //
        σ.name(), //
        parameters.isEmpty() ? "" : ",", //
        parameters.stream().map(ParameterFragment::parameterName).collect(joining(",")));
  }
  String printTerminationMethodReturnType(Variable head) {
    return String.format("%s.%s.%s", //
        packageName, //
        apiName + "AST", //
        namer.getASTClassName(head));
  }
  String printTerminationMethodConcreteBody(Variable head) {
    return String.format("return %s(w);", //
        parserCompiler.getParsingMethodName(head));
  }
  String printAdditionalDeclarations() {
    return ll1.ebnf.headVariables.stream() //
        .map(ll1::getSubBNF) //
        .map(bnf -> new JavaAPIAdapter(null, namer.headVariableClassName(bnf.startVariable), "__", "$", namer) {
          @Override public String printTopInterfaceBody() {
            return "";
          }
          @Override public String printConcreteImplementationClassBody() {
            return JavaMediator.this.printConcreteImplementationClassBody();
          }
          @Override public String printConcreteImplementationMethodBody(Verb σ, List<ParameterFragment> parameters) {
            return JavaMediator.this.printConcreteImplementationMethodBody(σ, parameters);
          }
          @Override public String printTerminationMethodReturnType() {
            return JavaMediator.this.printTerminationMethodReturnType(bnf.startVariable);
          }
          @Override public String printTerminationMethodConcreteBody() {
            return JavaMediator.this.printTerminationMethodConcreteBody(bnf.startVariable);
          }
        } //
            .printFluentAPI(new APICompiler(ll1.buildAutomaton(bnf)).compileFluentAPI())) //
        .collect(joining());
  }
}
