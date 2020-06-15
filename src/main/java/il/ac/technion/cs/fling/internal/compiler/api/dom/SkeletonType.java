package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.adapters.APIGenerator;

/** A representation of a type, which may, or may not take parameters.
 *
 * @author yogi */
public interface SkeletonType {
  String render(APIGenerator g);

  static Monomorphic of(final TypeName n) {
    return new Monomorphic(n);
  }

  default Stream<SkeletonType> arguments() {
    return Stream.empty();
  }

  SkeletonType TOP = g -> g.topName;

  SkeletonType BOTTOM = g -> g.bottomName;

  class Monomorphic implements SkeletonType {
    Monomorphic(final TypeName name) {
      this.name = name;
    }

    protected final TypeName name;

    @Override public String render(final APIGenerator g) {
      return g.render(name);
    }

    public Polymorphic with(final List<SkeletonType> arguments) {
      return new Polymorphic(name, arguments);
    }

  }

  class Polymorphic extends Monomorphic {
    @Override public String render(final APIGenerator g) {
      return g.render(name, arguments);
    }

    @Override public Stream<SkeletonType> arguments() {
      return arguments.stream();
    }

    private final List<SkeletonType> arguments;

    Polymorphic(final TypeName name, final List<SkeletonType> arguments) {
      super(name);
      this.arguments = Collections.unmodifiableList(arguments);
    }
  }

}
