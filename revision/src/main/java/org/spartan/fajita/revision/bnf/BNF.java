package org.spartan.fajita.revision.bnf;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public final class BNF {
  public final Set<Verb> verbs;
  public final Set<NonTerminal> nonTerminals;
  public final Set<NonTerminal> startSymbols;
  public final Set<DerivationRule> derivationRules;
  public final String name;

  public BNF(Set<Verb> verbs, Set<NonTerminal> nonTerminals, Set<DerivationRule> rules, Set<NonTerminal> start, String name) {
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.nonTerminals = new LinkedHashSet<>(nonTerminals);
    this.nonTerminals.add(SpecialSymbols.augmentedStartSymbol);
    this.derivationRules = new LinkedHashSet<>(rules);
    this.startSymbols = new LinkedHashSet<>(start);
    this.startSymbols
        .forEach(ss -> derivationRules.add(new DerivationRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
    this.name = name;
  }
  public BNF getSubBNF(NonTerminal startNT) {
    Set<Verb> subVerbs = new LinkedHashSet<>();
    Set<NonTerminal> subNonTerminals = new LinkedHashSet<>();
    Set<DerivationRule> subRules = new LinkedHashSet<>();
    Set<NonTerminal> subStart = new LinkedHashSet<>();
    subStart.add(startNT);
    subNonTerminals.add(startNT);
    boolean change;
    do {
      change = false;
      for (DerivationRule rule : derivationRules) {
        if (subNonTerminals.contains(rule.lhs) && subRules.add(rule)) {
          change = true;
          for (Symbol s : rule.rhs)
            if (s.isVerb())
              subVerbs.add(s.asVerb());
            else
              subNonTerminals.add(s.asNonTerminal());
        }
      }
    } while (change);
    return new BNF(subVerbs, subNonTerminals, subRules, subStart, startNT.name());
  }
  @Override public String toString() {
    // TODO Roth: set BNF toString
    return name;
  }
  public List<DerivationRule> getRulesOf(NonTerminal nt) {
    return derivationRules.stream().filter(r -> r.lhs.equals(nt)).collect(Collectors.toList());
  }
  // NOTE no equals/hashCode
}
