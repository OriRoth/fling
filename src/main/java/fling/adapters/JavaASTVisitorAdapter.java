package fling.adapters;

import static java.util.stream.Collectors.joining;

import fling.compiler.Namer;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.grammar.sententials.Variable;
import fling.namers.NaiveNamer;

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
    return String.format("public static class %s{%s}", //
        VISITOR_CLASS_NAME, //
        compilationUnit.classes.stream() //
            .map(this::printVisitMethod) //
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
    return String.format("public void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(clazz, parameterName));
  }
  public String printVisitMethod(ConcreteClassNode clazz) {
    Variable source = clazz.source;
    String parameterName = getNodeParameterName(source);
    return String.format("public void visit(%s %s){%s}", //
        getASTVariableClassName(source), //
        parameterName, //
        printVisitMethodBody(clazz, parameterName));
  }
  private String printVisitMethodBody(AbstractClassNode clazz, String parameterName) {
    return clazz.children.stream() //
        .map(child -> String.format("if(%s instanceof %s)visit((%s)%s);", //
            parameterName, //
            getASTVariableClassName(child.source), //
            getASTVariableClassName(child.source), //
            parameterName)) //
        .collect(joining());
  }
  @SuppressWarnings("unused") private String printVisitMethodBody(ConcreteClassNode clazz, String parameterName) {
    return "";
  }
  private String getASTVariableClassName(Variable variable) {
    return String.format("%s.%s.%s", //
        packageName, //
        astClassName, //
        namer.getASTClassName(variable));
  }
  private String getNodeParameterName(Variable variable) {
    return NaiveNamer.lowerCamelCase(variable.name());
  }
}
