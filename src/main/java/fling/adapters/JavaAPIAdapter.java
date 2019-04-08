package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.api.APICompiler.ParameterFragment;
import fling.compiler.api.PolymorphicLanguageAPIBaseAdapter;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.AbstractMethodNode.Chained;
import fling.compiler.api.nodes.InterfaceNode;
import fling.compiler.api.nodes.PolymorphicTypeNode;
import fling.grammar.sententials.Constants;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Verb;
import fling.grammar.sententials.Word;

@SuppressWarnings("static-method") public class JavaAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String packageName;
  private final String className;
  private final String terminationMethodName;
  private final Namer namer;

  public JavaAPIAdapter(String packageName, String className, String terminationMethodName, Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.packageName = packageName;
    this.className = className;
    this.namer = namer;
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
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
  @Override public String printIntermediateType(APICompiler.TypeName name) {
    return printTypeName(name);
  }
  @Override public String printIntermediateType(APICompiler.TypeName name,
      List<PolymorphicTypeNode<APICompiler.TypeName>> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }
  @Override public String printStartMethod(APICompiler.MethodDeclaration declaration,
      PolymorphicTypeNode<APICompiler.TypeName> returnType) {
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
  @Override public String printIntermediateMethod(APICompiler.MethodDeclaration declaration,
      PolymorphicTypeNode<APICompiler.TypeName> returnType) {
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
  @Override public String printInterface(APICompiler.InterfaceDeclaration declaration,
      List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("interface %s%s{%s}", //
        printInterfaceDeclaration(declaration), //
        !declaration.isAccepting ? "" : " extends " + printTopType(), //
        methods.stream() //
            .filter(method -> !method.isTerminationMethod()) //
            .map(this::printMethod) //
            .collect(joining()));
  }
  public String printTypeName(APICompiler.TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }
  public String printTypeName(APICompiler.InterfaceDeclaration declaration) {
    return printTypeName(declaration.q, declaration.α, declaration.legalJumps);
  }
  public String printTypeName(Named q, Word<Named> α, Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
  public String printInterfaceDeclaration(APICompiler.InterfaceDeclaration declaration) {
    return String.format("%s<%s>", printTypeName(declaration), //
        declaration.typeVariables.stream().map(Named::name).collect(Collectors.joining(",")));
  }
  public String printTypeName(
      InterfaceNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? "$" : interfaze.isBot() ? "ø" : printTypeName(interfaze.declaration);
  }
  public String printConcreteImplementation(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
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
  @SuppressWarnings("unused") protected String printStartMethodBody(Verb σ, List<ParameterFragment> parameters) {
    return "return new α();";
  }
  protected String printConcreteImplementationClassBody() {
    return "";
  }
  @SuppressWarnings("unused") protected String printConcreteImplementationMethodBody(Verb σ, List<ParameterFragment> parameters) {
    return "";
  }
  protected String printTerminationMethodReturnType() {
    return "void";
  }
  protected String printTerminationMethodConcreteBody() {
    return "";
  }
  protected String printAdditionalDeclarations() {
    return "";
  }
}
