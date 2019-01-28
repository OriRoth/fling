package fling.grammar;

import java.util.LinkedHashSet;
import java.util.Set;

import fling.automata.DPDA;
import fling.sententials.DerivationRule;
import fling.sententials.Variable;

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
    Set<Variable> V = new LinkedHashSet<>();
    Set<DerivationRule> R = new LinkedHashSet<>(ebnf.R);
    Set<Variable> startVariables = new LinkedHashSet<>(ebnf.startVariables);
    ebnf.V.forEach(v -> V.add(v.abbreviate(namer, V::add, R::add)));
    return new BNF(ebnf.Σ, V, R, startVariables, false);
  }
  private BNF getNormalizedBNF() {
    return null;
  }
}
