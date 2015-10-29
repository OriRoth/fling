package org.spartan.fajita.api.bnf;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public final class BNF {
  private final String apiName;
  private final Set<Terminal> terminals;
  private final Set<NonTerminal> nonterminals;
  private final Collection<DerivationRule> derivationRules;

  BNF(final BNFBuilder builder) {
    apiName = builder.getApiName();
    terminals = builder.getTerminals();
    nonterminals = builder.getNonTerminals();
    derivationRules = builder.getRules();
  }
  public Set<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  public Set<Terminal> getTerminals() {
    return terminals;
  }
  public String getApiName() {
    return apiName;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder() //
        .append("Terminals set: " + getTerminals() + "\n") //
        .append("Nonterminals set: " + getNonTerminals() + "\n") //
        .append("Rules for " + getApiName() + ":\n");
    for (Rule rule : getRules())
      sb.append(rule.toString() + "\n");
    return sb.toString();
  }
  public Collection<DerivationRule> getRules() {
    return derivationRules;
  }
  public Set<Type> getOverloadsOf(final Terminal t) {
    return getTerminals().stream().filter(terminal -> terminal.name().equals(t.name())).map(terminal -> terminal.type())
        .collect(Collectors.toSet());
  }
}
