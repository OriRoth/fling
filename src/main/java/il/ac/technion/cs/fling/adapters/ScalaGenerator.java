package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** Scala API adapter.
 *
 * @author Ori Roth */
public class ScalaGenerator extends CLikeGenerator {
  public ScalaGenerator(final Linker namer) {
    super(namer);
  }
  private String printParametersList(final MethodSignature s) {
    return render(s.parameters());
  }
  public String printTypeInstantiation(final Grounded returnType) {
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
  @Override public String render(final MethodSignature s, final Grounded returnType) {
    final String _returnType = render(returnType);
    final String returnValue = printTypeInstantiation(returnType);
    return String.format("def %s(%s):%s=%s", //
        s.name.name(), //
        printParametersList(s), //
        _returnType, //
        returnValue);
  }
  @Override String visit(final Model m) {
    return String.format("%s\n%s", //
        m.types().map(this::renderInstnatiation).collect(joining("\n")), //
        m.starts().map(this::renderInstnatiation).collect(joining("\n")));
  }
  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    return α == null ? qn
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
  @Override public String renderInstnatiation(final QAlphaTypeName name, final List<Grounded> typeArguments) {
    return String.format("%s[%s]", //
        render(name), //
        typeArguments.stream().map(this::render).collect(joining(",")));
  }
  @Override public String render(final QAlphaTypeName s) {
    final String typeName = render(s.q, s.α, s.legalJumps);
    final String typeParameters = s.parameters().map(Named::name).collect(Collectors.joining(","));
    return String.linef("class %s", //
        s.parameters.isEmpty() ? //
            typeName //
            : String.format("%s[%s]", typeName, typeParameters));
  }
  @Override public String renderInstnatiation(final QAlphaTypeName s, final List<Method> methods) {
    return String.format("%s(%s){\n%s\n}", //
        render(s), //
        printClassParameters(s.parameters), //
        methods.stream().map(this::renderInstnatiation).collect(joining("\n")));
  }
  @Override public String renderTypeBottom() {
    return "private class BOT{}";
  }
  @Override public String renderTypeTop() {
    return String.format("class TOP{\ndef %s():Unit={}\n}", endName());
  }
  @Override public String renderMethod(final MethodSignature s, final Grounded returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        render(returnType), //
        printTypeInstantiation(returnType));
  }
  @Override public String renderTerminationMethod() {
    return String.format("def %s():Unit={}", endName());
  }
  @SuppressWarnings("static-method") private String printClassParameters(final Word<Named> typeVariables) {
    return typeVariables.stream().map(Named::name) //
        .map(var -> String.format("val __%s:%s", var, var)) //
        .collect(joining(","));
  }
}
