package il.ac.technion.cs.fling.adapters._;
import java.util.List;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.*;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth */
public class JavaGenerator extends CLikeGenerator {
  private final String className;
  private final String packageName;
  public JavaGenerator(final String packageName, final String className) {
    super(new Linker(packageName, className));
    this.packageName = packageName;
    this.className = className;
    bottomName("ø");
    topName("$");
    endName("$");
  }
  @Override public void visit(final Model m) {
    if (packageName != null)
      linef("package %s;", packageName);
    ____();
    line("import java.util.*;");
    ____();
    linef("@SuppressWarnings(\"all\") public interface %s { ", className).indent();
    commentf("%d start methods", m.starts().count());
    m.starts().forEach(this::visitStartMethod);
    ____();
    commentf("%d interface types", m.types().count());
    m.types().forEach(this::visit);
    ____();
    commentf("Class implementing %d interfaces with %d methods", m.types().count(), m.methods().count());
    implementingClass(m);
    unindent().line("}");
  }
  @Override void visit(final Type t) {
    line(fullName(t) + " {").indent();
    t.methods().forEach(this::visit);
    unindent().line("}");
  }
  @Override String fullName(final Type t) {
    var $ = String.format("interface %s", render(t.name));
    if (!t.parameters.isEmpty())
      $ += String.format(" <%s>", t.parameters().map(Named::name).collect(commas()));
    if (t.isAccepting)
      $ += " extends " + topName();
    return $;
  }
  private void visitStartMethod(final Method m) {
    linef("public static %s { ", fullMethodSignature(m)).indent();
    linef("α $ = new α();");
    linef("$.%s;", methodInvocation(m));
    linef("return $;");
    unindent().line("}");
  }
  @Override void visit(final Method m) {
    linef("public %s;", fullMethodSignature(m));
  }
  private void implementingClass(final Model m) {
    linef("static class α implements %s {", m.types().map(Type::name).map(this::render).collect(commas())).indent();
    implementingClassClassBody();
    lines(m.methods().map(mm -> //
    String.format("public %s {\n%s\n return this; }", //
        fullMethodSignature(mm), //
        implementingClassMethodBody(mm.name, mm.parameters()))));
    linef("public %s %s(){%s}", printTerminationMethodReturnType(), endName(), printTerminationMethodConcreteBody());
    unindent().line("}");
  }
  private String implementingClassClassBody() {
    return "";
  }
  private String printTopInterfaceBody() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName());
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
            .collect(commas()), //
        printStartMethodBody(m.name, m.parameters()));
  }
  /** Additional declaration within the top class.
   *
   * @return additional declarations */
  protected String printAdditionalDeclarations() {
    return "";
  }
  /** Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ          current token
   * @param parameters method parameters
   * @return method body */
  private String implementingClassMethodBody(final Token σ, final Stream<MethodParameter> parameters) {
    return "";
  }
  /** Start static method body.
   *
   * @param σ          inducing token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printStartMethodBody(final Token σ,
      final Stream<MethodParameter> parameters) {
    return "return new α();";
  }
  /** Concrete implementation's termination method body. Might be used to create
   * and return the processed terminal.
   *
   * @return method body */
  String printTerminationMethodConcreteBody() {
    return "";
  }
  /** Return type of the termination method.
   *
   * @return return type */
  String printTerminationMethodReturnType() {
    return "void";
  }
  protected String printConcreteImplementationMethodBody(final Token σ, final List<MethodParameter> parameters) {
    // TODO Auto-generated method stub
    return null;
  }
  protected String printStartMethodBody(final Token σ, final List<MethodParameter> parameters) {
    // TODO Auto-generated method stub
    return null;
  }
}
