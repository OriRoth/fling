package fling.grammar;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fling.automata.DPDA;
import fling.compiler.Namer;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;

public abstract class Grammar {
  public final BNF ebnf;
  public final Namer namer;
  public final BNF bnf;
  public final BNF normalizedBNF;

  public Grammar(BNF ebnf, Namer namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    this.bnf = getBNF();
    this.normalizedBNF = getNormalizedBNF();
  }
  public abstract DPDA<?, ?, ?> toDPDA();
  private BNF getBNF() {
    Set<Variable> V = new LinkedHashSet<>(ebnf.V);
    Set<DerivationRule> R = new LinkedHashSet<>();
    for (DerivationRule r : ebnf.R) {
      List<SententialForm> rhs = new ArrayList<>();
      for (SententialForm sf : r.rhs()) {
        List<Symbol> abbreviatedSymbols = new ArrayList<>();
        for (Symbol s : sf)
          abbreviatedSymbols.add(namer.abbreviate(s, V::add, R::add));
        rhs.add(new SententialForm(abbreviatedSymbols));
      }
      R.add(new DerivationRule(r.lhs, rhs));
    }
    return new BNF(ebnf.Σ, V, R, ebnf.startVariables, false);
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
    return new BNF(ebnf.Σ, V, R, ebnf.startVariables, false);
  }
}
