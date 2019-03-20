package fling.compiler.ast.nodes;

import java.util.List;

import fling.grammar.sententials.Variable;

public class ConcreteClassNode implements ClassNode {
  public final Variable source;
  public final List<Variable> parents;
  public final List<FieldNode> fields;

  public ConcreteClassNode(Variable source, List<Variable> parents, List<FieldNode> fields) {
    this.source = source;
    this.parents = parents;
    this.fields = fields;
  }
}
