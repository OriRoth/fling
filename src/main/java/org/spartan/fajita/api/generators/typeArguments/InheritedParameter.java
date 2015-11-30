package org.spartan.fajita.api.generators.typeArguments;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

public final class InheritedParameter implements Comparable<InheritedParameter> {
  public final int depth;
  public final NonTerminal lhs;
  public final Verb lookahead;

  public InheritedParameter(final int depth, final NonTerminal lhs, final Verb l) {
    this.depth = depth;
    this.lhs = lhs;
    lookahead = l;
  }
  @Override public boolean equals(final Object obj) {
    if (obj == null || obj.getClass() != InheritedParameter.class)
      return false;
    if (this == obj)
      return true;
    return compareTo((InheritedParameter) obj) == 0;
  }
  @Override public int hashCode() {
    int $ = Integer.hashCode(depth);
    if (lhs != null)
      $ += lhs.hashCode();
    if (lhs != null)
      $ += lookahead.hashCode();
    return $;
  }
  @Override public int compareTo(final InheritedParameter o) {
    if (o == null)
      return -1;
    int depthComparison = depth - o.depth;
    if (depthComparison != 0)
      return depthComparison;
    int ntComparison = lhs.name().compareTo(o.lhs.name());
    if (ntComparison != 0)
      return ntComparison;
    int lookaheadComparison = lookahead.name().compareTo(o.lookahead.name());
    return lookaheadComparison;
  }
  @Override public String toString() {
    return lhs.name() + '_' + depth + '_' + lookahead.name()+"_"+lookahead.hashCode();
  }
}