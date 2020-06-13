package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;
import java.util.stream.Stream;

public class CompilationUnit {
  private final List<Method> startMethods;
  private final List<Interface> interfaces;

  public Stream<Interface> interfaces() {
    return interfaces.stream();
  }

  public final TypeBody body;

  public CompilationUnit(final List<Method> startMethods, final List<Interface> interfaces, final TypeBody body) {
    this.startMethods = startMethods;
    this.interfaces = interfaces;
    this.body = body;
  }

  public Stream<Method> startMethods() {
    return startMethods.stream();
  }
}
