package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;

public class CompilationUnit {
  public final List<Method> startMethods;
  public final List<Interface> interfaces;
  public final TypeBody body;

  public CompilationUnit(final List<Method> startMethods, final List<Interface> interfaces, final TypeBody body) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
    this.body = body;
  }
}
