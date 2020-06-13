package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

public class TypeBody<T, D> {
  public final List<AbstractMethod> methods;

  public TypeBody(final List<AbstractMethod> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }

  public List<AbstractMethod> methods() {
    return methods;
  }
}
