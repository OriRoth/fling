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

  public SMLGenerator(final Namer namer, final String endName) {
    super(namer, endName);
    firstDatatype = true;
  }

  @Override protected String comment(String comment) {
    return String.format("(* %s *)", comment);
  }

  private String getDatatypeKeyword() {
    if (!firstDatatype)
      return "and";
    firstDatatype = false;
    return "datatype";
  }

  @Override public String render(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n\n%s", fluentAPI.interfaces().map(this::renderInterface).collect(joining(" ")),
        fluentAPI.startMethods().map(this::render).collect(joining("\n")));
  }

  @Override public String render(final InterfaceDeclaration declaration) {
    final String name = render(declaration.q, declaration.α, declaration.legalJumps);
    final String variables = declaration.parameters.isEmpty() ? ""
        : String.format("(%s) ", declaration.parameters().map(Named::name).map(n -> "'" + n).collect(joining(", ")),
            name);
    return String.format("%s %s%s = %s", getDatatypeKeyword(), variables, name, name);
  }

  @Override public String render(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("%s of {\n%s\n}", //
        render(declaration), //
        methods.stream().map(this::render).collect(joining(",\n")));
  }

  @Override public String render(final MethodDeclaration declaration, final Type returnType) {
    if (!declaration.getInferredParameters().isEmpty())
      throw new RuntimeException("fluent API function parameters are not suported");
    return String.format("\t%s: %s", declaration.name.name(), render(returnType));
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    assert q.name() != null && !q.name().isEmpty();
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @Override public String render(final TypeName name) {
    // TODO sanely check whether is type variable
    final String prefix = name.α == null && name.legalJumps == null ? "'" : "";
    return prefix + render(name.q, name.α, name.legalJumps);
  }

  @Override public String render(final TypeName name, final List<Type> typeArguments) {
    return typeArguments.isEmpty() ? render(name)
        : String.format("(%s) %s", //
            typeArguments.stream().map(this::render).collect(joining(",")), //
            render(name));
  }

  @Override public String renderInterfaceBottom() {
    return String.format("%s BOT = FAILURE", getDatatypeKeyword());
  }

  @Override public String renderInterfaceTop() {
    return String.format("%s TOP = SUCCESS", getDatatypeKeyword());
  }

  @Override public String renderMethod(final MethodDeclaration declaration, final Type returnType) {
    final String name = Constants.$$.equals(declaration.name) ? endName : declaration.name.name();
    return String.format("fun main (%s:%s) = let\nin %s end", name, render(returnType), name);
  }

  @Override public String renderTerminationMethod() {
    return String.format("\t%s: TOP", endName);
  }
}
