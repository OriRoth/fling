package fling.compiler.api;

import static fling.compiler.api.nodes.PolymorphicTypeNode.*;
import static fling.grammar.sententials.Alphabet.ε;
import static fling.util.Collections.asList;
import static fling.util.Collections.asWord;
import static fling.util.Collections.chainList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fling.automata.DPDA;
import fling.automata.DPDA.δ;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.CompilationUnitNode;
import fling.compiler.api.nodes.InterfaceNode;
import fling.compiler.api.nodes.PolymorphicTypeNode;
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
 * @param <Q> states set
 * @param <Σ> alphabet set
 * @param <Γ> stack symbols set
 */
public class APICompiler<Q, Σ, Γ> {
  public final DPDA<Q, Σ, Γ> dpda;
  private final Map<TypeName, InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> types;
  private final Map<Q, PolymorphicTypeNode<TypeName>> typeVariables = new LinkedHashMap<>();

  public APICompiler(DPDA<Q, Σ, Γ> dpda) {
    this.dpda = dpda;
    this.types = new LinkedHashMap<>();
    dpda.Q().forEach(q -> typeVariables.put(q, new PolymorphicTypeNode<>(new TypeName(q))));
  }
  public CompilationUnitNode<TypeName, MethodDeclaration, InterfaceDeclaration> compileFluentAPI() {
    return new CompilationUnitNode<>(compileStartMethods(), compileInterfaces());
  }
  private List<AbstractMethodNode<TypeName, MethodDeclaration>> compileStartMethods() {
    return Collections.singletonList(new AbstractMethodNode.Start<>(consolidate(dpda.q0, dpda.γ0, true)));
  }
  private List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> compileInterfaces() {
    return chainList(fixedInterfaces(), types.values());
  }
  @SuppressWarnings("static-method") private List<InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> fixedInterfaces() {
    return Arrays.asList(InterfaceNode.top(), InterfaceNode.bot());
  }
  /**
   * Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name
   */
  private TypeName encodedName(final Q q, final Word<Γ> α) {
    final TypeName $ = new TypeName(q, α);
    if (types.containsKey($))
      return $;
    types.put($, null); // Pending computation.
    types.put($, encodedBody(q, α));
    return $;
  }
  private InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration> encodedBody(final Q q, final Word<Γ> α) {
    List<AbstractMethodNode<APICompiler<Q, Σ, Γ>.TypeName, APICompiler<Q, Σ, Γ>.MethodDeclaration>> $ = new ArrayList<>();
    $.addAll(dpda.Σ().map(σ -> //
    new AbstractMethodNode.Intermediate<>(new MethodDeclaration(σ), next(q, α, σ))).collect(Collectors.toList()));
    if (dpda.isAccepting(q))
      $.add(new AbstractMethodNode.Termination<>());
    return new InterfaceNode<>(new InterfaceDeclaration(q, α, asWord(dpda.Q)), //
        Collections.unmodifiableList($));
  }
  /**
   * Computes the type representing the state of the automaton after consuming
   * an input letter.
   *
   * @param q current state
   * @param α all known information about the top of the stack
   * @param σ current input letter
   * @return next state type
   */
  private PolymorphicTypeNode<TypeName> next(final Q q, final Word<Γ> α, final Σ σ) {
    final δ<Q, Σ, Γ> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? PolymorphicTypeNode.bot() : common(δ, α.pop(), false);
  }
  private PolymorphicTypeNode<TypeName> consolidate(final Q q, final Word<Γ> α, boolean isInitialType) {
    final δ<Q, Σ, Γ> δ = dpda.δδ(q, ε(), α.top());
    return δ == null ? new PolymorphicTypeNode<>(encodedName(q, α), getTypeArguments(isInitialType))
        : common(δ, α.pop(), isInitialType);
  }
  private PolymorphicTypeNode<TypeName> common(final δ<Q, Σ, Γ> δ, final Word<Γ> α, boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.α.isEmpty())
        return getTypeArgument(δ, isInitialType);
      return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.α), getTypeArguments(isInitialType));
    }
    if (δ.α.isEmpty())
      return consolidate(δ.q$, α, isInitialType);
    return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.α), //
        dpda.Q().map(q -> consolidate(q, α, isInitialType)).collect(Collectors.toList()));
  }
  private PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName> getTypeArgument(final δ<Q, Σ, Γ> δ, boolean isInitialType) {
    return !isInitialType ? typeVariables.get(δ.q$) : dpda.isAccepting(δ.q$) ? top() : bot();
  }
  private List<PolymorphicTypeNode<APICompiler<Q, Σ, Γ>.TypeName>> getTypeArguments(boolean isInitialType) {
    return !isInitialType ? asList(typeVariables.values())
        : dpda.Q().map(q$ -> dpda.isAccepting(q$) ? PolymorphicTypeNode.<TypeName> top() : PolymorphicTypeNode.<TypeName> bot())
            .collect(Collectors.toList());
  }

  public class TypeName {
    public final Q q;
    public final Word<Γ> α;

    public TypeName(Q q, Word<Γ> α) {
      this.q = q;
      this.α = α;
    }
    TypeName(Q q) {
      this.q = q;
      this.α = null;
    }
    TypeName() {
      this.q = null;
      this.α = null;
    }
    @Override public int hashCode() {
      int $ = 1;
      if (q != null)
        $ = $ * 31 + q.hashCode();
      if (α != null)
        $ = $ * 31 + α.hashCode();
      return $;
    }
    @Override public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof APICompiler.TypeName))
        return false;
      @SuppressWarnings("unchecked") APICompiler<Q, Σ, Γ>.TypeName other = (TypeName) o;
      return Objects.equals(q, other.q) && Objects.equals(α, other.α);
    }
    @Override public String toString() {
      return String.format("<~%s,%s~>", q, α);
    }
  }

  public class MethodDeclaration {
    public final Σ name;

    public MethodDeclaration(Σ name) {
      this.name = name;
    }
    MethodDeclaration() {
      this.name = null;
    }
  }

  public class InterfaceDeclaration {
    public final Q q;
    public final Word<Γ> α;
    @SuppressWarnings("hiding") public final Word<Q> typeVariables;

    public InterfaceDeclaration(Q q, Word<Γ> α, Word<Q> typeVariables) {
      this.q = q;
      this.α = α;
      this.typeVariables = typeVariables;
    }
    InterfaceDeclaration() {
      this.q = null;
      this.α = null;
      this.typeVariables = null;
    }
  }

  public boolean isTypeVariable(PolymorphicTypeNode<TypeName> type) {
    return typeVariables.get(type.name.q) == type;
  }
}
