package il.ac.technion.cs.fling.adapters;
import java.util.*;
import static java.util.stream.Collectors.joining;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
/** Java adapter printing AST visitor class given AST type definitions.
 *
 * @author Ori Roth */
@SuppressWarnings("static-method") public class JavaASTVisitorAdapter {
  private final String astClassName;
  private final Linker namer;
  private final String packageName;
  public JavaASTVisitorAdapter(final String packageName, final String astClassName, final Linker namer) {
    this.packageName = packageName;
    this.astClassName = astClassName;
    this.namer = namer;
  }
  public String printASTVisitorClass(final ASTCompilationUnitNode compilationUnit) {
    return String.format("public static class %s{%s%s}", //
        VISITOR_CLASS_NAME, //
        compilationUnit.classes.stream() //
            .map(this::printVisitMethod) //
            .collect(joining()), //
        compilationUnit.classes.stream() //
            .filter(ClassNode::isConcrete) //
            .map(ClassNode::asConcrete) //
            .map(this::printWhileVisitingMethod) //
            .collect(joining()));
  }
  public String printVisitMethod(final AbstractClassNode clazz) {
    final Variable source = clazz.source;
    final String parameterName = getNodeParameterName(source);
    return String.format("public final void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(clazz, parameterName));
  }
  public String printVisitMethod(final ClassNode clazz) {
    return clazz.isAbstract() ? //
        printVisitMethod(clazz.asAbstract()) : //
        printVisitMethod(clazz.asConcrete());
  }
  public String printVisitMethod(final ConcreteClassNode c) {
    final Variable source = c.source;
    final String parameterName = getNodeParameterName(source);
    return String.format("public final void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(c, parameterName));
  }
  public String printWhileVisitingMethod(final ConcreteClassNode n) {
    final Variable source = n.source;
    final String parameterName = getNodeParameterName(source);
    return String.format("public void whileVisiting(%s %s)throws %s{}", //
        getASTVariableClassName(source), //
        parameterName, //
        Exception.class.getCanonicalName());
  }
  private String getASTVariableClassName(final Variable variable) {
    return String.format("%s.%s.%s", //
        packageName, //
        astClassName, //
        namer.getASTClassName(variable));
  }
  private String getNodeParameterName(final Variable variable) {
    return Linker.lowerCamelCase(variable.name());
  }
  private String printVisitMethodBody(final AbstractClassNode clazz, final String parameterName) {
    return clazz.children.stream() //
        .map(child -> String.format("if(%s instanceof %s)%s", //
            parameterName, //
            getASTVariableClassName(child.source), //
            variableVisitingStatement(child.source, parameterName))) //
        .collect(joining("else "));
  }
  private String printVisitMethodBody(final ConcreteClassNode clazz, final String parameterName) {
    final StringBuilder $ = new StringBuilder();
    final Map<String, Integer> usedNames = new LinkedHashMap<>();
    $.append(String.format("try{this.whileVisiting(%s);}catch(%s __){__.printStackTrace();}", //
        parameterName, //
        Exception.class.getCanonicalName()));
    clazz.fields.stream() //
        .map(FieldNode::source) //
        .forEach(source -> {
          assert !source.isQuantifier() || source.getClass()
              .isAnnotationPresent(JavaCompatibleQuantifier.class) : "BNF uses a non-Java-compatible notation";
        });
    clazz.fields.stream() //
        .map(FieldNode::getInferredFieldFragments) //
        .flatMap(List::stream) //
        .map(field -> field.visitingStatement(//
            this::variableVisitingStatement, //
            String.format("%s.%s", //
                parameterName, //
                field.parameterName), //
            () -> Linker.getNameFromBase("_x_", usedNames)))
        .filter(Objects::nonNull) //
        .forEach($::append);
    return $.toString();
  }
  private String variableVisitingStatement(final Variable variable, final String access) {
    return String.format("{visit((%s)%s);}", //
        getASTVariableClassName(variable), //
        access);
  }
  private static final String VISITOR_CLASS_NAME = "Visitor";
}
