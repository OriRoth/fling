package roth.ori.fling.bnf;

import java.util.ArrayList;
import java.util.List;

import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;

public class DerivationRule {
  public final Symbol head;
  private final List<GrammarElement> body;

  public DerivationRule(final Symbol head, final List<GrammarElement> body) {
    this.head = head;
    this.body = new ArrayList<>(body);
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(head.toString2() + " ::= ");
    for (GrammarElement symb : body)
      sb.append(symb.toString() + " ");
    return sb.toString();
  }
  @Override public boolean equals(final Object obj) {
    return obj != null && obj.getClass().equals(getClass()) && head.equals(((DerivationRule) obj).head)
        && body.equals(((DerivationRule) obj).body);
  }
  @Override public int hashCode() {
    return head.hashCode() + 7 * body.hashCode();
  }
  public GrammarElement get(int i) {
    return body.get(i);
  }
  public int size() {
    return body.size();
  }
  public List<GrammarElement> body() {
    return new ArrayList<>(body);
  }
  public List<GrammarElement> suffix(int from) {
    return body().subList(from, body.size());
  }
}