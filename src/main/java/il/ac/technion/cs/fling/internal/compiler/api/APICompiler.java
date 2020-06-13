package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.AbstractMethod;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeBody;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interfac;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolymorphicType;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Encodes deterministic pushdown automaton ({@link DPDA}) as type declarations
 * constituting proper fluent API of the automaton's language. The automaton is
 * compiled into an AST which can be translated to polymorphic, object-oriented
 * language.
 *
 * @author Ori Roth */
public abstract class APICompiler {
  /** Inducing automaton. */
  public final DPDA<Named, Token, Named> dpda;
  /** Compiled types. */
  protected final Map<TypeName, Interfac<TypeName, MethodDeclaration, InterfaceDeclaration>> types = new LinkedHashMap<>();
  /** Mapping of terminals to type variable nodes. */
  protected final Map<Named, PolymorphicType<TypeName>> typeVariables = new LinkedHashMap<>();

  public APICompiler(final DPDA<Named, Token, Named> dpda) {
    this.dpda = dpda;
    dpda.Q().forEach(q -> typeVariables.put(q, new PolymorphicType<>(new TypeName(q))));
  }

  /** Compile fluent API. The object's state after calling this method is
   * undefined.
   *
   * @return compiled API */
  public CompilationUnit<TypeName, MethodDeclaration, InterfaceDeclaration> compileFluentAPI() {
    return new CompilationUnit<>(compileStartMethods(), compileInterfaces(), complieConcreteImplementation());
  }

  /** Compile API concrete implementation.
   *
   * @return concrete implementation */
  protected abstract TypeBody<TypeName, MethodDeclaration> complieConcreteImplementation();

  /** Compile API static start methods.
   *
   * @return compiled methods */
  protected abstract List<AbstractMethod<TypeName, MethodDeclaration>> compileStartMethods();

  /** Compile API types.
   *
   * @return compiled types */
  protected abstract List<Interfac<TypeName, MethodDeclaration, InterfaceDeclaration>> compileInterfaces();

  /** Type name node declaration.
   *
   * @author Ori Roth */
  public static class TypeName {
    /** Inducing state. */
    public final Named q;
    /** Inducing stack symbols. */
    public final Word<Named> α;
    /** Referenced states (type variables). */
    public final Set<Named> legalJumps;

    public TypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
      this.q = q;
      this.α = α;
      this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
    }

    TypeName(final Named q) {
      this.q = q;
      α = null;
      legalJumps = null;
    }

    @Override public int hashCode() {
      int $ = 1;
      if (q != null)
        $ = $ * 31 + q.hashCode();
      if (α != null)
        $ = $ * 31 + α.hashCode();
      if (legalJumps != null)
        $ = $ * 31 + legalJumps.hashCode();
      return $;
    }

    @Override public boolean equals(final Object o) {
      if (this == o)
        return true;
      if (!(o instanceof APICompiler.TypeName))
        return false;
      final APICompiler.TypeName other = (TypeName) o;
      return Objects.equals(q, other.q) && //
          Objects.equals(α, other.α) && //
          Objects.equals(legalJumps, other.legalJumps);
    }

    @Override public String toString() {
      return String.format("<~%s,%s,%s~>", q, α, legalJumps);
    }
  }

  /** Method node declaration.
   *
   * @author Ori Roth */
  public static class MethodDeclaration {
    /** Inducing token. */
    public final Token name;
    /** Inferred token parameters. Pending computation. */
    private List<ParameterFragment> inferredParameters;

    public MethodDeclaration(final Token name) {
      this.name = name;
    }

    /** @return inferred parameters
     * @throws IllegalStateException whether the parameters have not been set */
    public List<ParameterFragment> getInferredParameters() {
      if (inferredParameters == null)
        throw new IllegalStateException("parameter types and names not decided");
      return inferredParameters;
    }

    /** Set token's inferred parameters.
     *
     * @param inferredParameters parameters */
    public void setInferredParameters(final List<ParameterFragment> inferredParameters) {
      this.inferredParameters = inferredParameters;
    }
  }

  /** Parameter declaration inferred from token. Single token may define multiple
   * parameters.
   *
   * @author Ori Roth */
  public static class ParameterFragment {
    /** Parameter type name. */
    public final String parameterType;
    /** Parameter variable name. */
    public final String parameterName;

    private ParameterFragment(final String parameterType, final String parameterName) {
      this.parameterType = parameterType;
      this.parameterName = parameterName;
    }

    public static ParameterFragment of(final String parameterType, final String parameterName) {
      return new ParameterFragment(parameterType, parameterName);
    }

    public String parameterType() {
      return parameterType();
    }

    public String parameterName() {
      return parameterName;
    }
  }

  /** Type node declaration.
   *
   * @author Ori Roth */
  public static class InterfaceDeclaration {
    /** Inducing state. */
    public final Named q;
    /** Inducing stack symbols. */
    public final Word<Named> α;
    /** Referenced states (type variables). */
    public final Set<Named> legalJumps;
    /** Referenced states (type variables). */
    // TODO remove duplicate field.
    public final Word<Named> typeVariables;
    public final boolean isAccepting;

    public InterfaceDeclaration(final Named q, final Word<Named> α, final Set<Named> legalJumps,
        final Word<Named> typeVariables, final boolean isAccepting) {
      this.q = q;
      this.α = α;
      this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
      this.typeVariables = typeVariables;
      this.isAccepting = isAccepting;
    }

    InterfaceDeclaration() {
      q = null;
      α = null;
      legalJumps = null;
      typeVariables = null;
      isAccepting = false;
    }
  }
}
