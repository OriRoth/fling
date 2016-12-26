package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Namer;

public final class BNF {
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  private final List<NonTerminal> startSymbols;
  private final List<DerivationRule> derivationRules;

  public BNF(Collection<Verb> verbs, Collection<NonTerminal> nonTerminals, //
      Collection<DerivationRule> rules, Collection<NonTerminal> start) {
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.nonterminals = new ArrayList<>(nonTerminals);
    this.nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    this.derivationRules = new ArrayList<>(rules);
    this.startSymbols = new ArrayList<>(start);
    this.startSymbols
        .forEach(ss -> derivationRules.add(new DerivationRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
  }
  public List<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  public Set<Verb> getVerbs() {
    return verbs;
  }
  public BNF getSubBNF(NonTerminal startNT) {
    Set<Verb> subVerbs = new LinkedHashSet<>();
    Set<NonTerminal> subNonTerminals = new LinkedHashSet<>();
    Set<DerivationRule> subRules = new LinkedHashSet<>();
    List<NonTerminal> subStart = Arrays.asList(startNT);
    subNonTerminals.add(startNT);
    boolean change;
    do {
      change = false;
      for (DerivationRule rule : getRules()) {
        if (subNonTerminals.contains(rule.lhs) && subRules.add(rule)) {
          change = true;
          for (Symbol s : rule.getChildren())
            if (s.isVerb())
              subVerbs.add((Verb) s);
            else
              subNonTerminals.add((NonTerminal) s);
        }
      }
    } while (change);
    return new BNF(subVerbs, subNonTerminals, subRules, subStart);
  }
  public List<NonTerminal> getStartSymbols() {
    return startSymbols;
  }
  public String getApiName() {
    return Namer.getApiName(startSymbols.get(0));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder() //
        .append("Verbs set: " + getVerbs() + "\n") //
        .append("Nonterminals set: " + getNonTerminals() + "\n") //
        .append("Rules for " + getApiName() + ":\n");
    for (DerivationRule rule : getRules())
      sb.append(rule.toString() + "\n");
    return sb.toString();
  }
  public List<DerivationRule> getRules() {
    return derivationRules;
  }
  public List<DerivationRule> getRulesOf(NonTerminal nt) {
    return getRules().stream().filter(r -> r.lhs.equals(nt)).collect(Collectors.toList());
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((derivationRules == null) ? 0 : derivationRules.hashCode());
    result = prime * result + ((nonterminals == null) ? 0 : nonterminals.hashCode());
    result = prime * result + ((startSymbols == null) ? 0 : startSymbols.hashCode());
    result = prime * result + ((verbs == null) ? 0 : verbs.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BNF other = (BNF) obj;
    if (derivationRules == null) {
      if (other.derivationRules != null)
        return false;
    } else if (!derivationRules.equals(other.derivationRules))
      return false;
    if (nonterminals == null) {
      if (other.nonterminals != null)
        return false;
    } else if (!nonterminals.equals(other.nonterminals))
      return false;
    if (startSymbols == null) {
      if (other.startSymbols != null)
        return false;
    } else if (!startSymbols.equals(other.startSymbols))
      return false;
    if (verbs == null) {
      if (other.verbs != null)
        return false;
    } else if (!verbs.equals(other.verbs))
      return false;
    return true;
  }
}
