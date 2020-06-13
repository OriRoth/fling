package il.ac.technion.cs.fling.internal.compiler.api.dom;

import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;

public interface AbstractMethod {
  class Start implements AbstractMethod {
    public final MethodDeclaration declaration;
    public final Type returnType;

    public Start(final MethodDeclaration declaration, final Type returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public MethodDeclaration declaration() {
      return declaration;
    }
  }

  class Termination implements AbstractMethod {
  }

  class Intermediate implements AbstractMethod {
    public final MethodDeclaration declaration;
    public final Type returnType;

    public Intermediate(final MethodDeclaration declaration, final Type returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public MethodDeclaration declaration() {
      return declaration;
    }
  }

  class Chained implements AbstractMethod {
    public final MethodDeclaration declaration;

    public Chained(final MethodDeclaration declaration) {
      this.declaration = declaration;
    }

    public MethodDeclaration declaration() {
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

  default Start asStartMethod() {
    return (Start) this;
  }

  default Intermediate asIntermediateMethod() {
    return (Intermediate) this;
  }

  default Chained asChainedMethod() {
    return (Chained) this;
  }
}
