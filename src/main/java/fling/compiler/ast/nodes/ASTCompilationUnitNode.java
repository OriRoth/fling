package fling.compiler.ast.nodes;

import java.util.Collection;

public class ASTCompilationUnitNode {
  public final Collection<ClassNode> classes;
  public final boolean requireMultipleInheritance;

  public ASTCompilationUnitNode(Collection<ClassNode> classes, boolean requireMultipleInheritance) {
    this.classes = classes;
    this.requireMultipleInheritance = requireMultipleInheritance;
  }
}
