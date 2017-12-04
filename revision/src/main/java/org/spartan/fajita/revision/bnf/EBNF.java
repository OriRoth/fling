package org.spartan.fajita.revision.bnf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.extendibles.Extendible;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

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
    Set<DerivationRule> rs = new LinkedHashSet<>();
    for (DerivationRule r : derivationRules) {
      List<Symbol> rhs = new LinkedList<>();
      for (Symbol s : r.getRHS()) {
        rs.addAll(s.solve(r.lhs, x -> {
          NonTerminal nt = producer.apply(x);
          return nt;
        }));
        rhs.add(s.head());
      }
      rs.add(new DerivationRule(r.lhs, rhs));
    }
    Set<NonTerminal> nts = new LinkedHashSet<>();
    Set<Verb> vs = new LinkedHashSet<>();
    Set<NonTerminal> ns = new LinkedHashSet<>();
    for (DerivationRule r : rs) {
      nts.add(r.lhs);
      for (Symbol s : r.getRHS()) {
        assert !s.isExtendible();
        if (s.isVerb()) {
          for (ParameterType t : s.asVerb().type)
            if (t instanceof NestedType && ((NestedType) t).nested.head().isNonTerminal())
              ns.add(((NestedType) t).nested.head().asNonTerminal());
          vs.add(s.asVerb());
        } else
          nts.add(s.asNonTerminal());
      }
    }
    return new BNF(vs, nts, ns, rs, startSymbols, name);
  }
  public Map<NonTerminal, List<List<Symbol>>> regularForm() {
    Map<NonTerminal, List<List<Symbol>>> $ = new HashMap<>();
    for (DerivationRule r : derivationRules) {
      $.putIfAbsent(r.lhs, new LinkedList<>());
      $.get(r.lhs).add(r.getRHS());
    }
    return $;
  }
  public Map<NonTerminal, List<List<Symbol>>> normalizedForm(Function<NonTerminal, NonTerminal> producer) {
    Map<NonTerminal, List<List<Symbol>>> rf = regularForm(), $ = new HashMap<>();
    for (Entry<NonTerminal, List<List<Symbol>>> e : rf.entrySet()) {
      NonTerminal lhs = e.getKey();
      List<List<Symbol>> rhs = e.getValue();
      if (rhs.size() <= 1 || rhs.stream().allMatch(x -> x.isEmpty() || x.size() == 1 && x.get(0).isNonTerminal())) {
        $.put(lhs, rhs);
        continue;
      }
      $.put(lhs, new LinkedList<>());
      for (int i = 0; i < rhs.size(); ++i) {
        List<Symbol> l = new LinkedList<>();
        NonTerminal nt = producer.apply(lhs);
        l.add(nt);
        $.get(lhs).add(l);
        $.put(nt, new LinkedList<>());
        if (!rhs.get(i).isEmpty())
          $.get(nt).add(rhs.get(i));
      }
    }
    return $;
  }
}
