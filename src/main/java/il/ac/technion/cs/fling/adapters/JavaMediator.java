package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.compilers.ast.ASTCompiler;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.grammars.LL1JavaASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.Assignment;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.ParameterFragment;
import il.ac.technion.cs.fling.internal.compiler.ast.ASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
import il.ac.technion.cs.fling.namers.NaiveNamer;

/** Java adapters mediator. Connects fluent API, AST types and AST run-time
 * compiler generation given LL(1) grammars. AST visitor class definition is
 * also produced.
 *
 * @author Ori Roth */
public class JavaMediator {
  final LL1 ll1;
  private final Namer namer;
  final String packageName;
  final String apiName;
  private final JavaAPIAdapter apiAdapter;
  private final JavaInterfacesASTAdapter astAdapter;
  private final Class<? extends Terminal> Σ;
  private final ASTParserCompiler parserCompiler;
  /** API Java file contents. */
  public final String apiClass;
  /** AST Java file contents. Includes the AST visitor class. */
  public final String astClass;
  /** AST run-time compiler Java file contents. */
  public final String astCompilerClass;

  public <Σ extends Enum<Σ> & Terminal> JavaMediator(final EBNF bnf, final String packageName,
      final String apiName, final Class<Σ> Σ) {
    this.namer = new NaiveNamer(packageName, apiName);
    this.ll1 = new LL1(FancyEBNF.from(bnf), namer);
    this.packageName = packageName;
    this.apiName = apiName;
    this.apiAdapter = new JavaAPIAdapter(packageName, apiName, "$", namer) {
      @Override protected String printStartMethodBody(final Token σ, final List<ParameterFragment> parameters) {
        return JavaMediator.this.printStartMethodBody(σ, parameters);
      }

      @Override public String printConcreteImplementationClassBody() {
        return JavaMediator.this.printConcreteImplementationClassBody();
      }

      @Override public String printConcreteImplementationMethodBody(final Token σ,
          final List<ParameterFragment> parameters) {
        return JavaMediator.this.printConcreteImplementationMethodBody(σ, parameters);
      }

      @Override public String printTerminationMethodReturnType() {
        return JavaMediator.this.printTerminationMethodReturnType(ll1.normalizedBNF.ε);
      }

      @Override public String printTerminationMethodConcreteBody() {
        return JavaMediator.this.printTerminationMethodConcreteBody(ll1.normalizedBNF.ε);
      }

      @Override protected String printAdditionalDeclarations() {
        return JavaMediator.this.printAdditionalDeclarations();
      }
    };
    final JavaASTVisitorAdapter astVisitorAdapter = new JavaASTVisitorAdapter(packageName, apiName + "AST", namer);
    this.astAdapter = new JavaInterfacesASTAdapter(packageName, apiName + "AST", namer) {
      @Override protected String printAdditionalDeclarations(final ASTCompilationUnitNode compilationUnit) {
        return astVisitorAdapter.printASTVisitorClass(compilationUnit);
      }
    };
    this.Σ = Σ;
    this.parserCompiler = new LL1JavaASTParserCompiler<>(ll1.normalizedBNF, Σ, namer, packageName, apiName + "Compiler",
        apiName + "AST");
    this.astClass = astAdapter.printASTClass(new ASTCompiler(ll1.normalizedEBNF).compileAST());
    this.apiClass = apiAdapter
        .printFluentAPI(new ReliableAPICompiler(ll1.buildAutomaton(ll1.bnf.reduce())).compileFluentAPI());
    this.astCompilerClass = parserCompiler.printParserClass();
  }

  protected String printStartMethodBody(final Token σ, final List<ParameterFragment> parameters) {
    final List<String> processedParameters = processParameters(σ, parameters);
    return String.format("α α=new α();%sreturn α;", //
        Constants.$$.equals(σ) ? "" //
            : String.format("α.w.add(new %s(%s.%s%s%s));", //
                Assignment.class.getCanonicalName(), //
                Σ.getCanonicalName(), //
                σ.name(), //
                processedParameters.isEmpty() ? "" : ",", //
                String.join(",", processedParameters)));
  }

