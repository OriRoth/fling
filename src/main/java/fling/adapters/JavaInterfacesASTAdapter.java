package fling.adapters;

import static java.util.stream.Collectors.joining;

import fling.compiler.Namer;
import fling.compiler.ast.PolymorphicLanguageASTAdapterBase;
import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.compiler.ast.nodes.FieldNode;

/**
 * Java AST adapter. Abstract types translate to interfaces, while Concrete
 * types translate to classes implementing them.
 * 
 * @author Ori Roth
 */
@SuppressWarnings("static-method") public class JavaInterfacesASTAdapter implements PolymorphicLanguageASTAdapterBase {
  private final String packageName;
  private final String className;
  private final Namer namer;

  public JavaInterfacesASTAdapter(String packageName, String className, Namer namer) {
    this.packageName = packageName;
    this.className = className;
    this.namer = namer;
  }
  @Override public String printASTClass(ASTCompilationUnitNode compilationUnit) {
    namer.name(compilationUnit);
    return String.format("package %s;@SuppressWarnings(\"all\")public interface %s{%s%s}", //
        packageName, //
        className, //
        compilationUnit.classes.stream() //
            .map(this::printClass) //
            .collect(joining()), //
        printAdditionalDeclarations(compilationUnit));
  }
  @Override public String printAbstractClass(AbstractClassNode abstractClass) {
    return String.format("interface %s %s{}", //
        abstractClass.getClassName(), //
        abstractClass.parents.isEmpty() ? "" : //
            "extends " + abstractClass.parents.stream() //
                .map(ClassNode::getClassName) //
                .collect(joining(",")));
  }
  @Override public String printConcreteClass(ConcreteClassNode concreteClass) {
    return String.format("class %s %s%s{%s%s}", //
        concreteClass.getClassName(), //
        concreteClass.parents.isEmpty() ? "" : "implements ", //
        concreteClass.parents.stream().map(ClassNode::getClassName).collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("public final %s %s;", "", field)) //
            .collect(joining()), //
        printConstructor(concreteClass));
  }
  /**
   * Additional definitions to be printed in the top class.
   * 
   * @param compilationUnit AST
   * @return additional definitions
   */
  @SuppressWarnings("unused") protected String printAdditionalDeclarations(ASTCompilationUnitNode compilationUnit) {
    return "";
  }
  public String printField(String format, String separator, FieldNode field) {
    return field.getInferredFieldFragments().stream() //
        .map(fragment -> String.format(format, //
            fragment.parameterType, fragment.parameterName)) //
        .collect(joining(separator));
  }
  public String constructorAssignment(FieldNode field) {
    return field.getInferredFieldFragments().stream() //
        .map(fragment -> String.format("this.%s = %s;", //
            fragment.parameterName, fragment.parameterName)) //
        .collect(joining());
  }
  public String printConstructor(ConcreteClassNode concreteClass) {
    return String.format("public %s(%s){%s}", //
        concreteClass.getClassName(), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("%s %s", ",", field)) //
            .collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> constructorAssignment(field)) //
            .collect(joining()));
  }
  public boolean nonEmptyField(FieldNode field) {
    return !field.source.isVerb() || !field.source.asVerb().parameters.isEmpty();
  }
}
