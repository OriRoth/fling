package il.ac.technion.cs.fling.internal.compiler.ast.nodes;
import java.util.Collection;
public class ASTCompilationUnitNode {
  public final Collection<ClassNode> classes;
  private final boolean requireMultipleInheritance;
  public ASTCompilationUnitNode(final Collection<ClassNode> classes, final boolean requireMultipleInheritance) {
    this.classes = classes;
    this.requireMultipleInheritance = requireMultipleInheritance;
  }
}
