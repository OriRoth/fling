package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeSignature;
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

  @SuppressWarnings("static-method") public String printParametersList(final MethodSignature s) {
    return s.parmeters() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  public String printTypeInstantiation(final SkeletonType returnType) {
    final String _returnType = render(returnType);
    // TODO manage this HACK
    return !Arrays.asList("TOP", "BOT").contains(_returnType) //
        && !_returnType.contains("_") ? //
            "__" + _returnType
            : String.format("new %s(%s)", _returnType, //
                returnType.arguments() //
                    .map(this::printTypeInstantiation) //
                    .collect(joining(",")));
  }

  @Override public String render(final MethodSignature s, final SkeletonType returnType) {
    final String _returnType = render(returnType);
    final String returnValue = printTypeInstantiation(returnType);
    return String.format("def %s(%s):%s=%s", //
        s.name.name(), //
        printParametersList(s), //
        _returnType, //
        returnValue);
  }

  @Override String render(final Model m) {
    return String.format("%s\n%s", //
        m.types().map(this::render).collect(joining("\n")), //
        m.starts().map(this::render).collect(joining("\n")));
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    return α == null ? qn
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @Override public String render(final TypeName name) {
    return render(name.q, name.α, name.legalJumps);
  }

  @Override public String render(final TypeName name, final List<SkeletonType> typeArguments) {
    return String.format("%s[%s]", //
        render(name), //
        typeArguments.stream().map(this::render).collect(joining(",")));
  }

  @Override public String render(final TypeSignature s) {
    final String typeName = render(s.q, s.α, s.legalJumps);
    final String typeParameters = s.parameters().map(Named::name).collect(Collectors.joining(","));
    return String.format("class %s", //
        s.parameters.isEmpty() ? //
            typeName //
            : String.format("%s[%s]", typeName, typeParameters));
  }

  @Override public String render(final TypeSignature s, final List<Method> methods) {
    return String.format("%s(%s){\n%s\n}", //
        render(s), //
        printClassParameters(s.parameters), //
        methods.stream().map(this::render).collect(joining("\n")));
  }

  @Override public String renderInterfaceBottom() {
    return "private class BOT{}";
  }

  @Override public String renderInterfaceTop() {
    return String.format("class TOP{\ndef %s():Unit={}\n}", endName);
  }

  @Override public String renderMethod(final MethodSignature s, final SkeletonType returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        render(returnType), //
        printTypeInstantiation(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("def %s():Unit={}", endName);
  }

  @SuppressWarnings("static-method") private String printClassParameters(final Word<Named> typeVariables) {
    return typeVariables.stream().map(Named::name) //
        .map(var -> String.format("val __%s:%s", var, var)) //
        .collect(joining(","));
  }

  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
  }
}
