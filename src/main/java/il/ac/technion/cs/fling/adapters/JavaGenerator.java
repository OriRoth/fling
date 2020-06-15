package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method.Chained;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeSignature;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth */
public class JavaGenerator extends APIGenerator {
  private final String className;
  private final String packageName;

  public JavaGenerator(final Namer namer, final String packageName, final String className, final String endName) {
    super(namer, endName, "ø", "$");
    this.packageName = packageName;
    this.className = className;
  }

  public String printConcreteImplementation(final Model m) {
    return String.format("static class α implements %s{%s%s%s}", //
        m.types().map(this::printTypeName).collect(joining(",")), //
        printConcreteImplementationClassBody(), m.methods() //
            .map(Method::asChainedMethod) //
            .map(Chained::signature) //
            .map(declaration -> String.format("public α %s(%s){%sreturn this;}", //
                declaration.name.name(), //
                declaration.parmeters() //
                    .map(parameter -> String.format("%s %s", //
                        parameter.parameterType, //
                        parameter.parameterName)) //
                    .collect(joining(",")), //
                printConcreteImplementationMethodBody(declaration.name, declaration.getInferredParameters()))) //
            .collect(joining()),
        String.format("public %s %s(){%s}", //
            printTerminationMethodReturnType(), //
            endName, //
            printTerminationMethodConcreteBody()));
  }

  public String printTopInterfaceBody() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName);
  }

  public String printTypeName(final Type i) {
    return i.isTop() ? "$" : i.isBot() ? "ø" : printTypeName(i.signature);
  }

  public String render1(TypeSignature s) {
    return s.α == null ? s.q.name()
        : String.format("%s_%s%s", //
            s.q.name(), //
            render(s.α), //
            s.legalJumps == null ? "" : "_" + s.legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  public String render2(final TypeSignature s) {
    return String.format("%s<%s>", s.α == null ? s.q.name()
        : String.format("%s_%s%s", //
            s.q.name(), //
            render(s.α), //
            s.legalJumps == null ? "" : "_" + s.legalJumps.stream().map(Named::name).collect(Collectors.joining())), //
        s.parameters().map(Named::name).collect(Collectors.joining(",")));
  }

  private String render(Word<Named> α) {
    return α.stream().map(Named::name).collect(Collectors.joining());
  }

  @Override public String render(final TypeSignature s) {
    return String.format("%s<%s>", printTypeName(s), //
        s.parameters().map(Named::name).collect(Collectors.joining(",")));
  }

  String printTypeName(final TypeSignature s) {
    return render(s.q, s.α, s.legalJumps);
  }

  @Override public String render(final MethodSignature s, final SkeletonType returnType) {
    return String.format("%s %s(%s);", //
        render(returnType), //
        s.name.name(), //
        s.parmeters() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")));
  }

  @Override String render(final Model m) {
    return String.format("%s@SuppressWarnings(\"all\")public interface %s{%s%s%s%s}", //
        packageName == null ? "" : String.format("package %s;\nimport java.util.*;\n\n\n", packageName), //
        className, //
        m.starts().map(this::render).collect(joining()), //
        m.types().map(this::render).collect(joining()), //
        printConcreteImplementation(m), //
        printAdditionalDeclarations());
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

  @Override public String render(final TypeSignature s, final List<Method> methods) {
    return String.format("interface %s%s{%s}", //
        render(s), //
        !s.isAccepting ? "" : " extends " + topName, //
        methods.stream() //
            .filter(method -> !method.isTerminationMethod()) //
            .map(this::render) //
            .collect(joining()));
  }

  @Override public String renderInterfaceBottom() {
    return "interface ø {}";
  }

  @Override public String renderInterfaceTop() {
    return String.format("interface ${%s}", printTopInterfaceBody());
  }

  @Override public String renderMethod(final MethodSignature s, final SkeletonType returnType) {
    return String.format("public static %s %s(%s) {%s}", //
        render(returnType), //
        Constants.$$.equals(s.name) ? "__" : s.name.name(), //
        s.parmeters() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        printStartMethodBody(s.name, s.getInferredParameters()));
  }

  @Override public String renderTerminationMethod() {
    return String.format("%s %s();", printTerminationMethodReturnType(), endName);
  }

  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
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
  protected String printConcreteImplementationClassBody() {
    return "";
  }

  /** Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ          current token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printConcreteImplementationMethodBody(final Token σ,
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
}
