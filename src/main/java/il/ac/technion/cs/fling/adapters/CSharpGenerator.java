package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends APIGenerator {
  public CSharpGenerator(final Namer namer, final String endName) {
    super(namer, endName);
  }

  @SuppressWarnings("static-method") public String printParametersList(final MethodSignature s) {
    return s.parmeters() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  @Override public String render(final MethodSignature s, final SkeletonType returnType) {
    return String.format("public %s %s(%s){return new %s();}", //
        render(returnType), //
        s.name.name(), //
        printParametersList(s), //
        render(returnType));
  }

  @Override public String render(final Model m) {
    namer.name(m);
    return String.format("%s%s", //
        m.types().map(this::render).collect(joining()), //
        m.starts().map(this::render).collect(joining())) //
        .replace("$", "τ");
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    // TODO: manage this HACK
    return α == null ? qn.contains("_") ? qn : typeVariableName(q)
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @Override public String render(final TypeName name) {
    return render(name.q, name.α, name.legalJumps);
  }

  @Override public String render(final TypeName name, final List<SkeletonType> arguments) {
    return String.format("%s<%s>", //
        render(name), //
        arguments.stream().map(this::render).collect(joining(",")));
  }

  @Override public String render(final TypeSignature declaration) {
    final String printTypeName = render(declaration.q, declaration.α, declaration.legalJumps);
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

  @Override public String render(final TypeSignature declaration, final List<Method> methods) {
    return String.format("%s{%s}", //
        render(declaration), //
        methods.stream().map(this::render).collect(joining()));
  }

  @Override public String renderInterfaceBottom() {
    return "private class BOT{}";
  }

  @Override public String renderInterfaceTop() {
    return String.format("public class TOP{public void %s(){}}", endName);
  }

  @Override public String renderMethod(final MethodSignature s, final SkeletonType returnType) {
    return String.format("public static %s %s(){return new %s();}", //
        render(returnType), //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        render(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("public void %s(){}", endName);
  }

  public String typeVariableName(final Named typeVariable) {
    return "_" + typeVariable.name();
  }

  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
  }
}
