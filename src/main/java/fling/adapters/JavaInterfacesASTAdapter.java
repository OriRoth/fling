package fling.adapters;

import static java.util.stream.Collectors.joining;

import fling.compiler.Namer;
import fling.compiler.ast.PolymorphicLanguageASTAdapter;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.grammar.sententials.Symbol;

public class JavaInterfacesASTAdapter implements PolymorphicLanguageASTAdapter {
  private final String packageName;
  private final String className;
  private final Namer namer;

  public JavaInterfacesASTAdapter(String packageName, String className, Namer namer) {
    this.packageName = packageName;
    this.className = className;
    this.namer = namer;
  }
  @Override public String printASTClass(ASTCompilationUnitNode compilationUnit) {
    return String.format("package %s;@SuppressWarnings(\"all\")public interface %s{%s}", //
        packageName, //
        className, //
        compilationUnit.classes.stream().map(this::printClass).collect(joining()));
  }
  @Override public String printAbstractClass(AbstractClassNode abstractClass) {
    return String.format("interface %s %s{}", //
        namer.getClassName(abstractClass.source), //
        abstractClass.parents.isEmpty() ? "" : //
            "extends " + abstractClass.parents.stream().map(namer::getClassName).collect(joining(",")));
  }
  @Override public String printConcreteClass(ConcreteClassNode concreteClass) {
    return String.format("class %s implements %s{%s%s}", //
        namer.getClassName(concreteClass.source), //
        concreteClass.parents.stream().map(namer::getClassName).collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("public final %s %s;", "", field, concreteClass)) //
            .collect(joining()), //
        printConstructor(concreteClass));
  }
  public String printField(String format, String separator, FieldNode field, ClassNode container) {
    Symbol source = field.source;
    // TODO support more complex types.
    assert source.isTerminal() || source.isVariable();
    return source.isVariable() ? String.format(format, //
        namer.getClassName(source.asVariable()), //
        namer.getParameterName(source.asVariable(), field, container)) : //
        source.asTerminal().parameters().stream() //
            .map(parameter -> String.format(format, //
                namer.getParameterType(parameter), //
                namer.getParameterName(parameter, field, container))) //
            .collect(joining(separator));
  }
  public String constructorAssignment(FieldNode field, ClassNode container) {
    Symbol source = field.source;
    // TODO support more complex types.
    assert source.isTerminal() || source.isVariable();
    return source.isVariable() ? String.format("this.%s = %s;", //
        namer.getParameterName(source.asVariable(), field, container), //
        namer.getParameterName(source.asVariable(), field, container)) : //
        source.asTerminal().parameters().stream() //
            .map(parameter -> String.format("this.%s = %s;", //
                namer.getParameterType(parameter), //
                namer.getParameterName(parameter, field, container))) //
            .collect(joining());
  }
  public String printConstructor(ConcreteClassNode concreteClass) {
    return String.format("public %s(%s){%s}", //
        namer.getClassName(concreteClass.source), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("%s %s", ",", field, concreteClass)) //
            .collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> constructorAssignment(field, concreteClass)) //
            .collect(joining()));
  }
  public boolean nonEmptyField(FieldNode field) {
    return !field.source.isTerminal() || !field.source.asTerminal().parameters().isEmpty();
  }
}
