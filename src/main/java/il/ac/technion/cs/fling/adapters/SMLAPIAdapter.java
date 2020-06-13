package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.PolymorphicLanguageAPIBaseAdapter;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.APICompilationUnitNode;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.AbstractMethodNode;
import il.ac.technion.cs.fling.internal.compiler.api.nodes.PolymorphicTypeNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Prototypical SML API adapter.
 *
 * @author Ori Roth */
public class SMLAPIAdapter implements PolymorphicLanguageAPIBaseAdapter {
  private final String terminationMethodName;
  private final Namer namer;
  private boolean firstDatatype;

  public SMLAPIAdapter(final String terminationMethodName, final Namer namer) {
    this.terminationMethodName = terminationMethodName;
    this.namer = namer;
    firstDatatype = true;
  }

  @Override public String printFluentAPI(
      final APICompilationUnitNode<APICompiler.TypeName, APICompiler.MethodDeclaration, APICompiler.InterfaceDeclaration> fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n\n%s", fluentAPI.interfaces.stream().map(this::printInterface).collect(joining(" ")),
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining("\n")));
  }

  @Override public String printTopType() {
    return "TOP";
  }

  @Override public String printBotType() {
    return "BOT";
  }

  @Override public String printIntermediateType(final APICompiler.TypeName name) {
    // TODO sanely check whether is type variable
    final String prefix = name.α == null && name.legalJumps == null ? "'" : "";
    return prefix + printTypeName(name);
  }

  @Override public String printIntermediateType(final APICompiler.TypeName name,
      final List<PolymorphicTypeNode<APICompiler.TypeName>> typeArguments) {
    return typeArguments.isEmpty() ? printTypeName(name)
        : String.format("(%s) %s", //
            typeArguments.stream().map(this::printType).collect(joining(",")), //
            printTypeName(name));
  }

  @Override public String printStartMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
    final String name = Constants.$$.equals(declaration.name) ? terminationMethodName : declaration.name.name();
    return String.format("fun main (%s:%s) = let\nin %s end", name, printType(returnType), name);
  }

  @Override public String printTerminationMethod() {
    return String.format("\t%s: TOP", terminationMethodName);
  }

  @Override public String printIntermediateMethod(final APICompiler.MethodDeclaration declaration,
      final PolymorphicTypeNode<APICompiler.TypeName> returnType) {
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

  @Override public String printInterface(final APICompiler.InterfaceDeclaration declaration,
      final List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> methods) {
    return String.format("%s of {\n%s\n}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(this::printMethod).collect(joining(",\n")));
  }

  public String printTypeName(final APICompiler.TypeName name) {
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

  public String printInterfaceDeclaration(final APICompiler.InterfaceDeclaration declaration) {
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
