package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.*;
import java.util.stream.Collectors;

import fling.internal.compiler.Namer;
import fling.internal.compiler.api.*;
import fling.internal.compiler.api.APICompiler.ParameterFragment;
import fling.internal.compiler.api.nodes.*;
import fling.internal.compiler.api.nodes.AbstractMethodNode.Chained;
import fling.internal.grammar.sententials.*;

/**
 * Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth
 */
@SuppressWarnings("static-method") public class JavaAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String packageName;
  private final String className;
  private final String terminationMethodName;
  private final Namer namer;

  public JavaAPIAdapter(final String packageName, final String className, final String terminationMethodName, final Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.packageName = packageName;
    this.className = className;
    this.namer = namer;
  }
  @Override public String printFluentAPI(
      final APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s@SuppressWarnings(\"all\")public interface %s{%s%s%s%s}", //
        packageName == null ? "" : String.format("package %s;", packageName), //
        className, //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining()), //
        fluentAPI.interfaces.stream().map(this::printInterface).collect(joining()), //
        printConcreteImplementation(fluentAPI), //
        printAdditionalDeclarations());
  }
  @Override public String printTopType() {
    return "$";
  }
  @Override public String printBotType() {
    return "ø";
  }
  @Override public String printIntermediateType(final APICompiler.TypeName name) {
    return printTypeName(name);
  }
  @Override public String printIntermediateType(final APICompiler.TypeName name,
      final List<PolymorphicTypeNode<APICompiler.TypeName>> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }
  @Override public String printStartMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    return String.format("public static %s %s(%s) {%s}", //
        printType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        printStartMethodBody(declaration.name, declaration.getInferredParameters()));
  }
  @Override public String printTerminationMethod() {
    return String.format("%s %s();", //
        printTerminationMethodReturnType(), //
        terminationMethodName);
  }
  @Override public String printIntermediateMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    return String.format("%s %s(%s);", //
        printType(returnType), //
        declaration.name.name(), //
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")));
  }
  @Override public String printTopInterface() {
    return String.format("interface ${%s}", printTopInterfaceBody());
  }
  public String printTopInterfaceBody() {
    return String.format("%s %s();", //
        printTerminationMethodReturnType(), //
        terminationMethodName);
  }
  @Override public String printBotInterface() {
    return "interface ø {}";
  }
  @Override public String printInterface(final APICompiler.InterfaceDeclaration declaration,
      final List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("interface %s%s{%s}", //
        printInterfaceDeclaration(declaration), //
        !declaration.isAccepting ? "" : " extends " + printTopType(), //
        methods.stream() //
            .filter(method -> !method.isTerminationMethod()) //
            .map(this::printMethod) //
            .collect(joining()));
  }
  public String printTypeName(final APICompiler.TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }
  public String printTypeName(final APICompiler.InterfaceDeclaration declaration) {
    return printTypeName(declaration.q, declaration.α, declaration.legalJumps);
  }
  public String printTypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
  public String printInterfaceDeclaration(final APICompiler.InterfaceDeclaration declaration) {
    return String.format("%s<%s>", printTypeName(declaration), //
        declaration.typeVariables.stream().map(Named::name).collect(Collectors.joining(",")));
  }
  public String printTypeName(
      final InterfaceNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? "$" : interfaze.isBot() ? "ø" : printTypeName(interfaze.declaration);
  }
  public String printConcreteImplementation(
      final APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    return String.format("static class α implements %s{%s%s%s}", //
        fluentAPI.interfaces.stream().map(this::printTypeName).collect(joining(",")), //
        printConcreteImplementationClassBody(), fluentAPI.concreteImplementation.methods.stream() //
            .map(AbstractMethodNode::asChainedMethod) //
            .map(Chained::declaration) //
            .map(declaration -> String.format("public α %s(%s){%sreturn this;}", //
                declaration.name.name(), //
                declaration.getInferredParameters().stream() //
                    .map(parameter -> String.format("%s %s", //
                        parameter.parameterType, //
                        parameter.parameterName)) //
                    .collect(joining(",")), //
                printConcreteImplementationMethodBody(declaration.name, declaration.getInferredParameters()))) //
            .collect(joining()),
        String.format("public %s %s(){%s}", //
            printTerminationMethodReturnType(), //
            terminationMethodName, //
            printTerminationMethodConcreteBody()));
  }
  /**
   * Start static method body.
   *
   * @param σ inducing verb
   * @param parameters method parameters
   * @return method body
   */
  @SuppressWarnings("unused") protected String printStartMethodBody(final Verb σ, final List<ParameterFragment> parameters) {
    return "return new α();";
  }
  /**
   * Prints additional definition in concrete implementation class's body.
   *
   * @return additional definition
   */
  protected String printConcreteImplementationClassBody() {
    return "";
  }
  /**
   * Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ current verb
   * @param parameters method parameters
   * @return method body
   */
  @SuppressWarnings("unused") protected String printConcreteImplementationMethodBody(final Verb σ,
      final List<ParameterFragment> parameters) {
    return "";
  }
  /**
   * Return type of the termination method.
   *
   * @return return type
   */
  protected String printTerminationMethodReturnType() {
    return "void";
  }
  /**
   * Concrete implementation's termination method body. Might be used to create
   * and return the processed terminal.
   *
   * @return method body
   */
  protected String printTerminationMethodConcreteBody() {
    return "";
  }
  /**
   * Additional declaration within the top class.
   *
   * @return additional declarations
   */
  protected String printAdditionalDeclarations() {
    return "";
  }
}
