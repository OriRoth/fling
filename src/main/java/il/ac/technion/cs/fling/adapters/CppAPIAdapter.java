package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.*;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.Named;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.*;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.*;
import il.ac.technion.cs.fling.internal.grammar.rules.*;

/** C++ API adapter.
 *
 * @author Ori Roth */
public class CppAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String terminationMethodName;
  private final Namer namer;

  public CppAPIAdapter(final String terminationMethodName, final Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.namer = namer;
  }

  @Override public String printFluentAPI(
      final APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s%s%s", //
        fluentAPI.interfaces.stream().filter(i -> !i.isTop() && !i.isBot())
            .map(i -> printInterfaceDeclaration(i.declaration) + ";").collect(joining()), //
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(joining()), //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining()));
  }

  @Override public String printTopType() {
    return "TOP";
  }

  @Override public String printBotType() {
    return "BOT";
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
    return String.format("%s %s(){return %s();}", //
        printType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        printType(returnType));
  }

  @Override public String printTerminationMethod() {
    return String.format("void %s(){};", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    return String.format("%s %s(%s){return %s();};", //
        printType(returnType), //
        declaration.name.name(), //
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        printType(returnType));
  }

  @Override public String printTopInterface() {
    return String.format("class TOP{public:void %s(){};};", terminationMethodName);
  }

  @Override public String printBotInterface() {
    return "class BOT{};";
  }

  @Override public String printInterface(final APICompiler.InterfaceDeclaration declaration,
      final List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("%s{public:%s};", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining()));
  }

  public String printTypeName(final APICompiler.TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }

  @SuppressWarnings("static-method") public String printTypeName(final Named q, final Word<Named> α,
      final Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  public String printInterfaceDeclaration(final APICompiler.InterfaceDeclaration declaration) {
    return declaration.typeVariables.isEmpty()
        ? String.format("class %s", printTypeName(declaration.q, declaration.α, declaration.legalJumps))
        : String.format("template<%s>class %s",
            declaration.typeVariables.stream().map(q -> "class " + q.name()).collect(Collectors.joining(",")), //
            printTypeName(declaration.q, declaration.α, declaration.legalJumps));
  }
}
