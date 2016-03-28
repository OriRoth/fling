package org.spartan.fajita.api.bnf;

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

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;

public class BNFAnalyzer {
  public final BNF bnf;
  private final Collection<NonTerminal> nullableSymbols;
  private final Map<Symbol, Collection<Verb>> baseFirstSets;
  private final Map<NonTerminal, Collection<Verb>> followSets;

  public BNFAnalyzer(final BNF bnf) {
    this(bnf, false);
  }
  public BNFAnalyzer(final BNF bnf, boolean withFollow) {
    this.bnf = bnf;
    nullableSymbols = calculateNullableSymbols();
    baseFirstSets = calculateSymbolFirstSet();
    if (withFollow)
      followSets = calculateFollowSets();
    else
      followSets = null;
  }
  private Collection<NonTerminal> calculateNullableSymbols() {
    Set<NonTerminal> nullables = new HashSet<>();
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule rule : bnf.getRules())
        if (rule.getChildren().stream().allMatch(child -> nullables.contains(child)))
          moreChanges |= nullables.add(rule.lhs);
    } while (moreChanges);
    return nullables;
  }
  private Map<Symbol, Collection<Verb>> calculateSymbolFirstSet() {
    Map<Symbol, Collection<Verb>> $ = new HashMap<>();
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new LinkedHashSet<>());
    for (Verb term : bnf.getVerbs())
      $.put(term, new LinkedHashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (Symbol symbol : dRule.getChildren()) {
          moreChanges |= $.get(dRule.lhs).addAll($.getOrDefault(symbol, new HashSet<>()));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  public static Symbol[] ruleSuffix(DerivationRule rule, int index) {
    return Arrays.copyOfRange(rule.getChildren().toArray(new Symbol[] {}), index, rule.getChildren().size());
  }
  private Map<NonTerminal, Collection<Verb>> calculateFollowSets() {
    Map<NonTerminal, Collection<Verb>> $ = new HashMap<>();
    // initialization
    for (NonTerminal nt : bnf.getNonTerminals())
      $.put(nt, new HashSet<>());
    for (NonTerminal start : bnf.getStartSymbols())
      $.get(start).add(SpecialSymbols.$);
    // iterative step
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : bnf.getRules())
        for (int i = 0; i < dRule.getChildren().size(); i++) {
          if (!dRule.getChildren().get(i).isNonTerminal())
            continue;
          Symbol subExpression[] = ruleSuffix(dRule, i + 1);
          Collection<Verb> ntFollowSet = $.get(dRule.getChildren().get(i));
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
  public Collection<Verb> firstSetOf(final Symbol... expression) {
    List<Verb> $ = new ArrayList<>();
    // throw new IllegalArgumentException("Not handling epsilons!!");
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
    return firstSetOf(i.rule.getChildren().subList(i.dotIndex, i.rule.getChildren().size()));
  }
  public Collection<Verb> followSetOf(final NonTerminal nt) {
    if (followSets == null)
      throw new IllegalStateException("you chose no follow set at constructor");
    return followSets.get(nt);
  }
  public List<Symbol> llClosure(final NonTerminal nt, final Verb v) {
    List<Symbol> $ = new ArrayList<>();
    if (bnf.getRulesOf(nt).stream().noneMatch(d -> firstSetOf(d.getChildren()).contains(v)))
      return null;
    NonTerminal current = nt;
    while (true) {
      DerivationRule prediction = bnf.getRulesOf(current).stream() //
          .filter(d -> firstSetOf(d.getChildren()).contains(v)) //
          .findAny().get();
      final List<Symbol> rhs = prediction.getChildren();
      Collections.reverse(rhs);
      Symbol first = rhs.remove(rhs.size() - 1);
      $.addAll(rhs);
      if (first.isVerb())
        return $;
      current = (NonTerminal) first;
    }
  }
  public boolean isNullable(List<Symbol> children) {
    return isNullable(children.toArray(new Symbol[] {}));
  }
}
