package fling.compiler.ast.nodes;

import java.util.List;

public class ASTCompilationUnitNode {
  public final List<ClassNode> classes;
  public final boolean requireMultipleInheritance;

  public ASTCompilationUnitNode(List<ClassNode> classes, boolean requireMultipleInheritance) {
    this.classes = classes;
    this.requireMultipleInheritance = requireMultipleInheritance;
  }
}
