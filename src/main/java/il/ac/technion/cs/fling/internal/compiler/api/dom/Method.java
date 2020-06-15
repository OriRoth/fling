package il.ac.technion.cs.fling.internal.compiler.api.dom;

import il.ac.technion.cs.fling.adapters.APIGenerator;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;

/** Models methods in the fluent API model
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public interface Method {
  class Start implements Method {
    public final MethodSignature declaration;
    public final SkeletonType returnType;

    public Start(final MethodSignature declaration, final SkeletonType returnType) {
      this.declaration = declaration;
      this.returnType = returnType;
    }

    public MethodSignature declaration() {
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
    public final MethodSignature declaration;
    public final SkeletonType returnType;

    public Intermediate(final Token σ, final SkeletonType returnType) {
      declaration = new MethodSignature(σ);
      this.returnType = returnType;
    }

    public MethodSignature declaration() {
      return declaration;
    }

    @Override public String render(final APIGenerator g) {
      return g.render(declaration, returnType);
    }
  }

  class Chained implements Method {
    public final MethodSignature signature;

    public Chained(final MethodSignature signature) {
      this.signature = signature;
    }

    public MethodSignature signature() {
      return signature;
    }

    @Override public String render(final APIGenerator g) {
      throw new RuntimeException("Unimplemented yet" + g);
    }
  }

  default boolean isTerminationMethod() {
    return this instanceof Termination;
  }

  default Chained asChainedMethod() {
    return (Chained) this;
  }

  String render(APIGenerator g);
}
