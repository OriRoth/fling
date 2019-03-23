package fling.compiler.api.nodes;

import java.util.Collections;
import java.util.List;

public class ConcreteImplementationNode<T, D> {
  public final List<AbstractMethodNode<T, D>> methods;

  public ConcreteImplementationNode(List<AbstractMethodNode<T, D>> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }
  public List<AbstractMethodNode<T, D>> methods() {
    return methods;
  }
}
