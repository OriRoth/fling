package il.ac.technion.cs.fling.internal.compiler.ast.nodes;
import java.util.Collection;
public class ASTCompilationUnitNode {
  public final Collection<ClassNode> classes;
  /*
   * It's almost always a mistake to add a boolean parameter to a public method
   * (part of an API) if that method is not a setter. When reading code using such
   * a method, it can be difficult to decipher what the boolean stands for without
   * looking at the source or documentation. This problem is also known as the
   * boolean trap. The boolean parameter can often be profitably replaced with an
   * enum
   */
  public ASTCompilationUnitNode(final Collection<ClassNode> classes) {
    this.classes = classes;
  }
}
