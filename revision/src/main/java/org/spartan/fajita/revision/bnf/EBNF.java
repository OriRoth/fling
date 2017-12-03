package org.spartan.fajita.revision.bnf;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.symbols.Extendible;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public final class EBNF {
  public final Set<Verb> verbs;
  public final Set<NonTerminal> nonTerminals;
  public final Set<Extendible> extendibles;
  public final Set<NonTerminal> startSymbols;
  public final Set<DerivationRule> derivationRules;
  public final String name;

  public EBNF(Set<Verb> verbs, Set<NonTerminal> nonTerminals, Set<Extendible> extendibles, Set<DerivationRule> rules,
      Set<NonTerminal> start, String name) {
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.nonTerminals = new LinkedHashSet<>(nonTerminals);
    this.nonTerminals.add(SpecialSymbols.augmentedStartSymbol);
    this.extendibles = new LinkedHashSet<>(extendibles);
    this.derivationRules = new LinkedHashSet<>(rules);
    this.startSymbols = new LinkedHashSet<>(start);
    this.startSymbols
        .forEach(ss -> derivationRules.add(new DerivationRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
    this.name = name;
  }
  @Override public String toString() {
    // TODO Roth: set EBNF toString
    return name;
  }
  public List<DerivationRule> getRulesOf(NonTerminal nt) {
    return derivationRules.stream().filter(r -> r.lhs.equals(nt)).collect(Collectors.toList());
  }
  // NOTE no equals/hashCode
  public BNF toBNF(Function<NonTerminal, NonTerminal> producer) {
    Set<NonTerminal> nts = new LinkedHashSet<>(nonTerminals);
    Set<DerivationRule> rs = new LinkedHashSet<>();
    for (DerivationRule r : derivationRules) {
      List<Symbol> rhs = new LinkedList<>();
      for (Symbol s : r.rhs) {
        rs.addAll(s.solve(r.lhs, x -> {
          NonTerminal nt = producer.apply(x);
          nts.add(nt);
          return nt;
        }));
        rhs.add(s.head());
      }
      rs.add(new DerivationRule(r.lhs, rhs));
    }
    return new BNF(verbs, nts, rs, startSymbols, name);
  }
}
