package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

public class TypeBody {
  public final List<Method> methods;

  public TypeBody(final List<Method> methods) {
    this.methods = Collections.unmodifiableList(methods);
  }

  public List<Method> methods() {
    return methods;
  }
}
