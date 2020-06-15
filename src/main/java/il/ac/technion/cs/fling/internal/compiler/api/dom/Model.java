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
  private final List<Method.Start> starts;
  private final List<Type> types;

  public Stream<Type> types() {
    return types.stream();
  }

  public final TypeBody body;

  public Model(final List<Method.Start> starts, final List<Type> types, final TypeBody body) {
    this.starts = starts;
    this.types = types;
    this.body = body;
  }

  public Stream<Method.Start> starts() {
    return starts.stream();
  }
}
