package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import java.util.*;
import static java.util.stream.Collectors.toList;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Name;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
import il.ac.technion.cs.fling.internal.util.As;
/** {@link APICompiler} generating (possibly) exponential number of API types.
 * Supported method chains compiles only when prefix of legal word.
 *
 * @author Ori Roth */
public class ReliableAPICompiler extends APICompiler {
  private static final Name TOP = Type.Name.TOP;
  private static final Type topType = Type.named(TOP);
  private static final Name BOTTOM = Type.Name.BOTTOM;
  private static final Type bottomType = Type.named(BOTTOM);
  public ReliableAPICompiler(final DPDA<Named, Token, Named> dpda) {
    super(dpda);
  }
  {
    add(topType);
  }
  @Override protected List<Method> startMethods() {
    final List<Method> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(Method.termination());
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      final Method m = Method.named(σ).returning(consolidate(δ.q$, //
          dpda.γ0.pop().push(δ.getΑ()), //
          new LinkedHashSet<>(dpda.Q().filter(dpda::isAccepting).collect(toList())), //
          true));
      if (m.type != Type.Grounded.BOTTOM)
        $.add(m);
    }
    return $;
  }
  /** Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name */
  private Type.Name encodedName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final Type.Name.q.α.β $ = Type.Name.q(q).α(α).β(legalJumps);
    if (types.containsKey($))
      return BOTTOM.equals(types.get($).name) ? null : $;
    types.put($, shallowIsBot($) ? bottomType : topType); // Pending computation.
    final Type i = encodeType(q, α, legalJumps);
    types.put($, i == null ? bottomType : i);
    return BOTTOM.equals(types.get($).name) ? null : $;
  }
  private boolean shallowIsBot(final Type.Name.q.α.β n) {
    if (dpda.isAccepting(n.q()))
      return false;
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δδ(n.q(), σ, n.α());
      if (δ == null)
        continue;
      if (!δ.getΑ().isEmpty())
        // Consuming transition.
        return false;
      if (n.β.contains(δ.q$))
        // Legal epsilon-transition is possible.
        return false;
    }
    return true;
  }
  private Type encodeType(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final List<Method> $ = dpda.Σ().map(σ -> Method.named(σ).returning(next(q, α, legalJumps, σ))) //
        .filter(m -> m.type != Type.Grounded.BOTTOM) //
        .collect(toList());
    if (dpda.isAccepting(q))
      $.add(Method.termination());
    return $.isEmpty() ? null
        : new Type(Type.Name.q(q).α(α).β(legalJumps), $, As.word(legalJumps), dpda.isAccepting(q));
  }
  /** Computes the type representing the state of the automaton after consuming an
   * input letter.
   *
   * @param q          current state
   * @param α          all known information about the top of the stack
   * @param legalJumps
   * @param σ          current input letter
   * @return next state type */
  private Type.Grounded next(final Named q, final Word<Named> α, final Set<Named> legalJumps, final Token σ) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? Type.Grounded.BOTTOM : common(δ, α.pop(), legalJumps, false);
  }
  private Type.Grounded consolidate(final Named q, final Word<Named> α, final Set<Named> legalJumps,
      final boolean isInitialType) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, ε(), α.top());
    if (δ == null) {
      final Type.Name name = encodedName(q, α, legalJumps);
      return name == null ? Type.Grounded.BOTTOM
          : Type.Grounded.of(name).with(getTypeArguments(legalJumps, isInitialType));
    }
    return common(δ, α.pop(), legalJumps, isInitialType);
  }
  private Type.Grounded common(final δ<Named, Token, Named> δ, final Word<Named> α, final Set<Named> legalJumps,
      final boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, legalJumps, isInitialType);
      final Type.Name name = encodedName(δ.q$, δ.getΑ(), legalJumps);
      return name == null ? Type.Grounded.BOTTOM
          : Type.Grounded.of(name).with(getTypeArguments(legalJumps, isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, legalJumps, isInitialType);
    final Map<Named, Type.Grounded> typeArguments = new LinkedHashMap<>();
    for (final Named q : dpda.Q) {
      final Type.Grounded argument = consolidate(q, α, legalJumps, isInitialType);
      if (argument != Type.Grounded.BOTTOM)
        typeArguments.put(q, argument);
    }
    final Type.Name name = encodedName(δ.q$, δ.getΑ(), typeArguments.keySet());
    return name == null ? Type.Grounded.BOTTOM : //
        Type.Grounded.of(encodedName(δ.q$, δ.getΑ(), typeArguments.keySet()))
            .with(new ArrayList<>(typeArguments.values()));
  }
  private Type.Grounded getTypeArgument(final δ<Named, Token, Named> δ, final Set<Named> legalJumps,
      final boolean isInitialType) {
    //
    return legalJumps.contains(δ.q$) ? isInitialType ? Type.Grounded.TOP : //
        typeVariables.get(δ.q$) : Type.Grounded.BOTTOM;
  }
  private List<Type.Grounded> getTypeArguments(final Set<Named> legalJumps, final boolean isInitialType) {
    //
    //
    return isInitialType ? legalJumps.stream() //
        .map(q -> Type.Grounded.TOP) //
        .collect(toList())
        : dpda.Q() //
            .filter(legalJumps::contains) //
            .map(typeVariables::get) //
            .collect(toList());
  }
}
