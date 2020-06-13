package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;

import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;

public class CompilationUnit {
  public final List<Method> startMethods;
  public final List<Interface> interfaces;
  public final TypeBody<TypeName, MethodDeclaration> concreteImplementation;

  public CompilationUnit(final List<Method> startMethods, final List<Interface> interfaces,
      final TypeBody<TypeName, MethodDeclaration> concreteImplementation) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
    this.concreteImplementation = concreteImplementation;
  }
}
