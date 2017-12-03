package org.spartan.fajita.revision.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.spartan.fajita.revision.rllp.Item;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class BNFAnalyzer {
  public final BNF bnf;
  private final Collection<NonTerminal> nullableSymbols;
  private final Map<Symbol, Collection<Verb>> baseFirstSets;
  private final Map<NonTerminal, Collection<Verb>> followSets;
  private final Map<NonTerminal, Map<Verb, List<Symbol>>> llClosure;

  public BNFAnalyzer(final BNF bnf) {
    this.bnf = bnf;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
    followSets = calculateFollowSets();
    llClosure = new HashMap<>();
  }
  private Collection<NonTerminal> calculateNullableSymbols() {
    Set<NonTerminal> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : bnf.derivationRules)
        if (rule.rhs.stream().allMatch(child -> nullables.contains(child)))
          moreChanges |= nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Collection<Verb>> calculateSymbolFirstSet() {
    Map<Symbol, Collection<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.startSymbols)
      $.put(nt, new LinkedHashSet<>());
    for (Verb term : bnf.verbs)
      $.put(term, new LinkedHashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.derivationRules)
        for (Symbol symbol : dRule.rhs) {
          moreChanges |= $.get(dRule.lhs).addAll($.getOrDefault(symbol, new TreeSet<>()));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  private Map<NonTerminal, Collection<Verb>> calculateFollowSets() {
    Map<NonTerminal, Collection<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.startSymbols)
      $.put(nt, new TreeSet<>());
    for (NonTerminal start : bnf.startSymbols)
      $.get(start).add(SpecialSymbols.$);
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.derivationRules)
        for (int i = 0; i < dRule.size(); i++) {
          if (!dRule.get(i).isNonTerminal())
            continue;
          Symbol subExpression[] = ruleSuffix(dRule, i + 1);
          Collection<Verb> ntFollowSet = $.get(dRule.get(i));
          moreChanges |= ntFollowSet.addAll(firstSetOf(subExpression));
          if (isNullable(subExpression))
            moreChanges |= ntFollowSet.addAll($.get(dRule.lhs));
        }
    } while (moreChanges);
    return $;
  }
  public boolean isNullable(final Symbol... expression) {
    return Arrays.asList(expression).stream().allMatch(symbol -> nullableSymbols.contains(symbol));
  }
  public boolean isSuffixNullable(final Item i) {
    return isNullable(i.rule.rhs.subList(i.dotIndex, i.rule.size()));
  }
  public Collection<Verb> firstSetOf(final Symbol... expression) {
    List<Verb> $ = new ArrayList<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Collection<Verb> firstSetOf(final List<Symbol> expression) {
    return firstSetOf(expression.toArray(new Symbol[] {}));
  }
  public Collection<Verb> firstSetOf(Item i) {
    return firstSetOf(i.rule.rhs.subList(i.dotIndex, i.rule.size()));
  }
  public Collection<Verb> followSetWO$(final NonTerminal nt) {
    final Collection<Verb> $ = new ArrayList<>(followSetOf(nt));
    $.remove(SpecialSymbols.$);
    return $;
  }
  public Collection<Verb> followSetOf(final NonTerminal nt) {
    return followSets.get(nt);
  }
  public List<Symbol> llClosure(final NonTerminal nt, final Verb v) {
    if (llClosure.containsKey(nt) && llClosure.get(nt).containsKey(v))
      return llClosure.get(nt).get(v);
    llClosure.putIfAbsent(nt, new HashMap<>());
    List<Symbol> $ = new ArrayList<>();
    if (bnf.getRulesOf(nt).stream().noneMatch(d -> firstSetOf(d.rhs).contains(v))) {
      llClosure.get(nt).put(v, null);
      return null;
    }
    NonTerminal current = nt;
    while (true) {
      DerivationRule prediction = bnf.getRulesOf(current).stream() //
          .filter(d -> firstSetOf(d.rhs).contains(v)) //
          .findAny().get();
      final List<Symbol> rhs = prediction.rhs;
      Collections.reverse(rhs);
      Symbol first = rhs.remove(rhs.size() - 1);
      $.addAll(rhs);
      if (first.isVerb()) {
        llClosure.get(nt).put(v, $);
        return $;
      }
      current = (NonTerminal) first;
    }
  }
  public boolean isNullable(List<Symbol> expr) {
    return isNullable(expr.toArray(new Symbol[] {}));
  }
  private static Symbol[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.rhs.toArray(new Symbol[] {}), index, rule.size());
  }
}
