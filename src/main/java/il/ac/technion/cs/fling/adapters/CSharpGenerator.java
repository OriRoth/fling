package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

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

/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends APIGenerator {
  public CSharpGenerator(final String endName, final Namer namer) {
    super(namer, endName);
  }

  @Override public String renderCompilationUnit(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s%s", //
        fluentAPI.interfaces().map(this::renderInterface).collect(joining()), //
        fluentAPI.startMethods().map(this::renderMethod).collect(joining())) //
        .replace("$", "τ");
  }

  @Override public String renderTypeMonomorphic(final TypeName name) {
    return printTypeName(name);
  }

  @Override public String renderTypePolymorphic(final TypeName name, final List<Type> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::renderType).collect(joining(",")));
  }

  @Override public String renderMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("public static %s %s(){return new %s();}", //
        renderType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        renderType(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("public void %s(){}", endName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("public %s %s(%s){return new %s();}", //
        renderType(returnType), //
        declaration.name.name(), //
        printParametersList(declaration), //
        renderType(returnType));
  }

  @Override public String renderInterfaceTop() {
    return String.format("public class TOP{public void %s(){}}", endName);
  }

  @Override public String renderInterfaceBottom() {
    return "private class BOT{}";
  }

  @Override public String renderInterface(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("%s{%s}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::renderMethod).collect(joining()));
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
    return declaration.parmeters() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  public String printInterfaceDeclaration(final InterfaceDeclaration declaration) {
    final String printTypeName = printTypeName(declaration.q, declaration.α, declaration.legalJumps);
    return declaration.parameters.isEmpty() ? String.format("public class %s", printTypeName)
        : String.format("public class %s<%s>%s", //
            printTypeName, //
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
