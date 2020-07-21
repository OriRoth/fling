package il.ac.technion.cs.fling.adapters._;
import static java.util.stream.Collectors.joining;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends CLikeGenerator {
  public CSharpGenerator(final Linker namer) {
    super(namer);
  }
  private String render(final Method m) {
    return String.format("public %s %s(%s){return new %s();}", //
        render(m.type), //
        m.name.name(), //
        printParametersList(m), //
        render(m.type));
  }
  @Override void visit(final Type t) {
    final var printTypeName = fullName(t);
    return t.parameters.isEmpty() ? String.format("public class %s", printTypeName)
        : String.format("public class %s<%s>%s", //
            printTypeName, //
            t.parameters().map(this::typeVariableName) //
                .collect(commas()),
            t.parameters().map(this::typeVariableName) //
                .map(n -> "where " + n + ":new()") //
                .collect(commas()) //
        );
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
  private String typeVariableName(final Named typeVariable) {
    return "_" + typeVariable.name();
  }
  @Override String render(final Model m) {
    return String.format("%s%s", //
        m.types().map(this::render).collect(joining()), //
        m.starts().map(this::render).collect(joining())) //
        .replace("$", "τ");
  }
}
