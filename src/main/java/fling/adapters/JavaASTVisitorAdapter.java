package fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fling.compiler.Namer;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.notations.JavaCompatibleNotation;
import fling.namers.NaiveNamer;

/**
 * Java adapter printing AST visitor class given AST type definitions.
 * 
 * @author Ori Roth
 */
@SuppressWarnings("static-method") public class JavaASTVisitorAdapter {
  private static final String VISITOR_CLASS_NAME = "Visitor";
  private final String packageName;
  private final String astClassName;
  private final Namer namer;

  public JavaASTVisitorAdapter(String packageName, String astClassName, Namer namer) {
    this.packageName = packageName;
    this.astClassName = astClassName;
    this.namer = namer;
  }
  public String printASTVisitorClass(ASTCompilationUnitNode compilationUnit) {
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
  public String printVisitMethod(ClassNode clazz) {
    return clazz.isAbstract() ? //
        printVisitMethod(clazz.asAbstract()) : //
        printVisitMethod(clazz.asConcrete());
  }
  public String printVisitMethod(AbstractClassNode clazz) {
    Variable source = clazz.source;
    String parameterName = getNodeParameterName(source);
    return String.format("public final void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(clazz, parameterName));
  }
  public String printVisitMethod(ConcreteClassNode clazz) {
    Variable source = clazz.source;
    String parameterName = getNodeParameterName(source);
    return String.format("public final void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(clazz, parameterName));
  }
  public String printWhileVisitingMethod(ConcreteClassNode clazz) {
    Variable source = clazz.source;
    String parameterName = getNodeParameterName(source);
    return String.format("public void whileVisiting(%s %s)throws %s{}", //
        getASTVariableClassName(source), //
        parameterName, //
        Exception.class.getCanonicalName());
  }
  private String printVisitMethodBody(AbstractClassNode clazz, String parameterName) {
    return clazz.children.stream() //
        .map(child -> String.format("if(%s instanceof %s)%s;", //
            parameterName, //
            getASTVariableClassName(child.source), //
            variableVisitingMethod(child.source, parameterName))) //
        .collect(joining("else "));
  }
  private String printVisitMethodBody(ConcreteClassNode clazz, String parameterName) {
    StringBuilder $ = new StringBuilder();
    Map<String, Integer> usedNames = new LinkedHashMap<>();
    $.append(String.format("try{this.whileVisiting(%s);}catch(%s __){__.printStackTrace();}", //
        parameterName, //
        Exception.class.getCanonicalName()));
    clazz.fields.stream() //
        .map(FieldNode::source) //
        .forEach(source -> {
          assert !source.isNotation()
              || source.getClass().isAnnotationPresent(JavaCompatibleNotation.class) : "BNF uses a non-Java-compatible notation";
        });
    clazz.fields.stream() //
        .map(FieldNode::getInferredFieldFragments) //
        .flatMap(List::stream) //
        .map(field -> field.visitingMethod(//
            this::variableVisitingMethod, //
            String.format("%s.%s", //
                parameterName, //
                field.parameterName), //
            () -> NaiveNamer.getNameFromBase("_x_", usedNames)))
        .filter(Objects::nonNull) //
        .map(method -> method + ";") //
        .forEach($::append);
    return $.toString();
  }
  private String getASTVariableClassName(Variable variable) {
    return String.format("%s.%s.%s", //
        packageName, //
        astClassName, //
        namer.getASTClassName(variable));
  }
  private String variableVisitingMethod(Variable variable, String access) {
    return String.format("visit((%s)%s)", //
        getASTVariableClassName(variable), //
        access);
  }
  private String getNodeParameterName(Variable variable) {
    return NaiveNamer.lowerCamelCase(variable.name());
  }
}
