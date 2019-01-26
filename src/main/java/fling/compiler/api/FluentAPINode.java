package fling.compiler.api;

import java.util.List;

public class FluentAPINode<T, D, N> {
  public final List<AbstractMethodNode<T, D>> startMethods;
  public final List<InterfaceNode<T, D, N>> interfaces;

  public FluentAPINode(List<AbstractMethodNode<T, D>> startMethods, List<InterfaceNode<T, D, N>> interfaces) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
  }
}
