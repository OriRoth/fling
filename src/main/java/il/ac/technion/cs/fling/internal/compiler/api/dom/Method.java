package il.ac.technion.cs.fling.internal.compiler.api.dom;

import il.ac.technion.cs.fling.adapters.APIGenerator;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;

public interface Method {
  class Start implements Method {
    public final MethodDeclaration declaration;
    public final Type returnType;

    public Start(final MethodDeclaration declaration, final Type returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public MethodDeclaration declaration() {
      return declaration;
    }

    @Override public String render(final APIGenerator g) {
      return g.renderMethod(declaration, returnType);
    }
  }

  class Termination implements Method {
    @Override public String render(final APIGenerator g) {
      return g.renderTerminationMethod();
    }
  }

  class Intermediate implements Method {
    public final MethodDeclaration declaration;
    public final Type returnType;

    public Intermediate(final Token σ, final Type returnType) {
        declaration = new MethodDeclaration(σ);
      this.returnType = returnType;
    }

    public MethodDeclaration declaration() {
      return declaration;
    }

    @Override public String render(final APIGenerator g) {
      return g.printIntermediateMethod(declaration, returnType);
    }
  }

  class Chained implements Method {
    public final MethodDeclaration declaration;

    public Chained(final MethodDeclaration declaration) {
      this.declaration = declaration;
    }

    public MethodDeclaration declaration() {
      return declaration;
    }

    @Override public String render(final APIGenerator g) {
      return null;
    }
  }

  default boolean isStartMethod() {
    return this instanceof Start;
  }

  default boolean isTerminationMethod() {
    return this instanceof Termination;
  }

  default boolean isChainedMethod() {
    return this instanceof Chained;
  }

  default Chained asChainedMethod() {
    return (Chained) this;
  }

  String render(APIGenerator g);
}
