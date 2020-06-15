package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

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

/** C++ API adapter.
 *
 * @author Ori Roth */
public class CPPGenerator extends APIGenerator {

  public CPPGenerator(final Namer namer) {
    this(namer, "$");
  }

  public CPPGenerator(final Namer namer, final String endName) {
    super(namer, endName);
  }

  @Override public String render(final MethodSignature s, final SkeletonType returnType) {
    return String.format("%s %s(%s) { return %s(); }", //
        render(returnType), //
        s.name.name(), //
        s.parmeters() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        render(returnType));
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @Override public String render(final TypeName name) {
    return render(name.q, name.α, name.legalJumps);
  }

  @Override public String render(final TypeName name, final List<SkeletonType> typeArguments) {
    return String.format("%s<%s>", //
        render(name), //
        typeArguments.stream().map(this::render).collect(joining(",")));
  }

  @Override public String render(final TypeSignature s) {
    final String printTypeName = render(s.q, s.α, s.legalJumps);
    return s.parameters.isEmpty() ? String.format("class %s", printTypeName)
        : String.format("template <%s> class %s",
            s.parameters().map(q -> "typename " + q.name()).collect(Collectors.joining(", ")), //
            printTypeName);
  }

  @Override public String render(final TypeSignature s, final List<Method> methods) {
    return String.format("%s {\n\tpublic:\n\t\t%s\n};\n", //
        render(s), //
        methods.stream().map(this::render).collect(joining("\n\t\t")));
  }

  @Override public String renderInterfaceBottom() {
    return "class BOT{};";
  }

  @Override public String renderInterfaceTop() {
    return String.format("class TOP{\n\tpublic:\n\tvoid %s(){};};", endName);
  }

  @Override public String renderMethod(final MethodSignature s, final SkeletonType returnType) {
    return String.format("%s %s() { return %s(); }\n", //
        render(returnType), //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        render(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("void %s(){};", endName);
  }

  @Override protected String comment(String comment) {
    return "/* " + comment + "*/";
  }

  @Override String render(final Model m) {
    return String.format("%s%s%s", //
        m.types().filter(i -> !i.isTop() && !i.isBot()).map(i -> render(i.signature) + ";\n").collect(joining()), //
        m.types().map(this::render).collect(joining()), //
        m.starts().map(this::render).collect(joining()));
  }
}
