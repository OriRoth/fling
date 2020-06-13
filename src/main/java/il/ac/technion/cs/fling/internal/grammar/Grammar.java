package il.ac.technion.cs.fling.internal.grammar;

import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifier;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

public abstract class Grammar {
  public final FancyEBNF ebnf;
  public final Namer namer;
  public final FancyEBNF bnf;
  public final FancyEBNF normalizedBNF;
  public final FancyEBNF normalizedEBNF;
  private final Map<Variable, FancyEBNF> subBNFs;

  public Grammar(final FancyEBNF ebnf, final Namer namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    bnf = getBNF(ebnf);
    normalizedEBNF = normalize(ebnf, namer);
    normalizedBNF = getBNF(normalizedEBNF);
    subBNFs = new LinkedHashMap<>();
    for (final Variable head : bnf.headVariables)
      subBNFs.put(head, computeSubBNF(head));
  }

  public abstract DPDA<Named, Token, Named> buildAutomaton(FancyEBNF bnf);

  // TODO compute lazily.
  public DPDA<Named, Token, Named> toDPDA() {
    return buildAutomaton(bnf);
  }

  private FancyEBNF getBNF(final FancyEBNF ebnf) {
    final Set<Variable> Γ = new LinkedHashSet<>(ebnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    final Map<Variable, Quantifier> extensionHeadsMapping = new LinkedHashMap<>();
    final Set<Variable> extensionProducts = new LinkedHashSet<>();
    for (final ERule r : ebnf.R) {
      final List<Body> rhs = new ArrayList<>();
      for (final Body b : r.bodiesList()) {
        final List<Component> cs = new ArrayList<>();
        for (final Component c : b) {
          if (!c.isQuantifier()) {
            cs.add(c);
            continue;
          }
          final Quantifier q = c.asQuantifier();
          final Variable head = q.expand(namer, extensionProducts::add, R::add);
          extensionHeadsMapping.put(head, q);
          cs.add(head);
        }
        rhs.add(new Body(cs));
      }
      R.add(new ERule(r.variable, rhs));
    }
    Γ.addAll(extensionProducts);
    return new FancyEBNF(new EBNF(ebnf.Σ, Γ, ebnf.ε, R), ebnf.headVariables, extensionHeadsMapping, extensionProducts,
        false);
  }

  public FancyEBNF getSubBNF(final Variable variable) {
    return subBNFs.get(variable);
  }

  private FancyEBNF computeSubBNF(final Variable v) {
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Variable> Γ = new LinkedHashSet<>();
    Γ.add(v);
    final Set<ERule> rs = new LinkedHashSet<>();
    for (boolean more = true; more;) {
      more = false;
      for (final ERule r : bnf.R)
        if (!rs.contains(r) && Γ.contains(r.variable)) {
          more = true;
          rs.add(r);
          r.variables().forEachOrdered(Γ::add);
          r.tokens().forEachOrdered(Σ::add);
        }
    }
    return new FancyEBNF(new EBNF(Σ, Γ, v, rs), null, null, null, true);
  }

  private static FancyEBNF normalize(final FancyEBNF bnf, final Namer namer) {
    final Set<Variable> Γ = new LinkedHashSet<>(bnf.Γ);
    final Set<ERule> R = new LinkedHashSet<>();
    for (final Variable v : bnf.Γ) {
      final List<Body> rhs = bnf.bodiesList(v);
      assert rhs.size() > 0 : v + " in: " + bnf;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new ERule(v, rhs));
        continue;
      }
      final List<Variable> alteration = new ArrayList<>();
      for (final Body sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          final Variable a = namer.createASTChild(v);
          Γ.add(a);
          R.add(new ERule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new ERule(v, alteration.stream().map(Body::new).collect(toList())));
    }
    return new FancyEBNF(new EBNF(bnf.Σ, Γ, bnf.ε, R), bnf.headVariables, bnf.extensionHeadsMapping,
        bnf.extensionProducts, false);
  }

  public static boolean isSequenceRHS(final FancyEBNF bnf, final Variable v) {
    final List<Body> rhs = bnf.bodiesList(v);
    return rhs.size() == 1 && (rhs.get(0).size() != 1 || !bnf.isOriginalVariable(rhs.get(0).get(0)));
  }

  @SuppressWarnings("unused") public static DPDA<Named, Token, Named> cast(
      final DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
    return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
        dpda.Σ().map(Token::new).collect(toSet()), //
        new LinkedHashSet<>(dpda.Γ), //
        dpda.δs.stream() //
            .map(δ -> new DPDA.δ<Named, Token, Named>(δ.q, δ.σ == ε() ? ε() : new Token(δ.σ), δ.γ, δ.q$,
                new Word<>(δ.getΑ().stream() //
                    .map(Named.class::cast) //
                    .collect(toList())))) //
            .collect(toSet()), //
        new LinkedHashSet<>(dpda.F), //
        dpda.q0, //
        Word.of(dpda.γ0.stream().map(Named.class::cast)));
  }
}
