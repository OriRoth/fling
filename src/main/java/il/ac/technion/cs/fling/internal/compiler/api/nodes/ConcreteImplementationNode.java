package il.ac.technion.cs.fling.internal.compiler.api.nodes;

import java.util.*;

public class ConcreteImplementationNode<T, D> {
  public final List<AbstractMethodNode<T, D>> methods;

  public ConcreteImplementationNode(final List<AbstractMethodNode<T, D>> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }
  public List<AbstractMethodNode<T, D>> methods() {
    return methods;
  }
}
