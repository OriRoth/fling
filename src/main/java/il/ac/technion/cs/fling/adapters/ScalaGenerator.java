package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Scala API adapter.
 *
 * @author Ori Roth */
public class ScalaGenerator extends APIGenerator {

  public ScalaGenerator(final String endName, final Namer namer) {
    super(namer, endName);
  }

  @Override public String renderCompilationUnit(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n%s", //
        fluentAPI.interfaces().map(this::renderInterface).collect(joining("\n")), //
        fluentAPI.startMethods().map(this::renderMethod).collect(joining("\n")));
  }

  @Override public String renderTypeMonomorphic(final TypeName name) {
    return printTypeName(name);
  }

  @Override public String renderTypePolymorphic(final TypeName name, final List<Type> typeArguments) {
    return String.format("%s[%s]", //
        printTypeName(name), //
        typeArguments.stream().map(this::renderType).collect(joining(",")));
  }

  @Override public String renderMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        renderType(returnType), //
        printTypeInstantiation(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("def %s():Unit={}", endName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    final String _returnType = renderType(returnType);
    final String returnValue = printTypeInstantiation(returnType);
    return String.format("def %s(%s):%s=%s", //
        declaration.name.name(), //
        printParametersList(declaration), //
        _returnType, //
        returnValue);
  }

  @Override public String renderInterfaceTop() {
    return String.format("class TOP{\ndef %s():Unit={}\n}", endName);
  }

  @Override public String renderInterfaceBottom() {
    return "private class BOT{}";
  }

  @Override public String renderInterface(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("%s(%s){\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        printClassParameters(declaration.parameters), //
        methods.stream().map(this::renderMethod).collect(joining("\n")));
  }

  public String printTypeName(final TypeName name) {
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

  @SuppressWarnings("static-method") public String printParametersList(final MethodDeclaration declaration) {
    return declaration.parmeters() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  public String printInterfaceDeclaration(final InterfaceDeclaration declaration) {
    final String typeName = printTypeName(declaration.q, declaration.α, declaration.legalJumps);
    final String typeParameters = declaration.parameters().map(Named::name) //
        .collect(Collectors.joining(","));
    return String.format("class %s", //
        declaration.parameters.isEmpty() ? //
            typeName //
            : String.format("%s[%s]", typeName, typeParameters));
  }

  @SuppressWarnings("static-method") private String printClassParameters(final Word<Named> typeVariables) {
    return typeVariables.stream().map(Named::name) //
        .map(var -> String.format("val __%s:%s", var, var)) //
        .collect(joining(","));
  }

  public String printTypeInstantiation(final Type returnType) {
    final String _returnType = renderType(returnType);
    // TODO manage this HACK
    return !Arrays.asList("TOP", "BOT").contains(_returnType) //
        && !_returnType.contains("_") ? //
            "__" + _returnType
            : String.format("new %s(%s)", _returnType, //
                returnType.arguments() //
                    .map(this::printTypeInstantiation) //
                    .collect(joining(",")));
  }
}
