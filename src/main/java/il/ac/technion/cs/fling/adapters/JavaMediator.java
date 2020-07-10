package il.ac.technion.cs.fling.adapters;
import java.util.*;
import static java.util.stream.Collectors.joining;
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
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
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
  final Grammar grammar;
  final String packageName;
  public <Σ extends Enum<Σ> & Terminal> JavaMediator(final EBNF bnf, final String packageName, final String apiName,
      final Class<Σ> Σ) {
    grammar = new Grammar(new FancyEBNF(bnf, null, null, null, false));
    namer = new NaiveLinker(packageName, apiName);
    this.packageName = packageName;
    this.apiName = apiName;
    final APIGenerator apiAdapter = new JavaGenerator(packageName, apiName) {
      @Override public String printConcreteImplementationClassBody() {
        return outer().printConcreteImplementationClassBody();
      }
      @Override public String printConcreteImplementationMethodBody(final Token σ,
          final List<MethodParameter> parameters) {
        return outer().printConcreteImplementationMethodBody(σ, parameters);
      }
      @Override public String printTerminationMethodConcreteBody() {
        return outer().printTerminationMethodConcreteBody(grammar.normalizedBNF.ε);
      }
      private JavaMediator outer() {
        return JavaMediator.this;
      }
      @Override public String printTerminationMethodReturnType() {
        return outer().printTerminationMethodReturnType(grammar.normalizedBNF.ε);
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
    parserCompiler = new LL1JavaASTParserCompiler<>(grammar.normalizedBNF, Σ, namer, packageName, apiName + "Compiler",
        apiName + "AST");
    astClass = astAdapter.printASTClass(new ASTCompiler(grammar.normalizedEBNF).compileAST());
    apiClass = apiAdapter.go(new ReliableAPICompiler(grammar.buildAutomaton(grammar.bnf.clean())).go());
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
            typeName(Arrays.class), //
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
                typeName(Invocation.class), //
                typeName(Σ), //
                σ.name(), //
                processedParameters.isEmpty() ? "" : ",", //
                String.join(",", processedParameters)));
  }
  String printAdditionalDeclarations() {
    return grammar.ebnf.headVariables.stream() //
        .map(grammar::getSubBNF) //
        .map(bnf -> new JavaGenerator(null, namer.headVariableClassName(bnf.ε)) {
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
          @Override protected String printStartMethodBody(final Token σ, final List<MethodParameter> parameters) {
            return JavaMediator.this.printStartMethodBody(σ, parameters);
          }
        } //
            .go(new ReliableAPICompiler(LL1.buildAutomaton(bnf)).go())) //
        .collect(joining());
  }
  String printConcreteImplementationClassBody() {
    return String.format("public %s<%s> w=new %s();", //
        typeName(List.class), //
        typeName(Invocation.class), //
        typeName(LinkedList.class));
  }
  SortedSet<String> classes = new TreeSet<>();
  private String typeName(Class<?> c) {
    classes.add(c.getCanonicalName());
    return c.getSimpleName();
  }
  String printConcreteImplementationMethodBody(final Token σ, final List<MethodParameter> parameters) {
    assert σ.parameters.length == parameters.size();
    return String.format("this.w.add(new %s(%s.%s,%s));", //
        typeName(Invocation.class), //
        typeName(Σ), //
        σ.name(), //
        String.format("new Object[]{%s}", String.join(",", processParameters(σ, parameters))));
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
