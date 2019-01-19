package fling.sententials;

import java.util.List;

public class DerivationRule {
  public final Variable lhs;
  public final List<SententialForm> rhs;

  public DerivationRule(Variable lhs, List<SententialForm> rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }
  public Variable lhs() {
    return lhs;
  }
  public List<SententialForm> rhs() {
    return rhs;
  }
  @Override public int hashCode() {
    int $ = 1;
    $ = $ * 31 + lhs.hashCode();
    $ = $ * 31 + rhs.hashCode();
    return $;
  }
  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DerivationRule))
      return false;
    DerivationRule other = (DerivationRule) o;
    return lhs.equals(other.lhs) && rhs.equals(other.rhs);
  }
}
