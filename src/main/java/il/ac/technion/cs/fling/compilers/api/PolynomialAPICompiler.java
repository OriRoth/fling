package il.ac.technion.cs.fling.compilers.api;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.compiler.api.dom.Type.bot;
import static il.ac.technion.cs.fling.internal.compiler.api.dom.Type.top;
import static il.ac.technion.cs.fling.internal.util.As.list;
import static il.ac.technion.cs.fling.internal.util.As.word;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeBody;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** {@link APICompiler} generating polynomial number of API types. Supported
 * method chains may terminated only when legal, yet illegal method calls do not
 * raise compilation errors, but return bottom type.
 *
 * @author Ori Roth */
public class PolynomialAPICompiler extends APICompiler {
  private PolynomialAPICompiler(final DPDA<Named, Token, Named> dpda) {
    super(dpda);
  }

  @Override protected List<Method> compileStartMethods() {
    final List<Method> $ = new ArrayList<>();
    if (dpda.F.contains(dpda.q0))
      $.add(new Method.Start(new MethodDeclaration(Constants.$$), //
          Type.top()));
    for (final Token σ : dpda.Σ) {
      final δ<Named, Token, Named> δ = dpda.δ(dpda.q0, σ, dpda.γ0.top());
      if (δ == null)
        continue;
      final Method.Start startMethod = //
          new Method.Start(new MethodDeclaration(σ), //
              consolidate(δ.q$, dpda.γ0.pop().push(δ.getΑ()), true));
      if (!startMethod.returnType.isBot())
        $.add(startMethod);
    }
    return $;
  }

  @Override protected List<Interface> compileInterfaces() {
    return list(fixedInterfaces(), types.values());
  }

  @Override protected TypeBody complieConcreteImplementation() {
    return new TypeBody(dpda.Σ() //
        .filter(σ -> Constants.$$ != σ) //
        .map(σ -> new Method.Chained(new MethodDeclaration(σ))) //
        .collect(toList()));
  }

  @SuppressWarnings("static-method") private List<Interface> fixedInterfaces() {
    return Arrays.asList(Interface.top(), Interface.bot());
  }

  /** Get type name given a state and stack symbols to push. If this type is not
   * present, it is created.
   *
   * @param q current state
   * @param α current stack symbols to be pushed
   * @return type name */
  private TypeName encodedName(final Named q, final Word<Named> α) {
    final TypeName $ = new TypeName(q, α, null);
    if (types.containsKey($))
      return $;
    types.put($, null); // Pending computation.
    types.put($, encodedBody(q, α));
    return $;
  }

  private Interface encodedBody(final Named q, final Word<Named> α) {
    final List<Method> $ = dpda.Σ().map(σ -> //
    new Method.Intermediate(new MethodDeclaration(σ), next(q, α, σ))).collect(Collectors.toList());
    if (dpda.isAccepting(q))
      $.add(new Method.Termination());
    return new Interface(new InterfaceDeclaration(q, α, null, word(dpda.Q), dpda.isAccepting(q)), //
        Collections.unmodifiableList($));
  }

  /** Computes the type representing the state of the automaton after consuming an
   * input letter.
   *
   * @param q current state
   * @param α all known information about the top of the stack
   * @param σ current input letter
   * @return next state type */
  private Type next(final Named q, final Word<Named> α, final Token σ) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, σ, α.top());
    return δ == null ? Type.bot() : common(δ, α.pop(), false);
  }

  private Type consolidate(final Named q, final Word<Named> α, final boolean isInitialType) {
    final δ<Named, Token, Named> δ = dpda.δδ(q, ε(), α.top());
    return δ == null ? new Type(encodedName(q, α), getTypeArguments(isInitialType)) : common(δ, α.pop(), isInitialType);
  }

  private Type common(final δ<Named, Token, Named> δ, final Word<Named> α, final boolean isInitialType) {
    if (α.isEmpty()) {
      if (δ.getΑ().isEmpty())
        return getTypeArgument(δ, isInitialType);
      return new Type(encodedName(δ.q$, δ.getΑ()), getTypeArguments(isInitialType));
    }
    if (δ.getΑ().isEmpty())
      return consolidate(δ.q$, α, isInitialType);
    return new Type(encodedName(δ.q$, δ.getΑ()), //
        dpda.Q().map(q -> consolidate(q, α, isInitialType)).collect(toList()));
  }

  private Type getTypeArgument(final δ<Named, Token, Named> δ, final boolean isInitialType) {
    return !isInitialType ? typeVariables.get(δ.q$) : dpda.isAccepting(δ.q$) ? top() : bot();
  }

  private List<Type> getTypeArguments(final boolean isInitialType) {
    return !isInitialType ? list(typeVariables.values())
        : dpda.Q().map(q$ -> dpda.isAccepting(q$) ? Type.top() : Type.bot()).collect(toList());
  }
}
