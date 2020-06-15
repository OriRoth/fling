package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ParameterFragment;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method.Chained;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth */
public class JavaGenerator extends APIGenerator {
  private final String packageName;
  private final String className;

  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
  }

  public JavaGenerator(final String packageName, final String className, final String endName, final Namer namer) {
    super(namer, endName, "ø", "$");
    this.packageName = packageName;
    this.className = className;
  }

  @Override public String render(final Model fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n%s@SuppressWarnings(\"all\")public interface %s{%s%s%s%s}", //
        startComment(), //
        packageName == null ? "" : String.format("package %s;\nimport java.util.*;\n\n\n", packageName), //
        className, //
        fluentAPI.startMethods().map(this::render).collect(joining()), //
        fluentAPI.types().map(this::render).collect(joining()), //
        printConcreteImplementation(fluentAPI), //
        printAdditionalDeclarations());
  }

  @Override public String render(final TypeName name, final List<SkeletonType> typeArguments) {
    return String.format("%s<%s>", //
        render(name), //
        typeArguments.stream().map(this::render).collect(joining(",")));
  }

  @Override public String renderMethod(final MethodDeclaration declaration, final SkeletonType returnType) {
    return String.format("public static %s %s(%s) {%s}", //
        render(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        declaration.parmeters() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        printStartMethodBody(declaration.name, declaration.getInferredParameters()));
  }

  @Override public String renderTerminationMethod() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName);
  }

  @Override public String render(final MethodDeclaration declaration, final SkeletonType returnType) {
    return String.format("%s %s(%s);", //
        render(returnType), //
        declaration.name.name(), //
        declaration.parmeters() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")));
  }

  @Override public String renderInterfaceTop() {
    return String.format("interface ${%s}", printTopInterfaceBody());
  }

  public String printTopInterfaceBody() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName);
  }

  @Override public String renderInterfaceBottom() {
    return "interface ø {}";
  }

  @Override public String render(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("interface %s%s{%s}", //
        render(declaration), //
        !declaration.isAccepting ? "" : " extends " + topName, //
        methods.stream() //
            .filter(method -> !method.isTerminationMethod()) //
            .map(this::render) //
            .collect(joining()));
  }

  @Override public String render(final TypeName name) {
    return render(name.q, name.α, name.legalJumps);
  }

  public String printTypeName(final InterfaceDeclaration declaration) {
    return render(declaration.q, declaration.α, declaration.legalJumps);
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @Override public String render(final InterfaceDeclaration declaration) {
    return String.format("%s<%s>", printTypeName(declaration), //
        declaration.parameters().map(Named::name).collect(Collectors.joining(",")));
  }

  public String printTypeName(final Type interfaze) {
    return interfaze.isTop() ? "$" : interfaze.isBot() ? "ø" : printTypeName(interfaze.declaration);
  }

  public String printConcreteImplementation(final Model fluentAPI) {
    return String.format("static class α implements %s{%s%s%s}", //
        fluentAPI.types().map(this::printTypeName).collect(joining(",")), //
        printConcreteImplementationClassBody(), fluentAPI.body.methods.stream() //
            .map(Method::asChainedMethod) //
            .map(Chained::declaration) //
            .map(declaration -> String.format("public α %s(%s){%sreturn this;}", //
                declaration.name.name(), //
                declaration.parmeters() //
                    .map(parameter -> String.format("%s %s", //
                        parameter.parameterType, //
                        parameter.parameterName)) //
                    .collect(joining(",")), //
                printConcreteImplementationMethodBody(declaration.name, declaration.getInferredParameters()))) //
            .collect(joining()),
        String.format("public %s %s(){%s}", //
            printTerminationMethodReturnType(), //
            endName, //
            printTerminationMethodConcreteBody()));
  }

  /** Start static method body.
   *
   * @param σ          inducing token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printStartMethodBody(final Token σ,
      final List<ParameterFragment> parameters) {
    return "return new α();";
  }

  /** Prints additional definition in concrete implementation class's body.
   *
   * @return additional definition */
  protected String printConcreteImplementationClassBody() {
    return "";
  }

  /** Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ          current token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printConcreteImplementationMethodBody(final Token σ,
      final List<ParameterFragment> parameters) {
    return "";
  }

  /** Return type of the termination method.
   *
   * @return return type */
  protected String printTerminationMethodReturnType() {
    return "void";
  }

  /** Concrete implementation's termination method body. Might be used to create
   * and return the processed terminal.
   *
   * @return method body */
  protected String printTerminationMethodConcreteBody() {
    return "";
  }

  /** Additional declaration within the top class.
   *
   * @return additional declarations */
  protected String printAdditionalDeclarations() {
    return "";
  }
}
