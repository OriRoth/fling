package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Collectors;

import fling.compiler.Namer;
import fling.compiler.api.APICompiler;
import fling.compiler.api.PolymorphicLanguageAPIAdapter;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.PolymorphicTypeNode;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Word;

public class CppAPIAdapter<Q extends Named, Σ extends Terminal, Γ extends Named> implements PolymorphicLanguageAPIAdapter<Q, Σ, Γ> {
  private final String startMethodName;
  private final String terminationMethodName;
  private final Namer namer;

  public CppAPIAdapter(String startMethodName, String terminationMethodName, Namer namer) {
    this.startMethodName = startMethodName;
    this.terminationMethodName = terminationMethodName;
    this.namer = namer;
  }
  @Override public String printTopType() {
    return "TOP";
  }
  @Override public String printBotType() {
    return "BOT";
  }
  @Override public String printIntermediateType(APICompiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name);
  }
  @Override public String printIntermediateType(APICompiler<Q, Σ, Γ>.TypeName name,
      List<PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName>> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }
  @Override public String printStartMethod(PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("%s* %s() {return nullptr;}", printType(returnType), startMethodName);
  }
  @Override public String printTerminationMethod() {
    return String.format("virtual void %s() const;", terminationMethodName);
  }
  @Override public String printIntermediateMethod(APICompiler<Q, Σ, Γ>.MethodDeclaration declaration,
      PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("virtual %s %s(%s) const;", printType(returnType), declaration.name.name(),
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")));
  }
  @Override public String printTopInterface() {
    return String.format("class TOP{public:virtual void %s() const;};", terminationMethodName);
  }
  @Override public String printBotInterface() {
    return "class BOT{};";
  }
  @Override public String printInterface(APICompiler<Q, Σ, Γ>.InterfaceDeclaration declaration,
      List<AbstractMethodNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration>> methods) {
    return String.format("%s{public:%s};", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining()));
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration, APICompiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s%s%s", //
        fluentAPI.interfaces.stream().filter(i -> !i.isTop() && !i.isBot()).map(i -> printInterfaceDeclaration(i.declaration) + ";")
            .collect(joining()), //
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(joining()), //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining()));
  }
  public String printTypeName(APICompiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name.q, name.α);
  }
  public String printTypeName(Q q, Word<Γ> α) {
    return α == null ? q.name() : String.format("%s_%s", q.name(), α.stream().map(Named::name).collect(Collectors.joining()));
  }
  public String printInterfaceDeclaration(APICompiler<Q, Σ, Γ>.InterfaceDeclaration declaration) {
    return declaration.typeVariables.isEmpty() ? String.format("class %s", printTypeName(declaration.q, declaration.α))
        : String.format("template<%s>class %s",
            declaration.typeVariables.stream().map(q -> "class " + q.name()).collect(Collectors.joining(",")), //
            printTypeName(declaration.q, declaration.α));
  }
}
