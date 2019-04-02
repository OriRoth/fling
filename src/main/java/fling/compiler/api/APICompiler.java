package fling.compiler.api;

import static fling.compiler.api.nodes.PolymorphicTypeNode.bot;
import static fling.compiler.api.nodes.PolymorphicTypeNode.top;
import static fling.grammar.sententials.Alphabet.ε;
import static fling.util.Collections.asList;
import static fling.util.Collections.asWord;
import static fling.util.Collections.chainList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fling.automata.DPDA;
import fling.automata.DPDA.δ;
import fling.compiler.api.nodes.APICompilationUnitNode;
import fling.compiler.api.nodes.AbstractMethodNode;
import fling.compiler.api.nodes.ConcreteImplementationNode;
import fling.compiler.api.nodes.InterfaceNode;
import fling.compiler.api.nodes.PolymorphicTypeNode;
import fling.grammar.sententials.Constants;
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
public class APICompiler {
  public final DPDA<Named, Verb, Named> dpda;
  private final Map<TypeName, InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration>> types;
  private final Map<Named, PolymorphicTypeNode<TypeName>> typeVariables = new LinkedHashMap<>();

  public APICompiler(DPDA<Named, Verb, Named> dpda) {
    this.dpda = dpda;
    this.types = new LinkedHashMap<>();
    dpda.Q().forEach(q -> typeVariables.put(q, new PolymorphicTypeNode<>(new TypeName(q))));
  }
  public APICompilationUnitNode<TypeName, MethodDeclaration, InterfaceDeclaration> compileFluentAPI() {
    return new APICompilationUnitNode<>(compileStartMethods(), compileInterfaces(), complieConcreteImplementation());
  }
  @SuppressWarnings("unused") private ConcreteImplementationNode<TypeName, MethodDeclaration> complieConcreteImplementation() {
    return new ConcreteImplementationNode<>(dpda.Σ() //
        .filter(σ -> Constants.$$ != σ) //
        .map(σ -> new AbstractMethodNode.Chained<TypeName, MethodDeclaration>(new MethodDeclaration(σ))) //
        .collect(toList()));
  }
  private List<AbstractMethodNode<TypeName, MethodDeclaration>> compileStartMethods() {
    List<AbstractMethodNode<TypeName, MethodDeclaration>> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(new AbstractMethodNode.Start<>(new MethodDeclaration(Constants.$$), //
          PolymorphicTypeNode.top()));
    for (Verb σ : dpda.Σ) {
      δ<Named, Verb, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      AbstractMethodNode.Start<TypeName, MethodDeclaration> startMethod = //
          new AbstractMethodNode.Start<>(new MethodDeclaration(σ), //
              consolidate(δ.q$, dpda.γ0.pop().push(δ.getΑ()), true));
      if (!startMethod.returnType.isBot())
        $.add(startMethod);
    }
    return $;
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
  private TypeName encodedName(final Named q, final Word<Named> α) {
    final TypeName $ = new TypeName(q, α);
    if (types.containsKey($))
      return $;
    types.put($, null); // Pending computation.
    types.put($, encodedBody(q, α));
    return $;
  }
  private InterfaceNode<TypeName, MethodDeclaration, InterfaceDeclaration> encodedBody(final Named q, final Word<Named> α) {
    List<AbstractMethodNode<APICompiler.TypeName, APICompiler.MethodDeclaration>> $ = new ArrayList<>();
    $.addAll(dpda.Σ().map(σ -> //
    new AbstractMethodNode.Intermediate<>(new MethodDeclaration(σ), next(q, α, σ))).collect(toList()));
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
  private PolymorphicTypeNode<TypeName> next(final Named q, final Word<Named> α, final Verb σ) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? PolymorphicTypeNode.bot() : common(δ, α.pop(), false);
  }
  private PolymorphicTypeNode<TypeName> consolidate(final Named q, final Word<Named> α, boolean isInitialType) {
    final δ<Named, Verb, Named> δ = dpda.δδ(q, ε(), α.top());
    return δ == null ? new PolymorphicTypeNode<>(encodedName(q, α), getTypeArguments(isInitialType))
        : common(δ, α.pop(), isInitialType);
  }
  private PolymorphicTypeNode<TypeName> common(final δ<Named, Verb, Named> δ, final Word<Named> α, boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, isInitialType);
      return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.getΑ()), getTypeArguments(isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, isInitialType);
    return new PolymorphicTypeNode<>(encodedName(δ.q$, δ.getΑ()), //
        dpda.Q().map(q -> consolidate(q, α, isInitialType)).collect(toList()));
  }
  private PolymorphicTypeNode<APICompiler.TypeName> getTypeArgument(final δ<Named, Verb, Named> δ, boolean isInitialType) {
    return !isInitialType ? typeVariables.get(δ.q$) : dpda.isAccepting(δ.q$) ? top() : bot();
  }
  private List<PolymorphicTypeNode<APICompiler.TypeName>> getTypeArguments(boolean isInitialType) {
    return !isInitialType ? asList(typeVariables.values())
        : dpda.Q().map(q$ -> dpda.isAccepting(q$) ? PolymorphicTypeNode.<TypeName> top() : PolymorphicTypeNode.<TypeName> bot())
            .collect(toList());
  }

  public class TypeName {
    public final Named q;
    public final Word<Named> α;

    public TypeName(Named q, Word<Named> α) {
      this.q = q;
      this.α = α;
    }
    TypeName(Named q) {
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
      APICompiler.TypeName other = (TypeName) o;
      return Objects.equals(q, other.q) && Objects.equals(α, other.α);
    }
    @Override public String toString() {
      return String.format("<~%s,%s~>", q, α);
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
    @SuppressWarnings("hiding") public final Word<Named> typeVariables;

    public InterfaceDeclaration(Named q, Word<Named> α, Word<Named> typeVariables) {
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
