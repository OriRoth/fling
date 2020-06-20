package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.stream.Collectors;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth */
public class JavaGenerator extends CLikeGenerator {
  private final String className;
  private final String packageName;
  public JavaGenerator(final Linker namer, final String packageName, final String className) {
    super(namer);
    this.packageName = packageName;
    this.className = className;
    bottomName("ø");
    topName("$");
    endName("$");
  }
  @Override public void visit(final Model m) {
    indent();
    if (packageName == null)
      linef("package %s;", packageName);
    ____();
    line("import java.util.*;");
    ____();
    linef("@SuppressWarnings(\"all\") public interface %s { ", className);
    indent();
    commentf("%d start methods", m.starts().count());
    m.starts().forEach(this::visit);
    ____();
    commentf("%d interfact types", m.types().count());
    m.types().forEach(this::visit);
    ____();
    commentf("Class implementing %d interfaces with %d methods", m.types().count(), m.methods().count());
    implementingClass(m);
    unindent();
    line("}");
  }
  @Override void visit(Type t) {
    // !t.isAccepting ? "" : " extends " + topName(), //
    line(fullName(t) + " {").indent();
    t.methods().forEach(this::visit);
    unindent().line("}");
  }
  @Override String fullName(Type t) {
    String $ = String.format("interface %s", render(t.name));
    if (t.isAccepting)
      $ += " extends " + topName();
    if (t.parameters.isEmpty())
      return $;
    return $ + String.format(" <%s>", t.parameters().map(Named::name).collect(Collectors.joining(", ")));
  }
  private String implementingClass(final Model m) {
    return String.format("static class α implements %s{%s%s%s}", //
        m.types().map(Type::name).map(this::render).collect(joining(",")), //
        implementingClassClassBody(), m.methods() //
            .map(s -> String.format("public α %s(%s){%sreturn this;}", //
                s.name.name(), //
                s.parameters() //
                    .map(parameter -> String.format("%s %s", //
                        parameter.type, //
                        parameter.name)) //
                    .collect(joining(",")), //
                implementingClassMethodBody(s.name, s.getInferredParameters()))) //
            .collect(joining()),
        String.format("public %s %s(){%s}", //
            printTerminationMethodReturnType(), //
            endName(), //
            printTerminationMethodConcreteBody()));
  }
  private String printTopInterfaceBody() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName());
  }
  public String renderTypeBottom() {
    return "interface ø {}";
  }
  public String renderTypeTop() {
    return String.format("interface ${%s}", printTopInterfaceBody());
  }
  public String renderMethod(final Method m) {
    return String.format("public static %s %s(%s) {%s}", //
        render(m.type), //
        Constants.$$.equals(m.name) ? "__" : m.name.name(), //
        m.parameters() //
            .map(parameter -> String.format("%s %s", parameter.type, parameter.name)) //
            .collect(joining(",")), //
        printStartMethodBody(m.name, m.getInferredParameters()));
  }
  public String renderTerminationMethod() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName());
  }
  /** Additional declaration within the top class.
   *
   * @return additional declarations */
  protected String printAdditionalDeclarations() {
    return "";
  }
  /** Prints additional definition in concrete implementation class's body.
   *
   * @return additional definition */
  protected String implementingClassClassBody() {
    return "";
  }
  /** Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ          current token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String implementingClassMethodBody(final Token σ,
      final List<MethodParameter> parameters) {
    return "";
  }
  /** Start static method body.
   *
   * @param σ          inducing token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printStartMethodBody(final Token σ,
      final List<MethodParameter> parameters) {
    return "return new α();";
  }
  /** Concrete implementation's termination method body. Might be used to create
   * and return the processed terminal.
   *
   * @return method body */
  protected String printTerminationMethodConcreteBody() {
    return "";
  }
  /** Return type of the termination method.
   *
   * @return return type */
  protected String printTerminationMethodReturnType() {
    return "void";
  }
  @Override void visit(Method m) {
    // TODO Auto-generated method stub
  }
}
