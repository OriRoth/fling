package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;
import java.util.stream.Stream;

/** A model of the set of the definitions used for a a fluent API, including
 * types, start methods, and additional data
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public class Model {
  public final List<Method.Start> starts;
  public final List<Type> types;
  public final List<Method.Chained> methods;

  public Stream<Type> types() {
    return types.stream();
  }

  public Model(final List<Method.Start> starts, final List<Type> types, final List<Method.Chained> methods) {
    this.starts = starts;
    this.types = types;
    this.methods = methods;
  }

  public Stream<Method.Start> starts() {
    return starts.stream();
  }

  public Stream<Method.Chained> methods() {
    return methods.stream();
  }
}
