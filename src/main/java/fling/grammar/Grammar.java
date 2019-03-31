package fling.grammar;

import static fling.grammar.sententials.Alphabet.ε;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fling.automata.DPDA;
import fling.compiler.Namer;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.sententials.Word;

public abstract class Grammar {
  public final BNF ebnf;
  public final Namer namer;
  public final BNF bnf;
  public final BNF normalizedBNF;
  private final Map<Variable, BNF> subBNFs;

  public Grammar(BNF ebnf, Namer namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    this.bnf = getBNF();
    this.normalizedBNF = getNormalizedBNF();
    subBNFs = new LinkedHashMap<>();
    for (Variable head : bnf.headVariables)
      subBNFs.put(head, computeSubBNF(head));
  }
  public abstract DPDA<Named, Verb, Named> buildAutomaton(BNF bnf);
  // TODO compute lazily.
  public DPDA<Named, Verb, Named> toDPDA() {
    return buildAutomaton(bnf);
  }
  private BNF getBNF() {
    return new BNF(ebnf.Σ, ebnf.V, ebnf.R, ebnf.startVariable, ebnf.headVariables, false);
  }
  public BNF getSubBNF(Variable variable) {
    return subBNFs.get(variable);
  }
  private BNF computeSubBNF(Variable head) {
    Set<Verb> Σ = new LinkedHashSet<>();
    Set<Variable> V = new LinkedHashSet<>();
    V.add(head);
    Set<DerivationRule> R = new LinkedHashSet<>();
    int previousSize = -1;
    for (; previousSize < R.size();) {
      previousSize = R.size();
      for (DerivationRule rule : bnf.R)
        if (!R.contains(rule) && V.contains(rule.lhs)) {
          R.add(rule);
          for (SententialForm sf : rule.rhs)
            for (Symbol symbol : sf)
              if (symbol.isVariable())
                V.add(symbol.asVariable());
              else if (symbol.isVerb())
                Σ.add(symbol.asVerb());
        }
    }
    return new BNF(Σ, V, R, head, null, true);
  }
  private BNF getNormalizedBNF() {
    Set<Variable> V = new LinkedHashSet<>(ebnf.V);
    Set<DerivationRule> R = new LinkedHashSet<>();
    for (Variable v : ebnf.V) {
      List<SententialForm> rhs = ebnf.rhs(v);
      assert rhs.size() > 0;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new DerivationRule(v, rhs));
        continue;
      }
      List<Variable> alteration = new ArrayList<>();
      for (SententialForm sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(s -> s.isVariable()))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          Variable a = namer.createChild(v);
          V.add(a);
          R.add(new DerivationRule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new DerivationRule(v, alteration.stream().map(a -> new SententialForm(a)).collect(toList())));
    }
    return new BNF(ebnf.Σ, V, R, ebnf.startVariable, ebnf.headVariables, false);
  }
  public static boolean isSequenceRHS(List<SententialForm> rhs) {
    return rhs.size() == 1 && (rhs.get(0).size() != 1 || !rhs.get(0).get(0).isVariable());
  }
  @SuppressWarnings({ "null", "unused" }) public static DPDA<Named, Verb, Named> cast(
      DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
    return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
        dpda.Σ().map(Verb::new).collect(toSet()), //
        new LinkedHashSet<>(dpda.Γ), //
        dpda.δs.stream() //
            .map(δ -> new DPDA.δ<Named, Verb, Named>(δ.q, δ.σ == ε() ? ε() : new Verb(δ.σ), δ.γ, δ.q$, new Word<>(δ.getΑ().stream() //
                .map(Named.class::cast) //
                .collect(toList())))) //
            .collect(toSet()), //
        new LinkedHashSet<>(dpda.F), //
        dpda.q0, //
        new Word<>(dpda.γ0.stream() //
            .map(Named.class::cast) //
            .collect(toList())));
  }
}