  @SuppressWarnings("static-method") String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        List.class.getCanonicalName(), //
        Assignment.class.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }

  String printConcreteImplementationMethodBody(final Token σ, final List<ParameterFragment> parameters) {
    assert σ.parameters.length == parameters.size();
    final List<String> processedParameters = processParameters(σ, parameters);
    return String.format("this.w.add(new %s(%s.%s,%s));", //
        Assignment.class.getCanonicalName(), //
        Σ.getCanonicalName(), //
        σ.name(), //
        String.format("new Object[]{%s}", String.join(",", processedParameters)));
  }

  String printTerminationMethodReturnType(final Variable head) {
    return String.format("%s.%s.%s", //
        packageName, //
        apiName + "AST", //
        namer.getASTClassName(head));
  }

  String printTerminationMethodConcreteBody(final Variable head) {
    return String.format("return %s(w);", //
        parserCompiler.getParsingMethodName(head));
  }

  String printAdditionalDeclarations() {
    return ll1.ebnf.headVariables.stream() //
        .map(ll1::getSubBNF) //
        .map(bnf -> new JavaAPIAdapter(null, namer.headVariableClassName(bnf.ε), "$", namer) {
          @Override protected String printStartMethodBody(final Token σ, final List<ParameterFragment> parameters) {
            return JavaMediator.this.printStartMethodBody(σ, parameters);
          }

          @Override public String printTopInterfaceBody() {
            return "";
          }

          @Override public String printConcreteImplementationClassBody() {
            return JavaMediator.this.printConcreteImplementationClassBody();
          }

          @Override public String printConcreteImplementationMethodBody(final Token σ,
              final List<ParameterFragment> parameters) {
            return JavaMediator.this.printConcreteImplementationMethodBody(σ, parameters);
          }

          @Override public String printTerminationMethodReturnType() {
            return JavaMediator.this.printTerminationMethodReturnType(bnf.ε);
          }

          @Override public String printTerminationMethodConcreteBody() {
            return JavaMediator.this.printTerminationMethodConcreteBody(bnf.ε);
          }
        } //
            .printFluentAPI(new ReliableAPICompiler(ll1.buildAutomaton(bnf)).compileFluentAPI())) //
        .collect(joining());
  }

  private List<String> processParameters(final Token σ, final List<ParameterFragment> parameters) {
    Arrays.stream(new Object[] {}).map(Object::toString).toArray(String[]::new);
    final List<String> processedParameters = new ArrayList<>();
    for (int i = 0; i < parameters.size(); ++i) {
      final Parameter parameter = σ.parameters[i];
      final ParameterFragment declaration = parameters.get(i);
      if (parameter.isVariableTypeParameter())
        processedParameters.add(String.format("((%s.%s.%s.α)%s).$()", //
            packageName, //
            apiName, //
            namer.headVariableClassName(parameter.asVariableTypeParameter().variable), //
            declaration.parameterName));
      else if (parameter.isVarargsTypeParameter()) {
        final String αClass = String.format("%s.%s.%s.α", //
            packageName, //
            apiName, //
            namer.headVariableClassName(parameter.asVarargsVariableTypeParameter().variable));
        processedParameters.add(String.format("%s.stream(%s).map(%s.class::cast).map(%s::$).toArray(%s.%s.%s[]::new)", //
            Arrays.class.getCanonicalName(), //
            declaration.parameterName, //
            αClass, //
            αClass, //
            packageName, //
            apiName + "AST", //
            namer.getASTClassName(parameter.asVarargsVariableTypeParameter().variable)));
      } else if (parameter.isStringTypeParameter())
        processedParameters.add(declaration.parameterName);
      else
        throw new RuntimeException("problem while processing API parameters");
    }
    return processedParameters;
  }
}
