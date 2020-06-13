package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Prototypical SML API adapter.
 *
 * @author Ori Roth */
public class SMLGenerator extends AbstractGenerator {
  private boolean firstDatatype;

  public SMLGenerator(final String terminationMethodName, final Namer namer) {
    super(terminationMethodName, namer);
    firstDatatype = true;
  }

  @Override public String printFluentAPI(
      final CompilationUnit<TypeName, MethodDeclaration, InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n\n%s", fluentAPI.interfaces.stream().map(this::printInterface).collect(joining(" ")),
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining("\n")));
  }

  @Override public String topTypeName() {
    return "TOP";
  }

  @Override public String bottomTypeName() {
    return "BOT";
  }

  @Override public String typeName(final TypeName name) {
    // TODO sanely check whether is type variable
    final String prefix = name.α == null && name.legalJumps == null ? "'" : "";
    return prefix + printTypeName(name);
  }

  @Override public String typeName(final TypeName name, final List<Type> typeArguments) {
    return typeArguments.isEmpty() ? printTypeName(name)
        : String.format("(%s) %s", //
            typeArguments.stream().map(this::printType).collect(joining(",")), //
            printTypeName(name));
  }

  @Override public String startMethod(final MethodDeclaration declaration, final Type returnType) {
    final String name = Constants.$$.equals(declaration.name) ? terminationMethodName : declaration.name.name();
    return String.format("fun main (%s:%s) = let\nin %s end", name, printType(returnType), name);
  }

  @Override public String printTerminationMethod() {
    return String.format("\t%s: TOP", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    if (!declaration.getInferredParameters().isEmpty()) {
      throw new RuntimeException("fluent API function parameters are not suported");
    }
    return String.format("\t%s: %s", declaration.name.name(), printType(returnType));
  }

  @Override public String printTopInterface() {
    return String.format("%s TOP = SUCCESS", getDatatypeKeyword());
  }

  @Override public String printBotInterface() {
    return String.format("%s BOT = FAILURE", getDatatypeKeyword());
  }

  @Override public String printInterface(final InterfaceDeclaration declaration, final List<AbstractMethod> methods) {
    return String.format("%s of {\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining(",\n")));
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
    final String variables = declaration.typeVariables.isEmpty() ? ""
        : String.format("(%s) ",
            declaration.typeVariables.stream().map(Named::name).map(n -> "'" + n).collect(joining(", ")), name);
    return String.format("%s %s%s = %s", getDatatypeKeyword(), variables, name, name);
  }

  private String getDatatypeKeyword() {
    if (!firstDatatype)
      return "and";
    firstDatatype = false;
    return "datatype";
  }
}
