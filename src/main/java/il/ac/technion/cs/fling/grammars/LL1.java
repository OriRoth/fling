package il.ac.technion.cs.fling.grammars;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.internal.util.As.reversed;
import java.util.*;
import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF.SF;
import il.ac.technion.cs.fling.DPDA.δ;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** LL grammar, supporting 1 lookahead symbol. Given variable 'v' and terminal
 * 't', only a single derivation may be inferred.
 *
 * @author Ori Roth */
public enum LL1 {
  ;
  /** Translate LL(1) BNF to DPDA. */
  public static DPDA<Named, Token, Named> buildAutomaton(final BNF _bnf) {
    final Firsts bnf = new Firsts(_bnf);
    final Set<δ<Named, Token, Named>> δs = new LinkedHashSet<>();
    final Set<Named> F = new LinkedHashSet<>();
    final Named q0;
    final Word<Named> γ0;
    final Set<Token> Σ = new LinkedHashSet<>();
    bnf.tokens().forEach(Σ::add);
    // Is this really essential? We make no assumptions on the BNF. Try to remove
    // this statement.
    Σ.remove(Constants.$$);
    // TODO use namer to determine type names.
    final Map<Token, Named> typeNameMapping = new LinkedHashMap<>();
    final Map<String, Integer> usedNames = new LinkedHashMap<>();
    for (final Token v : Σ) {
      final String name = v.name();
      if (!usedNames.containsKey(name)) {
        // Why do you use 2 here?
        usedNames.put(name, 2);
        typeNameMapping.put(v, Named.by(name));
      } else
        typeNameMapping.put(v, Named.by(name + usedNames.put(name, usedNames.get(name) + 1)));
    }
    final Set<Named> Q = new LinkedHashSet<>(typeNameMapping.values());
    final Named q0$ = Named.by("q0$"), q0ø = Named.by("q0ø"), qT = Named.by("qT");
    Q.add(q0$);
    Q.add(q0ø);
    Q.add(qT);
    final Set<Named> Γ = new LinkedHashSet<>(typeNameMapping.values());
    Γ.add(Constants.$$);
    bnf.variables().forEach(Γ::add);
    final Map<Variable, Named> A = new LinkedHashMap<>();
    bnf.variables().forEach(v -> A.put(v, getAcceptingVariable(v)));
    Γ.addAll(A.values());
    F.add(q0$);
    q0 = bnf.nullable(bnf.start()) ? q0$ : q0ø;
    γ0 = getPossiblyAcceptingVariables(bnf, typeNameMapping, new SF(new Word<>(Constants.$$, bnf.start())), true);
    /* Computing automaton transitions for q0ø */
    // Moving from q0ø to q0$ with ε + $.
    δs.add(new δ<>(q0ø, ε(), Constants.$$, q0$, Word.empty()));
    // Moving from q0ø to q0$ with ε + accepting nullable variable.
    for (final Variable v : A.keySet())
      if (bnf.nullable(v))
        δs.add(new δ<>(q0ø, ε(), A.get(v), q0$, new Word<>(A.get(v))));
    // Moving from q0ø to qσ with σ + appropriate variable.
    bnf.variables().forEach(v -> //
    bnf.forms(v).forEach(sf -> //
    bnf.firsts(sf.inner()).stream() //
        .filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> { //
          δs.add(new δ<>(q0ø, σ, v, typeNameMapping.get(σ),
              reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, false))));
          if (!bnf.nullable(v))
            δs.add(new δ<>(q0ø, σ, A.get(v), typeNameMapping.get(σ),
                reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
        })));
    bnf.variables().forEach(v -> //
    bnf.tokens().forEach(σ -> { //
      if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ) && bnf.nullable(v))
        δs.add(new δ<>(q0ø, σ, v, typeNameMapping.get(σ), Word.empty()));
    }));
    // Moving from q0ø to q0ø with σ + σ.
    bnf.tokens().forEach(σ -> {
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0ø, σ, typeNameMapping.get(σ), q0ø, Word.empty()));
    });
    // Get stuck in q0ø with σ + inappropriate variable.
    /* Computing automaton transitions for q0$ */
    // Moving from q0$ to q0ø with ε + terminal.
    bnf.tokens().forEach(σ -> {
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0$, ε(), typeNameMapping.get(σ), q0ø, new Word<>(typeNameMapping.get(σ))));
    });
    // Moving from q0$ to q0ø with ε + non-accepting variable.
    bnf.variables().forEach(v -> δs.add(new δ<>(q0$, ε(), v, q0ø, new Word<>(v))));
    // Moving from q0$ to q0ø with ε + non-nullable accepting variable.
    bnf.variables().filter(v -> !bnf.nullable(v)) //
        .forEach(v -> δs.add(new δ<>(q0$, ε(), A.get(v), q0ø, new Word<>(A.get(v)))));
    // Moving from q0$ to qσ with σ + appropriate variable.
    bnf.variables().filter(bnf::nullable).forEach(v -> //
    bnf.forms(v).forEach(sf -> //
    bnf.firsts(sf.inner()).stream() //
        .filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> {
          δs.add(new δ<>(q0$, σ, A.get(v), typeNameMapping.get(σ),
              reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
        })));
    bnf.variables().filter(bnf::nullable).forEach(v -> //
    bnf.tokens().forEach(σ -> {
      if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ))
        δs.add(new δ<>(q0$, σ, A.get(v), typeNameMapping.get(σ), Word.empty()));
    }));
    // Get stuck in q0$ with σ + inappropriate variable.
    /* Computing automaton transitions for qσ */
    // Moving from qσ to q0$ with ε + σ.
    bnf.tokens().filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> δs.add(new δ<>(typeNameMapping.get(σ), ε(), typeNameMapping.get(σ), q0$, Word.empty())));
    // Moving from qσ to qσ with ε + appropriate variable.
    bnf.variables().forEach(v -> //
    bnf.forms(v).forEach(sf -> //
    bnf.firsts(sf.inner()).stream().filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> {
          final Named σState = typeNameMapping.get(σ);
          δs.add(new δ<>(σState, ε(), A.get(v), σState,
              reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, true))));
          δs.add(new δ<>(σState, ε(), v, σState,
              reversed(getPossiblyAcceptingVariables(bnf, typeNameMapping, sf, false))));
        })));
    // Moving from qσ to qσ with ε + nullable variable.
    bnf.tokens().filter(σ -> !Constants.$$.equals(σ)).forEach(σ -> //
    bnf.variables().filter(bnf::nullable).filter(v -> !bnf.firsts(v).contains(σ)) //
        .forEach(v -> {
          final Named σState = typeNameMapping.get(σ);
          δs.add(new δ<>(σState, ε(), v, σState, Word.empty()));
          δs.add(new δ<>(σState, ε(), A.get(v), σState, Word.empty()));
        }));
    // Moving from qσ to qT with ε + inappropriate, non-nullable symbol.
    bnf.tokens().filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> {
          final Set<Named> legalTops = new HashSet<>();
          legalTops.add(typeNameMapping.get(σ));
          bnf.variables().forEach(v -> bnf.forms(v).forEach(sf -> { //
            if (bnf.firsts(sf.inner()).contains(σ)) {
              legalTops.add(v);
              legalTops.add(getAcceptingVariable(v));
            }
          }));
          bnf.variables() //
              .filter(bnf::nullable) //
              .forEach(v -> {
                legalTops.add(v);
                legalTops.add(A.get(v));
              });
          for (final Named γ : Γ)
            if (!legalTops.contains(γ))
              δs.add(new δ<>(typeNameMapping.get(σ), ε(), γ, qT, Word.empty()));
        });
    bnf.tokens().filter(σ -> !Constants.$$.equals(σ)) //
        .forEach(σ -> {
          final Set<Named> legalTops = new HashSet<>();
          legalTops.add(typeNameMapping.get(σ));
          bnf.variables().forEach(v -> //
          bnf.forms(v).forEach(sf -> { //
            if (bnf.firsts(sf.inner()).contains(σ)) {
              legalTops.add(v);
              legalTops.add(getAcceptingVariable(v));
            }
          }));
          bnf.variables() //
              .filter(bnf::nullable) //
              .forEach(v -> {
                legalTops.add(v);
                legalTops.add(A.get(v));
              });
          for (final Named γ : Γ)
            if (!legalTops.contains(γ))
              δs.add(new δ<>(typeNameMapping.get(σ), ε(), γ, qT, Word.empty()));
        });
    // Automaton should not stop in qσ.
    // Automaton gets stuck after reaching qT.
    return new DPDA<>(Q, Σ, Γ, δs, F, q0, γ0);
  }
  private static Named getAcceptingVariable(final Variable v) {
    return Named.by(v.name() + "$");
  }
  private static Word<Named> getPossiblyAcceptingVariables(final Nullables e, final Map<Token, Named> typeNameMapping,
      final SF sf, final boolean isFromQ0$) {
    final List<Named> $ = new ArrayList<>();
    boolean isAccepting = isFromQ0$;
    for (final Symbol s : reversed(sf.inner())) {
      $.add(s.isVariable() && isAccepting ? //
          getAcceptingVariable(s.asVariable()) : //
          s.isToken() && !Constants.$$.equals(s) ? typeNameMapping.get(s) : //
              s);
      isAccepting &= e.nullable(s);
    }
    return new Word<>(reversed($));
  }
}
