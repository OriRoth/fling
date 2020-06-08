package il.ac.technion.cs.fling.grammars;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.util.As.reversed;

import java.util.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.sententials.*;

/** LL grammar, supporting 1 lookahead symbol. Given variable 'v' and terminal
 * 't', only a single derivation may inferred.
 * 
 * @author Ori Roth */
public class LL1 extends Grammar {
  public LL1(final FancyEBNF bnf, final Namer namer) {
    super(bnf, namer);
  }

  /** Translate LL(1) BNF to DPDA. */
  @Override public DPDA<Named, Token, Named> buildAutomaton(final FancyEBNF bnf) {
    final Set<Named> Q = new LinkedHashSet<>();
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Named> Γ = new LinkedHashSet<>();
    final Set<δ<Named, Token, Named>> δs = new LinkedHashSet<>();
    final Set<Named> F = new LinkedHashSet<>();
    Named q0;
    Word<Named> γ0;
    Σ.addAll(bnf.Σ);
    Σ.remove(Constants.$$);
    // TODO use namer to determine type names.
    final Map<Token, Named> typeNameMapping = new LinkedHashMap<>();
    final Map<String, Integer> usedNames = new LinkedHashMap<>();
    for (final Token v : Σ) {
      final String name = v.name();
      if (!usedNames.containsKey(name)) {
        usedNames.put(name, 2);
        typeNameMapping.put(v, Named.by(name));
      } else
        typeNameMapping.put(v, Named.by(name + usedNames.put(name, usedNames.get(name) + 1)));
    }
    Q.addAll(typeNameMapping.values());
    final Named q0$ = Named.by("q0$"), q0ø = Named.by("q0ø"), qT = Named.by("qT");
    Q.add(q0$);
    Q.add(q0ø);
    Q.add(qT);
    Γ.addAll(typeNameMapping.values());
    Γ.add(Constants.$$);
    Γ.addAll(bnf.Γ);
    assert Γ.contains(Constants.S);
    final Map<Variable, Named> A = new LinkedHashMap<>();
    for (final Variable v : bnf.Γ)
      A.put(v, getAcceptingVariable(v));
    Γ.addAll(A.values());
    F.add(q0$);
    q0 = bnf.isNullable(Constants.S) ? q0$ : q0ø;
    γ0 = getPossiblyAcceptingVariables(bnf, typeNameMapping, new ExtendedSententialForm(Constants.$$, Constants.S),
        true);
    /* Computing automaton transitions for q0ø */
    // Moving from q0ø to q0$ with ε + $.
    δs.add(new δ<>(q0ø, ε(), Constants.$$, q0$, Word.empty()));
    // Moving from q0ø to q0$ with ε + accepting nullable variable.
    for (final Variable v : A.keySet())
      if (bnf.isNullable(v))
        δs.add(new δ<>(q0ø, ε(), A.get(v), q0$, new Word<>(A.get(v))));
    // Moving from q0ø to qσ with σ + appropriate variable.
    for (final DerivationRule r : bnf.R)
      for (final ExtendedSententialForm sf : r.rhs)
        for (final Token σ : bnf.firsts(sf))
          if (!Constants.$$.equals(σ)) {
            δs.add(new δ<>(q0ø, σ, r.lhs, typeNameMapping.get(σ),
                reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, false))));
            if (!bnf.isNullable(r.lhs))
              δs.add(new δ<>(q0ø, σ, A.get(r.lhs), typeNameMapping.get(σ),
                  reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
          }
    for (final Variable v : bnf.Γ)
      for (final Token σ : bnf.Σ)
        if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ) && bnf.isNullable(v))
          δs.add(new δ<>(q0ø, σ, v, typeNameMapping.get(σ), Word.empty()));
    // Moving from q0ø to q0ø with σ + σ.
    for (final Token σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0ø, σ, typeNameMapping.get(σ), q0ø, Word.empty()));
    // Get stuck in q0ø with σ + inappropriate variable.
    /* Computing automaton transitions for q0$ */
    // Moving from q0$ to q0ø with ε + terminal.
    for (final Token σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0$, ε(), typeNameMapping.get(σ), q0ø, new Word<>(typeNameMapping.get(σ))));
    // Moving from q0$ to q0ø with ε + non-accepting variable.
    for (final Named v : bnf.Γ)
      δs.add(new δ<>(q0$, ε(), v, q0ø, new Word<>(v)));
    // Moving from q0$ to q0ø with ε + non-nullable accepting variable.
    for (final Variable v : bnf.Γ)
      if (!bnf.isNullable(v))
        δs.add(new δ<>(q0$, ε(), A.get(v), q0ø, new Word<>(A.get(v))));
    // Moving from q0$ to qσ with σ + appropriate variable.
    for (final DerivationRule r : bnf.R)
      if (bnf.isNullable(r.lhs))
        for (final ExtendedSententialForm sf : r.rhs)
          for (final Token σ : bnf.firsts(sf))
            if (!Constants.$$.equals(σ))
              δs.add(new δ<>(q0$, σ, A.get(r.lhs), typeNameMapping.get(σ),
                  reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
    for (final Variable v : bnf.Γ)
      if (bnf.isNullable(v))
        for (final Token σ : bnf.Σ)
          if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ))
            δs.add(new δ<>(q0$, σ, A.get(v), typeNameMapping.get(σ), Word.empty()));
    // Get stuck in q0$ with σ + inappropriate variable.
    /* Computing automaton transitions for qσ */
    // Moving from qσ to q0$ with ε + σ.
    for (final Token σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(typeNameMapping.get(σ), ε(), typeNameMapping.get(σ), q0$, Word.empty()));
    // Moving from qσ to qσ with ε + appropriate variable.
    for (final DerivationRule r : bnf.R)
      for (final ExtendedSententialForm sf : r.rhs)
        for (final Token σ : bnf.firsts(sf))
          if (!Constants.$$.equals(σ)) {
            final Named σState = typeNameMapping.get(σ);
            δs.add(new δ<>(σState, ε(), A.get(r.lhs), σState,
                reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
            δs.add(new δ<>(σState, ε(), r.lhs, σState,
                reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, false))));
          }
    // Moving from qσ to qσ with ε + nullable variable.
    for (final Token σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        for (final Variable v : bnf.Γ)
          if (bnf.isNullable(v) && !bnf.firsts(v).contains(σ)) {
            final Named σState = typeNameMapping.get(σ);
            δs.add(new δ<>(σState, ε(), v, σState, Word.empty()));
            δs.add(new δ<>(σState, ε(), A.get(v), σState, Word.empty()));
          }
    // Moving from qσ to qT with ε + inappropriate, non-nullable symbol.
    for (final Token σ : bnf.Σ)
      if (!Constants.$$.equals(σ)) {
        final Set<Named> legalTops = new HashSet<>();
        legalTops.add(typeNameMapping.get(σ));
        for (final DerivationRule r : bnf.R)
          for (final ExtendedSententialForm sf : r.rhs)
            if (bnf.firsts(sf).contains(σ)) {
              legalTops.add(r.lhs);
              legalTops.add(getAcceptingVariable(r.lhs));
            }
        bnf.Γ.stream() //
            .filter(bnf::isNullable) //
            .forEach(v -> {
              legalTops.add(v);
              legalTops.add(A.get(v));
            });
        for (final Named γ : Γ)
          if (!legalTops.contains(γ))
            δs.add(new δ<>(typeNameMapping.get(σ), ε(), γ, qT, Word.empty()));
      }
    // Automaton should not stop in qσ.
    // Automaton gets stuck after reaching qT.
    return new DPDA<>(Q, Σ, Γ, δs, F, q0, γ0);
  }

  @SuppressWarnings("static-method") private Named getAcceptingVariable(final Variable v) {
    return Named.by(v.name() + "$");
  }

  private Word<Named> getPossiblyAcceptingVariables(final FancyEBNF bnf, final Map<Token, Named> typeNameMapping,
      final ExtendedSententialForm sf, final boolean isFromQ0$) {
    final List<Named> $ = new ArrayList<>();
    boolean isAccepting = isFromQ0$;
    for (final Symbol s : reversed(sf)) {
      $.add(s.isVariable() && isAccepting ? //
          getAcceptingVariable(s.asVariable()) : //
          s.isToken() && !Constants.$$.equals(s) ? typeNameMapping.get(s) : //
              s);
      isAccepting &= bnf.isNullable(s);
    }
    return new Word<>(reversed($));
  }
}
