package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class InheritenceRule extends Rule {
  public final List<NonTerminal> subtypes;

  public InheritenceRule(final NonTerminal lhs, final List<NonTerminal> nts, final int index) {
    super(lhs, index);
    subtypes = new ArrayList<>(nts);
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.methodSignatureString() + " ::= ");
    for (NonTerminal nt : subtypes)
      sb.append(nt.methodSignatureString() + " | ");
    sb.deleteCharAt(sb.length() - 2);
    return sb.toString();
  }
  @Override public List<Symbol> getChildren() {
    return new ArrayList<>(subtypes);
  }
  public Set<DerivationRule> getAsDerivationRules() {
    HashSet<DerivationRule> $ = new HashSet<>();
    for (NonTerminal nonTerminal : subtypes)
      $.add(new DerivationRule(lhs, Arrays.asList(nonTerminal), 0));
    return $;
  }
}