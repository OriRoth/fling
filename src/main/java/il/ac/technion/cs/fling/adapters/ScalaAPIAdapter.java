package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.PolymorphicLanguageAPIBaseAdapter;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.APICompilationUnitNode;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.AbstractMethodNode;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.PolymorphicTypeNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Scala API adapter.
 *
 * @author Ori Roth */
public class ScalaAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String terminationMethodName;
  private final Namer namer;

  public ScalaAPIAdapter(final String terminationMethodName, final Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.namer = namer;
  }

  @Override public String printFluentAPI(
      final APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n%s", //
        fluentAPI.interfaces.stream().map(this::printInterface).collect(joining("\n")), //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining("\n")));
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
    return String.format("%s[%s]", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }

  @Override public String printStartMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        printType(returnType), //
        printTypeInstantiation(returnType));
  }

  @Override public String printTerminationMethod() {
    return String.format("def %s():Unit={}", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    final String _returnType = printType(returnType);
    final String returnValue = printTypeInstantiation(returnType);
    return String.format("def %s(%s):%s=%s", //
        declaration.name.name(), //
        printParametersList(declaration), //
        _returnType, //
        returnValue);
  }

  @Override public String printTopInterface() {
    return String.format("class TOP{\ndef %s():Unit={}\n}", terminationMethodName);
  }

  @Override public String printBotInterface() {
    return "private class BOT{}";
  }

  @Override public String printInterface(final APICompiler.InterfaceDeclaration declaration,
      final List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("%s(%s){\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        printClassParameters(declaration.typeVariables), //
        methods.stream().map(this::printMethod).collect(joining("\n")));
  }

  public String printTypeName(final APICompiler.TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }

  @SuppressWarnings("static-method") public String printTypeName(final Named q, final Word<Named> α,
      final Set<Named> legalJumps) {
    final String qn = q.name();
    return α == null ? qn
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @SuppressWarnings("static-method") public String printParametersList(
      final APICompiler.MethodDeclaration declaration) {
    return declaration.getInferredParameters().stream() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  public String printInterfaceDeclaration(final APICompiler.InterfaceDeclaration declaration) {
    final String typeName = printTypeName(declaration.q, declaration.α, declaration.legalJumps);
    final String typeParameters = declaration.typeVariables.stream().map(Named::name) //
        .collect(Collectors.joining(","));
    return String.format("class %s", //
        declaration.typeVariables.isEmpty() ? //
            typeName //
            : String.format("%s[%s]", typeName, typeParameters));
  }

  @SuppressWarnings("static-method") private String printClassParameters(final Word<Named> typeVariables) {
    return typeVariables.stream().map(Named::name) //
        .map(var -> String.format("val __%s:%s", var, var)) //
        .collect(joining(","));
  }

  public String printTypeInstantiation(final PolymorphicTypeNode<TypeName> returnType) {
    final String _returnType = printType(returnType);
    // TODO manage this HACK
    return !Arrays.asList("TOP", "BOT").contains(_returnType) //
        && !_returnType.contains("_") ? //
            "__" + _returnType
            : String.format("new %s(%s)", _returnType, //
                returnType.typeArguments == null ? "" : //
                    returnType.typeArguments.stream() //
                        .map(this::printTypeInstantiation) //
                        .collect(joining(",")));
  }
}
