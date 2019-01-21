package fling.grammar;

import java.util.LinkedHashSet;
import java.util.Set;

import fling.automata.DPDA;
import fling.sententials.DerivationRule;
import fling.sententials.Variable;

public abstract class Grammar {
  public final BNF bnf;
  public final Namer namer;
  public final BNF standardizedBnf;

  public Grammar(BNF bnf, Namer namer) {
    this.bnf = bnf;
    this.namer = namer;
    this.standardizedBnf = getStandardizedBNF();
  }
  public abstract DPDA<?, ?, ?> toDPDA();
  private BNF getStandardizedBNF() {
    Set<Variable> V = new LinkedHashSet<>(bnf.V);
    Set<DerivationRule> R = new LinkedHashSet<>(bnf.R);
    Set<Variable> startVariables = new LinkedHashSet<>(bnf.startVariables);
    bnf.V.forEach(v -> v.expand(namer, V::add, R::add));
    return new BNF(bnf.Σ, V, R, startVariables, false);
  }
}
