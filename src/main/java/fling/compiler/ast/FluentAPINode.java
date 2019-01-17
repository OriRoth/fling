package fling.compiler.ast;

import java.util.List;

public class FluentAPINode<T, D, N> {
  public final List<MethodNode<T, D>> startMethods;
  public final List<InterfaceNode<T, D, N>> interfaces;

  public FluentAPINode(List<MethodNode<T, D>> startMethods, List<InterfaceNode<T, D, N>> interfaces) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
  }
}
