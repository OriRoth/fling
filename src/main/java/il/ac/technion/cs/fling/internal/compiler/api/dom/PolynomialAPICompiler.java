package il.ac.technion.cs.fling.internal.compiler.api.dom;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.util.As.list;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import static java.util.stream.Collectors.toList;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
import il.ac.technion.cs.fling.internal.util.As;
/** {@link APICompiler} generating polynomial number of API types. Supported
 * method chains may terminated only when legal, yet illegal method calls do not
 * raise compilation errors, but return bottom type.
 *
 * @author Ori Roth */
public class PolynomialAPICompiler extends APICompiler {
  public PolynomialAPICompiler(final DPDA<Named, Token, Named> dpda) {
    super(dpda);
  }
  private final Type.Grounded top = Type.Grounded.of(Type.Name.TOP);
  private final Type.Grounded bottom = Type.Grounded.of(Type.Name.BOTTOM);
  @Override protected List<Method> startMethods() {
    final List<Method> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(Method.named(Constants.$$).returning(top));
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      final Method m = Method.named(σ).returning(consolidate(δ.q$, dpda.γ0.pop().push(δ.getΑ()), true));
      if (m.type != bottom)
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
  private Type.Name encodedName(final Named q, final @NonNull Word<Named> α) {
    final Type.Name $ = Type.Name.q(q).α(α);
    if (types.containsKey($))
      return $;
    types.put($, null); // Pending computation.
    types.put($, encodeInterface(q, α));
    return $;
  }
  private Type encodeInterface(final Named q, final Word<Named> α) {
    final List<Method> $ = dpda.Σ().map(σ -> Method.named(σ).returning(next(q, α, σ))).collect(Collectors.toList());
    if (dpda.isAccepting(q))
      $.add(Method.termination());
    return new Type(Type.Name.q(q).α(α), $, As.word(dpda.Q), dpda.isAccepting(q));
  }
  /** Computes the type representing the state of the automaton after consuming an
   * input letter.
   *
   * @param q current state
   * @param α all known information about the top of the stack
   * @param σ current input letter
   * @return next state type */
  private Type.Grounded next(final Named q, final Word<Named> α, final Token σ) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? bottom : common(δ, α.pop(), false);
  }
  private Type.Grounded consolidate(final Named q, final Word<Named> α, final boolean isInitialType) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, ε(), α.top());
    return δ == null ? Type.Grounded.of(encodedName(q, α)).with(getTypeArguments(isInitialType))
        : common(δ, α.pop(), isInitialType);
  }
  private Type.Grounded common(final δ<Named, Token, Named> δ, final Word<Named> α, final boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, isInitialType);
      return Type.Grounded.of(encodedName(δ.q$, δ.getΑ())).with(getTypeArguments(isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, isInitialType);
    return Type.Grounded.of(encodedName(δ.q$, δ.getΑ()))
        .with(dpda.Q().map(q -> consolidate(q, α, isInitialType)).collect(toList()));
  }
  private Type.Grounded getTypeArgument(final δ<Named, Token, Named> δ, final boolean isInitialType) {
    return isInitialType ? dpda.isAccepting(δ.q$) ? top : bottom : typeVariables.get(δ.q$);
  }
  private List<Type.Grounded> getTypeArguments(final boolean isInitialType) {
    return isInitialType ? dpda.Q().map(q$ -> dpda.isAccepting(q$) ? top : bottom).collect(toList())
        : list(typeVariables.values());
  }
}