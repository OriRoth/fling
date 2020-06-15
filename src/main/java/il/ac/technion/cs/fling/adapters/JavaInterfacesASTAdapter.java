package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.PolymorphicLanguageASTAdapterBase;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.AbstractClassNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ClassNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ConcreteClassNode;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode;

/** Java AST adapter. Abstract types translate to interfaces, while Concrete
 * types translate to classes implementing them.
 *
 * @author Ori Roth */
public class JavaInterfacesASTAdapter implements PolymorphicLanguageASTAdapterBase {
  private final String className;
  private final Namer namer;
  private final String packageName;

  public JavaInterfacesASTAdapter(final String packageName, final String className, final Namer namer) {
    this.packageName = packageName;
    this.className = className;
    this.namer = namer;
  }

  public String constructorAssignment(final FieldNode field) {
    return field.getInferredFieldFragments().stream() //
        .map(fragment -> String.format("this.%s = %s;", //
            fragment.parameterName, fragment.parameterName)) //
        .collect(joining());
  }

  public boolean nonEmptyField(final FieldNode field) {
    return !field.source.isToken() || field.source.isParameterized();
  }

  @Override public String printAbstractClass(final AbstractClassNode abstractClass) {
    return String.format("interface %s %s{}", //
        abstractClass.getClassName(), //
        abstractClass.parents.isEmpty() ? "" : //
            "extends " + abstractClass.parents.stream() //
                .map(ClassNode::getClassName) //
                .collect(joining(",")));
  }

  @Override public String printASTClass(final ASTCompilationUnitNode compilationUnit) {
    namer.name(compilationUnit);
    return String.format("package %s;\n import java.util.*;\n @SuppressWarnings(\"all\")public interface %s{%s%s}", //
        packageName, //
        className, //
        compilationUnit.classes.stream() //
            .map(this::printClass) //
            .collect(joining()), //
        printAdditionalDeclarations(compilationUnit));
  }

  @Override public String printConcreteClass(final ConcreteClassNode concreteClass) {
    return String.format("public class %s %s%s{%s%s}", //
        concreteClass.getClassName(), //
        concreteClass.parents.isEmpty() ? "" : "implements ", //
        concreteClass.parents.stream().map(ClassNode::getClassName).collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("public final %s %s;", "", field)) //
            .collect(joining()), //
        printConstructor(concreteClass));
  }

  public String printConstructor(final ConcreteClassNode concreteClass) {
    return String.format("public %s(%s){%s}", //
        concreteClass.getClassName(), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(field -> printField("%s %s", ",", field)) //
            .collect(joining(",")), //
        concreteClass.fields.stream() //
            .filter(this::nonEmptyField) //
            .map(this::constructorAssignment) //
            .collect(joining()));
  }

  public String printField(final String format, final String separator, final FieldNode field) {
    return field.getInferredFieldFragments().stream() //
        .map(fragment -> String.format(format, //
            fragment.parameterType, fragment.parameterName)) //
        .collect(joining(separator));
  }

  /** Additional definitions to be printed in the top class.
   *
   * @param compilationUnit AST
   * @return additional definitions */
  @SuppressWarnings("unused") protected String printAdditionalDeclarations(
      final ASTCompilationUnitNode compilationUnit) {
    return "";
  }
}
