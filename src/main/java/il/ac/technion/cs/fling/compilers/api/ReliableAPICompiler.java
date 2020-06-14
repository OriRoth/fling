package il.ac.technion.cs.fling.compilers.api;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.util.As.list;
import static il.ac.technion.cs.fling.internal.util.As.word;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeBody;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** {@link APICompiler} generating (possibly) exponential number of API types.
 * Supported method chains compiles only when prefix of legal word.
 *
 * @author Ori Roth */
public class ReliableAPICompiler extends APICompiler {
  public ReliableAPICompiler(final DPDA<Named, Token, Named> dpda) {
    super(dpda);
  }

  @Override protected List<Method.Start> compileStartMethods() {
    final List<Method.Start> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(new Method.Start(new MethodDeclaration(Constants.$$), //
          Type.TOP));
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      final Method.Start startMethod = //
          new Method.Start(new MethodDeclaration(σ), //
              consolidate(δ.q$, //
                  dpda.γ0.pop().push(δ.getΑ()), //
                  new LinkedHashSet<>(dpda.Q().filter(dpda::isAccepting).collect(toList())), //
                  true));
      if (!(startMethod.returnType == Type.BOTTOM))
        $.add(startMethod);
    }
    return $;
  }

  @Override protected List<Interface> compileInterfaces() {
    return list( //
        fixedInterfaces(), //
        types.values().stream().filter(interfaze -> !interfaze.isBot()).collect(toList()));
  }

  @SuppressWarnings("static-method") private List<Interface> fixedInterfaces() {
    return Arrays.asList(Interface.top());
  }

  @Override protected TypeBody complieConcreteImplementation() {
    return new TypeBody(dpda.Σ() //
        .filter(σ -> Constants.$$ != σ) //
        .map(σ -> new Method.Chained(new MethodDeclaration(σ))) //
        .collect(toList()));
  }

  /** Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name */
  private TypeName encodedName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final TypeName $ = new TypeName(q, α, legalJumps);
    if (types.containsKey($))
      return types.get($).isBot() ? null : $;
    types.put($, shallowIsBot($) ? //
        Interface.bot() : //
        Interface.top()); // Pending computation.
    final Interface i = encodeInterface(q, α, legalJumps);
    types.put($, i == null ? Interface.bot() : i);
    return types.get($).isBot() ? null : $;
  }

  private boolean shallowIsBot(final TypeName type) {
    if (dpda.isAccepting(type.q))
      return false;
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δδ(type.q, σ, type.α);
      if (δ == null)
        continue;
      if (!δ.getΑ().isEmpty())
        // Consuming transition.
        return false;
      if (type.legalJumps.contains(δ.q$))
        // Legal epsilon-transition is possible.
        return false;
    }
    return true;
  }

  private Interface encodeInterface(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final List<Method> $ = dpda.Σ().map(σ -> new Method.Intermediate(σ, next(q, α, legalJumps, σ))) //
        .filter(m -> m.returnType != Type.BOTTOM) //
        .collect(toList());
    if (dpda.isAccepting(q))
      $.add(new Method.Termination());
    return $.isEmpty() ? null
        : new Interface(new InterfaceDeclaration(q, α, legalJumps, word(legalJumps), dpda.isAccepting(q)), $);
  }

  /** Computes the type representing the state of the automaton after consuming an
   * input letter.
   *
   * @param q          current state
   * @param α          all known information about the top of the stack
   * @param legalJumps
   * @param σ          current input letter
   * @return next state type */
  private Type next(final Named q, final Word<Named> α, final Set<Named> legalJumps, final Token σ) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? Type.BOTTOM : common(δ, α.pop(), legalJumps, false);
  }

  private Type consolidate(final Named q, final Word<Named> α, final Set<Named> legalJumps,
      final boolean isInitialType) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, ε(), α.top());
    if (δ == null) {
      final TypeName name = encodedName(q, α, legalJumps);
      return name == null ? Type.BOTTOM : Type.of(name).with(getTypeArguments(legalJumps, isInitialType));
    }
    return common(δ, α.pop(), legalJumps, isInitialType);
  }

  private Type common(final δ<Named, Token, Named> δ, final Word<Named> α, final Set<Named> legalJumps,
      final boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, legalJumps, isInitialType);
      final TypeName name = encodedName(δ.q$, δ.getΑ(), legalJumps);
      return name == null ? Type.BOTTOM : Type.of(name).with(getTypeArguments(legalJumps, isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, legalJumps, isInitialType);
    final Map<Named, Type> typeArguments = new LinkedHashMap<>();
    for (final Named q : dpda.Q) {
      final Type argument = consolidate(q, α, legalJumps, isInitialType);
      if (!(argument == Type.BOTTOM))
        typeArguments.put(q, argument);
    }
    final TypeName name = encodedName(δ.q$, δ.getΑ(), typeArguments.keySet());
    return name == null ? Type.BOTTOM : //
        Type.of(encodedName(δ.q$, δ.getΑ(), typeArguments.keySet())).with(new ArrayList<>(typeArguments.values()));
  }

  private Type getTypeArgument(final δ<Named, Token, Named> δ, final Set<Named> legalJumps,
      final boolean isInitialType) {
    return !legalJumps.contains(δ.q$) ? Type.BOTTOM : //
        isInitialType ? Type.TOP : //
            typeVariables.get(δ.q$);
  }

  private List<Type> getTypeArguments(final Set<Named> legalJumps, final boolean isInitialType) {
    return !isInitialType ? //
        dpda.Q() //
            .filter(legalJumps::contains) //
            .map(typeVariables::get) //
            .collect(toList()) //
        : legalJumps.stream() //
            .map(q -> Type.TOP) //
            .collect(toList());
  }
}
