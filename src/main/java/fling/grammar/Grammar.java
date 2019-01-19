package fling.grammar;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import fling.automata.DPDA;
import fling.sententials.Constants;
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
    Set<Variable> startVariables = new LinkedHashSet<>(
        bnf.rhs(Constants.S).stream().map(sf -> sf.get(0).asVariable()).collect(Collectors.toSet()));
    bnf.V.forEach(v -> v.expand(namer, nv -> {
      V.add(nv);
      if (startVariables.contains(v))
        startVariables.add(nv);
    }, R::add));
    return new BNF(bnf.Σ, V, R, startVariables);
  }
}
