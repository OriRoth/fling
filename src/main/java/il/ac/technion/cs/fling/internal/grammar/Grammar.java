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

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
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

  public Grammar(FancyEBNF ebnf, Namer namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    this.bnf = getBNF(ebnf);
    this.normalizedEBNF = normalize(ebnf, namer);
    this.normalizedBNF = getBNF(normalizedEBNF);
    subBNFs = new LinkedHashMap<>();
    for (Variable head : bnf.headVariables)
      subBNFs.put(head, computeSubBNF(head));
  }

  public abstract DPDA<Named, Token, Named> buildAutomaton(FancyEBNF bnf);

  // TODO compute lazily.
  public DPDA<Named, Token, Named> toDPDA() {
    return buildAutomaton(bnf);
  }

  private FancyEBNF getBNF(FancyEBNF ebnf) {
    Set<Variable> Γ = new LinkedHashSet<>(ebnf.Γ);
    Set<ERule> R = new LinkedHashSet<>();
    Map<Variable, Quantifier> extensionHeadsMapping = new LinkedHashMap<>();
    Set<Variable> extensionProducts = new LinkedHashSet<>();
    for (ERule r : ebnf.R) {
      List<Body> rhs = new ArrayList<>();
      for (Body b : r.bodiesList()) {
        List<Component> cs = new ArrayList<>();
        for (Component c : b) {
          if (!c.isQuantifier()) {
            cs.add(c);
            continue;
          }
          Quantifier q = c.asQuantifier();
          Variable head = q.expand(namer, extensionProducts::add, R::add);
          extensionHeadsMapping.put(head, q);
          cs.add(head);
        }
        rhs.add(new Body(cs));
      }
      R.add(new ERule(r.variable, rhs));
    }
    Γ.addAll(extensionProducts);
    return new FancyEBNF(new EBNF(ebnf.Σ, Γ, ebnf.ε, R), ebnf.headVariables, extensionHeadsMapping, extensionProducts, false);
  }

  public FancyEBNF getSubBNF(Variable variable) {
    return subBNFs.get(variable);
  }

  private FancyEBNF computeSubBNF(Variable v) {
    final Set<Token> Σ = new LinkedHashSet<>();
    final Set<Variable> V = new LinkedHashSet<>();
    V.add(v);
    final Set<ERule> rs = new LinkedHashSet<>();
    for (boolean more = true; more;) {
      more = false;
      for (ERule r : bnf.R)
        if (!rs.contains(r) && V.contains(r.variable)) {
          more = true;
          rs.add(r);
          r.variables().forEachOrdered(V::add);
          r.tokens().forEachOrdered(Σ::add);
        }
    }
    return new FancyEBNF(new EBNF(Σ, V, v, rs), null, null, null, true);
  }

  private static FancyEBNF normalize(FancyEBNF bnf, Namer namer) {
    Set<Variable> V = new LinkedHashSet<>(bnf.Γ);
    Set<ERule> R = new LinkedHashSet<>();
    for (Variable v : bnf.Γ) {
      List<Body> rhs = bnf.bodiesList(v);
      assert rhs.size() > 0: v.toString() + " in: " + bnf;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new ERule(v, rhs));
        continue;
      }
      List<Variable> alteration = new ArrayList<>();
      for (Body sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          Variable a = namer.createASTChild(v);
          V.add(a);
          R.add(new ERule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new ERule(v, alteration.stream().map(a -> new Body(a)).collect(toList())));
    }
    return new FancyEBNF(new EBNF(bnf.Σ, V, bnf.ε, R), bnf.headVariables, bnf.extensionHeadsMapping, bnf.extensionProducts,
        false);
  }

  public static boolean isSequenceRHS(FancyEBNF bnf, Variable v) {
    List<Body> rhs = bnf.bodiesList(v);
    return rhs.size() == 1 && (rhs.get(0).size() != 1 || !bnf.isOriginalVariable(rhs.get(0).get(0)));
  }

  @SuppressWarnings({ "null", "unused" }) public static DPDA<Named, Token, Named> cast(
      DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
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
