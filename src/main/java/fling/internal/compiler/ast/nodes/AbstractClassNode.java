package fling.internal.compiler.ast.nodes;


import java.util.*;

import fling.Variable;

public class AbstractClassNode extends ClassNode {
  public final List<AbstractClassNode> parents;
  public final List<ClassNode> children;

  public AbstractClassNode(final Variable source, final List<AbstractClassNode> parents, final List<ClassNode> children) {
    super(source);
    this.parents = parents;
    this.children = children;
  }
}
