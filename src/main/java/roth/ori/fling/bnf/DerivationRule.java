package roth.ori.fling.bnf;

import java.util.ArrayList;
import java.util.List;

import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.GrammarElement;

public class DerivationRule {
  public final NonTerminal lhs;
  private final List<GrammarElement> rhs;

  public DerivationRule(final NonTerminal lhs, final List<GrammarElement> rhs) {
    this.lhs = lhs;
    this.rhs = new ArrayList<>(rhs);
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
    for (GrammarElement symb : rhs)
      sb.append(symb.toString() + " ");
    return sb.toString();
  }
  @Override public boolean equals(final Object obj) {
    return obj != null && obj.getClass().equals(getClass()) && lhs.equals(((DerivationRule) obj).lhs)
        && rhs.equals(((DerivationRule) obj).rhs);
  }
  @Override public int hashCode() {
    return lhs.hashCode() + 7 * rhs.hashCode();
  }
  public GrammarElement get(int i) {
    return rhs.get(i);
  }
  public int size() {
    return rhs.size();
  }
  public List<GrammarElement> getRHS() {
    return new ArrayList<>(rhs);
  }
  public List<GrammarElement> getRHSSuffix(int from) {
    return getRHS().subList(from, rhs.size());
  }
}