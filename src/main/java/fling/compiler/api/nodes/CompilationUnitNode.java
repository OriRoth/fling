package fling.compiler.api.nodes;

import java.util.List;

public class CompilationUnitNode<T, D, N> {
  public final List<AbstractMethodNode<T, D>> startMethods;
  public final List<InterfaceNode<T, D, N>> interfaces;

  public CompilationUnitNode(List<AbstractMethodNode<T, D>> startMethods, List<InterfaceNode<T, D, N>> interfaces) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
  }
}
