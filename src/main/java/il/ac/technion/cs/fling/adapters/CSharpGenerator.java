package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends CLikeGenerator {
  public CSharpGenerator(final Linker namer) {
    super(namer);
  }
  @Override public String render(final MethodSignature s, final Type.Grounded returnType) {
    return String.format("public %s %s(%s){return new %s();}", //
        render(returnType), //
        s.name.name(), //
        printParametersList(s), //
        render(returnType));
  }
  @Override void visit(final Type t) {
    final String printTypeName = fullName(t);
    return s.parameters.isEmpty() ? String.format("public class %s", printTypeName)
        : String.format("public class %s<%s>%s", //
            printTypeName, //
            s.parameters().map(this::typeVariableName) //
                .collect(Collectors.joining(",")),
            s.parameters().map(this::typeVariableName) //
                .map(n -> "where " + n + ":new()") //
                .collect(Collectors.joining("")) //
        );
  }
  @Override public String render(final TypeSignature s, final List<Method> methods) {
    return String.format("%s{%s}", //
        render(s), //
        methods.stream().map(this::render).collect(joining()));
  }
  @Override public String renderTypeBottom() {
    return "private class BOT{}";
  }
  @Override public String renderTypeTop() {
    return String.format("public class TOP { public void %s(){} }", endName());
  }
  @Override public String renderMethod(final MethodSignature s, final Type.Grounded returnType) {
    return String.format("public static %s %s(){return new %s();}", //
        render(returnType), //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        render(returnType));
  }
  @Override public String renderTerminationMethod() {
    return String.format("public void %s(){}", endName());
  }
  public String typeVariableName(final Named typeVariable) {
    return "_" + typeVariable.name();
  }
  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
  }
  @Override String render(final Model m) {
    return String.format("%s%s", //
        m.types().map(this::render).collect(joining()), //
        m.starts().map(this::render).collect(joining())) //
        .replace("$", "τ");
  }
  @Override String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    // TODO: manage this HACK
    return α == null ? qn.contains("_") ? qn : typeVariableName(q)
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
}
