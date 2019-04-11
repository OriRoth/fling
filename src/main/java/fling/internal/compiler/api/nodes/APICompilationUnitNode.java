package fling.internal.compiler.api.nodes;

import java.util.List;

public class APICompilationUnitNode<T, D, N> {
  public final List<AbstractMethodNode<T, D>> startMethods;
  public final List<InterfaceNode<T, D, N>> interfaces;
  public final ConcreteImplementationNode<T, D> concreteImplementation;

  public APICompilationUnitNode(List<AbstractMethodNode<T, D>> startMethods, List<InterfaceNode<T, D, N>> interfaces,
      ConcreteImplementationNode<T, D> concreteImplementation) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
    this.concreteImplementation = concreteImplementation;
  }
}
