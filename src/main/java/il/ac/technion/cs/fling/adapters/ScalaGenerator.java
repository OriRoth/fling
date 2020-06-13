package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolymorphicType;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Scala API adapter.
 *
 * @author Ori Roth */
public class ScalaGenerator extends AbstractGenerator {

  public ScalaGenerator(final String terminationMethodName, final Namer namer) {
    super(terminationMethodName, namer);
  }

  @Override public String printFluentAPI(
      final CompilationUnit<TypeName, MethodDeclaration, InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n%s", //
        fluentAPI.interfaces.stream().map(this::printInterface).collect(joining("\n")), //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining("\n")));
  }

  @Override public String topTypeName() {
    return "TOP";
  }

  @Override public String bottomTypeName() {
    return "BOT";
  }

  @Override public String typeName(final TypeName name) {
    return printTypeName(name);
  }

  @Override public String typeName(final TypeName name, final List<PolymorphicType<TypeName>> typeArguments) {
    return String.format("%s[%s]", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }

  @Override public String startMethod(final MethodDeclaration declaration, final PolymorphicType<TypeName> returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        printType(returnType), //
        printTypeInstantiation(returnType));
  }

  @Override public String printTerminationMethod() {
    return String.format("def %s():Unit={}", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration,
      final PolymorphicType<TypeName> returnType) {
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

  @Override public String printInterface(final InterfaceDeclaration declaration,
      final List<AbstractMethod<TypeName, MethodDeclaration>> methods) {
    return String.format("%s(%s){\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        printClassParameters(declaration.typeVariables), //
        methods.stream().map(this::printMethod).collect(joining("\n")));
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
    return declaration.getInferredParameters().stream() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  public String printInterfaceDeclaration(final InterfaceDeclaration declaration) {
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

  public String printTypeInstantiation(final PolymorphicType<TypeName> returnType) {
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
