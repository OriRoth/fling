package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

public class TypeBody<T, D> {
  public final List<AbstractMethod<T, D>> methods;

  public TypeBody(final List<AbstractMethod<T, D>> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }

  public List<AbstractMethod<T, D>> methods() {
    return methods;
  }
}
