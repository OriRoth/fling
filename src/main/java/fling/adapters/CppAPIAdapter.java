package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fling.internal.compiler.Namer;
import fling.internal.compiler.api.APICompiler;
import fling.internal.compiler.api.PolymorphicLanguageAPIBaseAdapter;
import fling.internal.compiler.api.nodes.APICompilationUnitNode;
import fling.internal.compiler.api.nodes.AbstractMethodNode;
import fling.internal.compiler.api.nodes.PolymorphicTypeNode;
import fling.internal.grammar.sententials.Constants;
import fling.internal.grammar.sententials.Named;
import fling.internal.grammar.sententials.Word;

/**
 * Prototypical C++ API adapter.
 * 
 * @author Ori Roth
 */
public class CppAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String terminationMethodName;
  private final Namer namer;

  public CppAPIAdapter(String terminationMethodName, Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.namer = namer;
  }
  @Override public String printFluentAPI(
      APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s%s%s", //
        fluentAPI.interfaces.stream().filter(i -> !i.isTop() && !i.isBot()).map(i -> printInterfaceDeclaration(i.declaration) + ";")
            .collect(joining()), //
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(joining()), //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining()));
  }
  @Override public String printTopType() {
    return "TOP";
  }
  @Override public String printBotType() {
    return "BOT";
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
    return String.format("%s* %s() {return nullptr;}", //
        printType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name());
  }
  @Override public String printTerminationMethod() {
    return String.format("virtual void %s() const;", terminationMethodName);
  }
  @Override public String printIntermediateMethod(APICompiler.MethodDeclaration declaration,
      PolymorphicTypeNode<APICompiler.TypeName> returnType) {
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
  @Override public String printInterface(APICompiler.InterfaceDeclaration declaration,
      List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("%s{public:%s};", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining()));
  }
  public String printTypeName(APICompiler.TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }
  @SuppressWarnings("static-method") public String printTypeName(Named q, Word<Named> α, Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
  public String printInterfaceDeclaration(APICompiler.InterfaceDeclaration declaration) {
    return declaration.typeVariables.isEmpty()
        ? String.format("class %s", printTypeName(declaration.q, declaration.α, declaration.legalJumps))
        : String.format("template<%s>class %s",
            declaration.typeVariables.stream().map(q -> "class " + q.name()).collect(Collectors.joining(",")), //
            printTypeName(declaration.q, declaration.α, declaration.legalJumps));
  }
}
