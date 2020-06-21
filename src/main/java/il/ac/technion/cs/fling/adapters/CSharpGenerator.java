package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
/** C# API adapter.
 *
 * @author Ori Roth */
public class CSharpGenerator extends CLikeGenerator {
  public CSharpGenerator(final Linker namer) {
    super(namer);
  }
  public String printParametersList(final Method s) {
    final Stream<MethodParameter> parmeters = s.parameters();
    return render(parmeters);
  }
  @Override public String render(final Method s, final Type.Grounded returnType) {
    return String.format("public %s %s(%s){return new %s();}", //
        render(returnType), //
        s.name.name(), //
        printParametersList(s), //
        render(returnType));
  }
  @Override public String render(final Name name) {
    return render(name.q, name.α, name.legalJumps);
  }
  private String render(final Name name, final List<Type.Grounded> arguments) {
    return String.format("%s<%s>", //
        render(name), //
        arguments.stream().map(this::render).collect(joining(",")));
  }
  @Override public String render(final Type.Name s) {
    final String printTypeName = render(s.q, s.α, s.legalJumps);
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
  private String render(final QAlphaTypeName s, final List<Method> methods) {
    return String.format("%s{%s}", //
        render(s), //
        methods.stream().map(this::renderInstnatiation).collect(joining()));
  }
  @Override public String renderMethod(final Method s, final Type.Grounded returnType) {
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
  @Override String render(final Model m) {
    return String.format("%s%s", //
        m.types().map(this::renderInstnatiation).collect(joining()), //
        m.starts().map(this::renderInstnatiation).collect(joining())) //
        .replace("$", "τ");
  }
}
