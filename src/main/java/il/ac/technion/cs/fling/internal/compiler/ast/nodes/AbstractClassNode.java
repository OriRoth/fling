package il.ac.technion.cs.fling.internal.compiler.ast.nodes;


import java.util.List;

import il.ac.technion.cs.fling.Variable;

public class AbstractClassNode extends ClassNode {
  public final List<AbstractClassNode> parents;
  public final List<ClassNode> children;

  public AbstractClassNode(final Variable source, final List<AbstractClassNode> parents, final List<ClassNode> children) {
    super(source);
    this.parents = parents;
    this.children = children;
  }
}
