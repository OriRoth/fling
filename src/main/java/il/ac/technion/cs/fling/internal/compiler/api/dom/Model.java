package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;
import java.util.stream.Stream;

public class Model {
  private final List<Method.Start> starts;
  private final List<Type> types;

  public Stream<Type> types() {
    return types.stream();
  }

  public final TypeBody body;

  public Model(final List<Method.Start> startMethods, final List<Type> types, final TypeBody body) {
    this.starts = startMethods;
    this.types = types;
    this.body = body;
  }

  public Stream<Method.Start> startMethods() {
    return starts.stream();
  }
}
