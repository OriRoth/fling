package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** Prototypical SML API adapter.
 *
 * @author Ori Roth */
public class SMLGenerator extends APIGenerator {
  private boolean firstDatatype;
  public SMLGenerator(final Linker namer, final String endName) {
    super(namer);
    firstDatatype = true;
  }
  @Override public String render(final MethodSignature s, final Grounded returnType) {
    if (!s.parameters().isEmpty())
      throw new RuntimeException("fluent API function parameters are not suported");
    return String.format("\t%s: %s", s.name.name(), render(returnType));
  }
  @Override String render(final Model m) {
    return String.format("%s\n\n%s", m.types().map(this::renderInstnatiation).collect(joining(" ")),
        m.starts().map(this::renderInstnatiation).collect(joining("\n")));
  }
  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    assert q.name() != null && !q.name().isEmpty();
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }
  @Override public String render(final Name name) {
    // TODO sanely check whether is type variable
    final String prefix = name.α == null && name.legalJumps == null ? "'" : "";
    return prefix + render(name.q, name.α, name.legalJumps);
  }
  @Override public String renderInstnatiation(final Name name, final List<Grounded> typeArguments) {
    return typeArguments.isEmpty() ? render(name)
        : String.format("(%s) %s", //
            typeArguments.stream().map(this::render).collect(joining(",")), //
            render(name));
  }
  @Override public String render(final QAlphaTypeName s) {
    final String name = render(s.q, s.α, s.legalJumps);
    final String variables = s.parameters.isEmpty() ? ""
        : String.format("(%s) ", s.parameters().map(Named::name).map(n -> "'" + n).collect(joining(", ")), name);
    return String.format("%s %s%s = %s", getDatatypeKeyword(), variables, name, name);
  }
  @Override public String render(final QAlphaTypeName s, final List<Method> methods) {
    return String.format("%s of {\n%s\n}", //
        render(s), //
        methods.stream().map(this::renderInstnatiation).collect(joining(",\n")));
  }
  @Override public String renderTypeBottom() {
    return String.format("%s BOT = FAILURE", getDatatypeKeyword());
  }
  @Override public String renderTypeTop() {
    return String.format("%s TOP = SUCCESS", getDatatypeKeyword());
  }
  @Override public String renderMethod(final MethodSignature s, final Grounded returnType) {
    final String name = Constants.$$.equals(s.name) ? endName() : s.name.name();
    return String.format("fun main (%s:%s) = let\nin %s end", name, render(returnType), name);
  }
  @Override public String renderTerminationMethod() {
    return String.format("\t%s: TOP", endName());
  }
  private String getDatatypeKeyword() {
    if (!firstDatatype)
      return "and";
    firstDatatype = false;
    return "datatype";
  }
  @Override protected String comment(final String comment) {
    return String.format("(* %s *)", comment);
  }
  @SuppressWarnings("static-method") String render(final Stream<MethodParameter> ps) {
    return ps.map(p -> p.type + " " + p.name).collect(joining(", "));
  }
}
