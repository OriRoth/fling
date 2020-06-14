package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.adapters.APIGenerator;

/** A representation of a type, which may, or may not take parameters.
 *
 * @author yogi */
public interface Type {
  String render(APIGenerator g);

  static Monomorphic of(final TypeName n) {
    return new Monomorphic(n);
  }

  default Stream<Type> arguments() {
    return Stream.empty();
  }

  Type TOP = g -> g.topName;

  Type BOTTOM = g -> g.bottomName;

  class Monomorphic implements Type {
    Monomorphic(final TypeName name) {
      this.name = name;
    }

    protected final TypeName name;

    @Override public String render(final APIGenerator g) {
      return g.renderTypeMonomorphic(name);
    }

    public Polymorphic with(final List<Type> arguments) {
      return new Polymorphic(name, arguments);
    }

  }

  class Polymorphic extends Monomorphic {
    @Override public String render(final APIGenerator g) {
      return g.renderTypePolymorphic(name, arguments);
    }

    @Override public Stream<Type> arguments() {
      return arguments.stream();
    }

    private final List<Type> arguments;

    Polymorphic(final TypeName name, final List<Type> arguments) {
      super(name);
      this.arguments = Collections.unmodifiableList(arguments);
    }
  }

}
