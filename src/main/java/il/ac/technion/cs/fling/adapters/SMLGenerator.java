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

/** Prototypical SML API adapter.
 *
 * @author Ori Roth */
public class SMLGenerator extends APIGenerator {
  private boolean firstDatatype;

  public SMLGenerator(final String endName, final Namer namer) {
    super(namer, endName);
    firstDatatype = true;
  }

  @Override public String renderCompilationUnit(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n\n%s", fluentAPI.interfaces().map(this::renderInterface).collect(joining(" ")),
        fluentAPI.startMethods().map(this::renderMethod).collect(joining("\n")));
  }

  @Override public String renderTypeMonomorphic(final TypeName name) {
    // TODO sanely check whether is type variable
    final String prefix = name.α == null && name.legalJumps == null ? "'" : "";
    return prefix + printTypeName(name);
  }

  @Override public String renderTypePolymorphic(final TypeName name, final List<Type> typeArguments) {
    return typeArguments.isEmpty() ? printTypeName(name)
        : String.format("(%s) %s", //
            typeArguments.stream().map(this::renderType).collect(joining(",")), //
            printTypeName(name));
  }

  @Override public String renderMethod(final MethodDeclaration declaration, final Type returnType) {
    final String name = Constants.$$.equals(declaration.name) ? endName : declaration.name.name();
    return String.format("fun main (%s:%s) = let\nin %s end", name, renderType(returnType), name);
  }

  @Override public String renderTerminationMethod() {
    return String.format("\t%s: TOP", endName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    if (!declaration.getInferredParameters().isEmpty())
      throw new RuntimeException("fluent API function parameters are not suported");
    return String.format("\t%s: %s", declaration.name.name(), renderType(returnType));
  }

  @Override public String renderInterfaceTop() {
    return String.format("%s TOP = SUCCESS", getDatatypeKeyword());
  }

  @Override public String renderInterfaceBottom() {
    return String.format("%s BOT = FAILURE", getDatatypeKeyword());
  }

  @Override public String renderInterface(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("%s of {\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::renderMethod).collect(joining(",\n")));
  }

  public String printTypeName(final TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }

  @SuppressWarnings("static-method") public String printTypeName(final Named q, final Word<Named> α,
      final Set<Named> legalJumps) {
    assert q.name() != null && !q.name().isEmpty();
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  public String printInterfaceDeclaration(final InterfaceDeclaration declaration) {
    final String name = printTypeName(declaration.q, declaration.α, declaration.legalJumps);
    final String variables = declaration.parameters.isEmpty() ? ""
        : String.format("(%s) ", declaration.parameters().map(Named::name).map(n -> "'" + n).collect(joining(", ")),
            name);
    return String.format("%s %s%s = %s", getDatatypeKeyword(), variables, name, name);
  }

  private String getDatatypeKeyword() {
    if (!firstDatatype)
      return "and";
    firstDatatype = false;
    return "datatype";
  }
}
