package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;

public class CompilationUnit<T, D, N> {
  public final List<AbstractMethod<T, D>> startMethods;
  public final List<Interfac<T, D, N>> interfaces;
  public final TypeBody<T, D> concreteImplementation;

  public CompilationUnit(final List<AbstractMethod<T, D>> startMethods,
      final List<Interfac<T, D, N>> interfaces, final TypeBody<T, D> concreteImplementation) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
    this.concreteImplementation = concreteImplementation;
  }
}
