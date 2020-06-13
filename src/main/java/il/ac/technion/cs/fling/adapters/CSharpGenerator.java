package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends AbstractGenerator {
  public CSharpGenerator(final String terminationMethodName, final Namer namer) {
    super(terminationMethodName, namer);
  }

  @Override public String printFluentAPI(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s%s", //
        fluentAPI.interfaces().map(this::printInterface).collect(joining()), //
        fluentAPI.startMethods().map(this::printMethod).collect(joining())) //
        .replace("$", "τ");
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

  @Override public String typeName(final TypeName name, final List<Type> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }

  @Override public String startMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("public static %s %s(){return new %s();}", //
        printType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        printType(returnType));
  }

  @Override public String printTerminationMethod() {
    return String.format("public void %s(){}", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("public %s %s(%s){return new %s();}", //
        printType(returnType), //
        declaration.name.name(), //
        printParametersList(declaration), //
        printType(returnType));
  }

  @Override public String printTopInterface() {
    return String.format("public class TOP{public void %s(){}}", terminationMethodName);
  }

  @Override public String printBotInterface() {
    return "private class BOT{}";
  }

  @Override public String printInterface(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("%s{%s}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining()));
  }

  public String printTypeName(final TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }

  public String printTypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    // TODO: manage this HACK
    return α == null ? qn.contains("_") ? qn : typeVariableName(q)
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
    return declaration.parameters.isEmpty()
        ? String.format("public class %s", printTypeName(declaration.q, declaration.α, declaration.legalJumps))
        : String.format("public class %s<%s>%s", //
            printTypeName(declaration.q, declaration.α, declaration.legalJumps), //
            declaration.parameters().map(this::typeVariableName) //
                .collect(Collectors.joining(",")),
            declaration.parameters().map(this::typeVariableName) //
                .map(n -> "where " + n + ":new()") //
                .collect(Collectors.joining("")) //
        );
  }

  public String typeVariableName(final Named typeVariable) {
    return "_" + typeVariable.name();
  }
}
