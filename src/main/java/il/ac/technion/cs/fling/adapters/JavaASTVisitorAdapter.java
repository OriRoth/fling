package il.ac.technion.cs.fling.adapters;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.namers.NaiveNamer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * Java adapter printing AST visitor class given AST type definitions.
 *
 * @author Ori Roth
 */
@SuppressWarnings("static-method")
public class JavaASTVisitorAdapter {
  private final String astClassName;
  private final Namer namer;
  private final String packageName;

  public JavaASTVisitorAdapter(final String packageName, final String astClassName, final Namer namer) {
    this.packageName = packageName;
    this.astClassName = astClassName;
    this.namer = namer;
  }

  public String printASTVisitorClass(final ASTCompilationUnitNode compilationUnit) {
    return format("public interface %s{%s}", //
            VISITOR_CLASS_NAME, //
            compilationUnit.classes.stream() //
                    .map(this::printVisitMethod) //
                    .collect(joining()));
  }

  public String printVisitMethod(final AbstractClassNode clazz) {
    final Variable source = clazz.source;
    final String parameterName = getNodeParameterName(source);
    return format("default void visit(%s %s){%s}", //
            getASTVariableClassName(source), //
            parameterName, //
            printVisitMethodBody(clazz, parameterName));
  }

  public String printVisitMethod(final ClassNode clazz) {
    return clazz.isAbstract() ? //
            printVisitMethod(clazz.asAbstract()) : //
            printVisitMethod(clazz.asConcrete());
  }

  public String printVisitMethod(final ConcreteClassNode clazz) {
    final Variable source = clazz.source;
    final String parameterName = getNodeParameterName(source);
    return format("default void visit(%s %s){%s}", //
            getASTVariableClassName(source), //
            parameterName, //
            printVisitMethodBody(clazz, parameterName));
  }

  private String getASTVariableClassName(final Variable variable) {
    return format("%s.%s.%s", //
            packageName, //
            astClassName, //
            namer.getASTClassName(variable));
  }

  private String getNodeParameterName(final Variable variable) {
    return NaiveNamer.lowerCamelCase(variable.name());
  }

  private String printVisitMethodBody(final AbstractClassNode clazz, final String parameterName) {
    return clazz.children.stream() //
            .map(child -> format("if(%s instanceof %s)%s", //
                    parameterName, //
                    getASTVariableClassName(child.source), //
                    variableVisitingStatement(child.source, parameterName))) //
            .collect(joining("else "));
  }

  private String printVisitMethodBody(final ConcreteClassNode clazz, final String parameterName) {
    final StringBuilder $ = new StringBuilder();
    final Map<String, Integer> usedNames = new LinkedHashMap<>();
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
                    format("%s.%s", //
                            parameterName, //
                            field.parameterName), //
                    () -> NaiveNamer.getNameFromBase("_x_", usedNames)))
            .filter(Objects::nonNull) //
            .forEach($::append);
    return $.toString();
  }

  private String variableVisitingStatement(final Variable variable, final String access) {
    return format("{visit((%s)%s);}", //
            getASTVariableClassName(variable), //
            access);
  }

  private static final String VISITOR_CLASS_NAME = "Visitor";

}
