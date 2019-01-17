package fling.compiler.ast;

import java.util.Collections;
import java.util.List;

public class InterfaceNode<T, D, N> {
  public final N name;
  public final List<MethodNode<T, D>> methods;

  public InterfaceNode(N name, List<MethodNode<T, D>> methods) {
    this.name = name;
    this.methods = Collections.unmodifiableList(methods);
  }
}
