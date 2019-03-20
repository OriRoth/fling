package fling.compiler.ast.nodes;

import java.util.List;

import fling.grammar.sententials.Variable;

public class AbstractClassNode implements ClassNode {
  public final Variable source;
  public final List<Variable> parents;
  public final List<Variable> children;

  public AbstractClassNode(Variable source, List<Variable> parents, List<Variable> children) {
    this.source = source;
    this.parents = parents;
    this.children = children;
  }
}
