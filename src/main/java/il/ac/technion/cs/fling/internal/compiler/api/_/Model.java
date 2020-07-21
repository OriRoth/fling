package il.ac.technion.cs.fling.internal.compiler.api._;
import java.util.List;
import java.util.stream.Stream;
/** A model of the set of the definitions used for a a fluent API, including
 * types, start methods, and additional data
 *
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public class Model {
  public final List<Method> starts;
  public final List<Type> types;
  public Stream<Type> types() {
    return types.stream();
  }
  public Model(final List<Method> starts, final List<Type> types) {
    this.starts = starts;
    this.types = types;
  }
  public Stream<Method> starts() {
    return starts.stream();
  }
  public Stream<Method> methods() {
    return types().flatMap(Type::methods).distinct();
  }
}
