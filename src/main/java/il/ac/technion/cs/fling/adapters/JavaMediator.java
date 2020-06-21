package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.compilers.ast.ASTCompiler;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.grammars.LL1JavaASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.Invocation;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.compiler.ast.ASTParserCompiler;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;
import il.ac.technion.cs.fling.namers.NaiveLinker;

/** Java adapters mediator. Connects fluent API, AST types and AST run-time
 * compiler generation given LL(1) grammars. AST visitor class definition is
 * also produced.
 *
 * @author Ori Roth */
public class JavaMediator {
  /** API Java file contents. */
  public final String apiClass;
  /** AST Java file contents. Includes the AST visitor class. */
  public final String astClass;
  /** AST run-time compiler Java file contents. */
  public final String astCompilerClass;
  private final Linker namer;
  private final ASTParserCompiler parserCompiler;
  private final Class<? extends Terminal> Σ;
  final String apiName;
  final LL1 ll1;
  final String packageName;

  public <Σ extends Enum<Σ> & Terminal> JavaMediator(final EBNF bnf, final String packageName, final String apiName,
      final Class<Σ> Σ) {
    namer = new NaiveLinker(packageName, apiName);
    ll1 = new LL1(FancyEBNF.from(bnf), namer);
    this.packageName = packageName;
    this.apiName = apiName;
    final APIGenerator apiAdapter = new JavaGenerator(namer, packageName, apiName) {
      @Override public String printConcreteImplementationClassBody() {
        return outer().printConcreteImplementationClassBody();
      }

      @Override public String printConcreteImplementationMethodBody(final Token σ,
          final List<MethodParameter> parameters) {
        return outer().printConcreteImplementationMethodBody(σ, parameters);
      }

      @Override public String printTerminationMethodConcreteBody() {
        return outer().printTerminationMethodConcreteBody(ll1.normalizedBNF.ε);
      }

      private JavaMediator outer() {
        return JavaMediator.this;
      }

      @Override public String printTerminationMethodReturnType() {
        return outer().printTerminationMethodReturnType(ll1.normalizedBNF.ε);
      }

      @Override protected String printAdditionalDeclarations() {
        return outer().printAdditionalDeclarations();
      }

      @Override protected String printStartMethodBody(final Token σ, final List<MethodParameter> parameters) {
        return outer().printStartMethodBody(σ, parameters);
      }
    };
    final JavaASTVisitorAdapter astVisitorAdapter = new JavaASTVisitorAdapter(packageName, apiName + "AST", namer);
    final JavaInterfacesASTAdapter astAdapter = new JavaInterfacesASTAdapter(packageName, apiName + "AST", namer) {
      @Override protected String printAdditionalDeclarations(final ASTCompilationUnitNode compilationUnit) {
        return astVisitorAdapter.printASTVisitorClass(compilationUnit);
      }
    };
    this.Σ = Σ;
    parserCompiler = new LL1JavaASTParserCompiler<>(ll1.normalizedBNF, Σ, namer, packageName, apiName + "Compiler",
        apiName + "AST");
    astClass = astAdapter.printASTClass(new ASTCompiler(ll1.normalizedEBNF).compileAST());
    apiClass = apiAdapter.go(new ReliableAPICompiler(ll1.buildAutomaton(ll1.bnf.reduce())).makeModel());
    astCompilerClass = parserCompiler.printParserClass();
  }

  private List<String> processParameters(final Token σ, final List<MethodParameter> parameters) {
    Arrays.stream(new Object[] {}).map(Object::toString).toArray(String[]::new);
    final List<String> processedParameters = new ArrayList<>();
    for (int i = 0; i < parameters.size(); ++i) {
      final Parameter parameter = σ.parameters[i];
      final MethodParameter declaration = parameters.get(i);
      if (parameter.isVariableTypeParameter())
        processedParameters.add(String.format("((%s.%s.%s.α)%s).$()", //
            packageName, //
            apiName, //
            namer.headVariableClassName(parameter.asVariableTypeParameter().variable), //
            declaration.name));
      else if (parameter.isVarargsTypeParameter()) {
        final String αClass = String.format("%s.%s.%s.α", //
            packageName, //
            apiName, //
            namer.headVariableClassName(parameter.asVarargsVariableTypeParameter().variable));
        processedParameters.add(String.format("%s.stream(%s).map(%s.class::cast).map(%s::$).toArray(%s.%s.%s[]::new)", //
            Arrays.class.getCanonicalName(), //
            declaration.name, //
            αClass, //
            αClass, //
            packageName, //
            apiName + "AST", //
            namer.getASTClassName(parameter.asVarargsVariableTypeParameter().variable)));
      } else if (parameter.isStringTypeParameter())
        processedParameters.add(declaration.name);
      else
        throw new RuntimeException("problem while processing API parameters");
    }
    return processedParameters;
  }

  protected String printStartMethodBody(final Token σ, final List<MethodParameter> parameters) {
    final List<String> processedParameters = processParameters(σ, parameters);
    return String.format("α α=new α();%sreturn α;", //
        Constants.$$.equals(σ) ? "" //
            : String.format("α.w.add(new %s(%s.%s%s%s));", //
                Invocation.class.getCanonicalName(), //
                Σ.getCanonicalName(), //
                σ.name(), //
                processedParameters.isEmpty() ? "" : ",", //
                String.join(",", processedParameters)));
  }

  String printAdditionalDeclarations() {
    return ll1.ebnf.headVariables.stream() //
        .map(ll1::getSubBNF) //
        .map(bnf -> new JavaGenerator(namer, null, namer.headVariableClassName(bnf.ε)) {
          @Override public String printConcreteImplementationClassBody() {
            return JavaMediator.this.printConcreteImplementationClassBody();
          }

          @Override public String printConcreteImplementationMethodBody(final Token σ,
              final List<MethodParameter> parameters) {
            return JavaMediator.this.printConcreteImplementationMethodBody(σ, parameters);
          }

          @Override public String printTerminationMethodConcreteBody() {
            return JavaMediator.this.printTerminationMethodConcreteBody(bnf.ε);
          }

          @Override public String printTerminationMethodReturnType() {
            return JavaMediator.this.printTerminationMethodReturnType(bnf.ε);
          }

          @Override public String printTopInterfaceBody() {
            return "";
          }

          @Override protected String printStartMethodBody(final Token σ, final List<MethodParameter> parameters) {
            return JavaMediator.this.printStartMethodBody(σ, parameters);
          }
        } //
            .go(new ReliableAPICompiler(ll1.buildAutomaton(bnf)).makeModel())) //
        .collect(joining());
  }

  String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        List.class.getCanonicalName(), //
        Invocation.class.getCanonicalName(), //
        LinkedList.class.getCanonicalName());
  }

  String printConcreteImplementationMethodBody(final Token σ, final List<MethodParameter> parameters) {
    assert σ.parameters.length == parameters.size();
    final List<String> processedParameters = processParameters(σ, parameters);
    return String.format("this.w.add(new %s(%s.%s,%s));", //
        Invocation.class.getCanonicalName(), //
        Σ.getCanonicalName(), //
        σ.name(), //
        String.format("new Object[]{%s}", String.join(",", processedParameters)));
  }

  String printTerminationMethodConcreteBody(final Variable head) {
    return String.format("return %s(w);", //
        parserCompiler.getParsingMethodName(head));
  }

  String printTerminationMethodReturnType(final Variable head) {
    return String.format("%s.%s.%s", //
        packageName, //
        apiName + "AST", //
        namer.getASTClassName(head));
  }
}
