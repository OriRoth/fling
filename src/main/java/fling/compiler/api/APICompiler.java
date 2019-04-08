package fling.compiler.api;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fling.automata.DPDA;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.ConcreteImplementationNode;
import fling.compiler.api.nodes.InterfaceNode;
import fling.compiler.api.nodes.PolymorphicTypeNode;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Verb;
import fling.grammar.sententials.Word;

/**
 * Encodes deterministic pushdown automaton ({@link DPDA}) as a Java class; A
 * method calls chain of the form {@code START().a().b()...z().ACCEPT()}
 * type-checks against this class if, and only if, the original automaton
 * accepts the input word {@code ab...z}; A chain of a rejected input word
 * either do not type-check, or terminate with {@code STUCK()} call; Otherwise
 * the chain may terminate its computation by calling {@code TERMINATED()}.
 *
 * @author Ori Roth
 */
public abstract class APICompiler {
  public final DPDA<Named, Verb, Named> dpda;
  protected final Map<TypeName, InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> types;
  protected final Map<Named, PolymorphicTypeNode<TypeName>> typeVariables = new LinkedHashMap<>();

  public APICompiler(DPDA<Named, Verb, Named> dpda) {
    this.dpda = dpda;
    this.types = new LinkedHashMap<>();
    dpda.Q().forEach(q -> typeVariables.put(q, new PolymorphicTypeNode<>(new TypeName(q))));
  }
  public APICompilationUnitNode<TypeName, MethodDeclaration, InterfaceDeclaration> compileFluentAPI() {
    return new APICompilationUnitNode<>(compileStartMethods(), compileInterfaces(), complieConcreteImplementation());
  }
  protected abstract ConcreteImplementationNode<TypeName, MethodDeclaration> complieConcreteImplementation();
  protected abstract List<AbstractMethodNode<TypeName, MethodDeclaration>> compileStartMethods();
  protected abstract List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> compileInterfaces();

  public class TypeName {
    public final Named q;
    public final Word<Named> α;
    public final Set<Named> legalJumps;

    public TypeName(Named q, Word<Named> α, Set<Named> legalJumps) {
      this.q = q;
      this.α = α;
      this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
    }
    TypeName(Named q) {
      this.q = q;
      this.α = null;
      this.legalJumps = null;
    }
    TypeName() {
      this.q = null;
      this.α = null;
      this.legalJumps = null;
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
    @Override public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof APICompiler.TypeName))
        return false;
      APICompiler.TypeName other = (TypeName) o;
      return Objects.equals(q, other.q) && //
          Objects.equals(α, other.α) && //
          Objects.equals(legalJumps, other.legalJumps);
    }
    @Override public String toString() {
      return String.format("<~%s,%s,%s~>", q, α, legalJumps);
    }
  }

  public class MethodDeclaration {
    public final Verb name;
    private List<ParameterFragment> inferredParameters;

    public MethodDeclaration(Verb name) {
      this.name = name;
    }
    public List<ParameterFragment> getInferredParameters() {
      if (inferredParameters == null)
        throw new IllegalStateException("parameter types and names not decided");
      return inferredParameters;
    }
    public void setInferredParameters(List<ParameterFragment> inferredParameters) {
      this.inferredParameters = inferredParameters;
    }
  }

  public static class ParameterFragment {
    public final String parameterType;
    public final String parameterName;

    private ParameterFragment(String parameterType, String parameterName) {
      this.parameterType = parameterType;
      this.parameterName = parameterName;
    }
    public static ParameterFragment of(String parameterType, String parameterName) {
      return new ParameterFragment(parameterType, parameterName);
    }
    public String parameterType() {
      return parameterType();
    }
    public String parameterName() {
      return parameterName;
    }
  }

  public class InterfaceDeclaration {
    public final Named q;
    public final Word<Named> α;
    public final Set<Named> legalJumps;
    @SuppressWarnings("hiding") public final Word<Named> typeVariables;
    public final boolean isAccepting;

    public InterfaceDeclaration(Named q, Word<Named> α, Set<Named> legalJumps, Word<Named> typeVariables, boolean isAccepting) {
      this.q = q;
      this.α = α;
      this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
      this.typeVariables = typeVariables;
      this.isAccepting = isAccepting;
    }
    InterfaceDeclaration() {
      this.q = null;
      this.α = null;
      this.legalJumps = null;
      this.typeVariables = null;
      this.isAccepting = false;
    }
  }

  public boolean isTypeVariable(PolymorphicTypeNode<TypeName> type) {
    return typeVariables.get(type.name.q) == type;
  }
}
