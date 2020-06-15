package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TypeBody {
  private final List<Method> methods;

  public TypeBody(final List<Method> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }

  public Stream<Method> methods() {
    return methods.stream();
  }
}
