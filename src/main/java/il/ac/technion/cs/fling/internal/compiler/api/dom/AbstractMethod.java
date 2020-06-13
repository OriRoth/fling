package il.ac.technion.cs.fling.internal.compiler.api.dom;

public interface AbstractMethod<T, D> {
  class Start<T, D> implements AbstractMethod<T, D> {
    public final D declaration;
    public final PolymorphicType<T> returnType;

    public Start(final D declaration, final PolymorphicType<T> returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public D declaration() {
      return declaration;
    }
  }

  class Termination<T, D> implements AbstractMethod<T, D> {
  }

  class Intermediate<T, D> implements AbstractMethod<T, D> {
    public final D declaration;
    public final PolymorphicType<T> returnType;

    public Intermediate(final D declaration, final PolymorphicType<T> returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public D declaration() {
      return declaration;
    }
  }

  class Chained<T, D> implements AbstractMethod<T, D> {
    public final D declaration;

    public Chained(final D declaration) {
      this.declaration = declaration;
    }

    public D declaration() {
      return declaration;
    }
  }

  default boolean isStartMethod() {
    return this instanceof Start;
  }

  default boolean isTerminationMethod() {
    return this instanceof Termination;
  }

  default boolean isIntermediateMethod() {
    return this instanceof Intermediate;
  }

  default boolean isChainedMethod() {
    return this instanceof Chained;
  }

  default Start<T, D> asStartMethod() {
    return (Start<T, D>) this;
  }

  default Intermediate<T, D> asIntermediateMethod() {
    return (Intermediate<T, D>) this;
  }

  default Chained<?, D> asChainedMethod() {
    return (Chained<?, D>) this;
  }
}
