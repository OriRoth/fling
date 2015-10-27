package org.spartan.fajita.api.bnf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public final class BNF {
  private final String apiName;
  private final Set<Terminal> terminals;
  private final Set<NonTerminal> nonterminals;
  private final Collection<DerivationRule> derivationRules;
  private final NonTerminal augmentedStartSymbol;
  private final Map<Symbol, Set<Terminal>> baseFirstSets;
  private final Map<NonTerminal, Set<Terminal>> followSets;

  BNF(final BNFBuilder builder) {
    apiName = builder.getApiName();
    terminals = builder.getTerminals();
    nonterminals = builder.getNonTerminals();
    derivationRules = builder.getRules();
    augmentedStartSymbol = BNFBuilder.getAugmentedStartSymbol();
    baseFirstSets = calculateSymbolFirstSet();
    followSets = calculateFollowSets();
  }
  private Map<Symbol, Set<Terminal>> calculateSymbolFirstSet() {
    Map<Symbol, Set<Terminal>> $ = new HashMap<>();
    for (NonTerminal nt : getNonTerminals())
      $.put(nt, new HashSet<>());
    for (Terminal term : getTerminals())
      $.put(term, new HashSet<>(Arrays.asList(term)));
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : getRules())
        for (Symbol symbol : dRule.getChildren()) {
          moreChanges |= $.get(dRule.lhs).addAll($.get(symbol));
          if (!isNullable(symbol))
            break;
        }
    } while (moreChanges);
    return $;
  }
  private Map<NonTerminal, Set<Terminal>> calculateFollowSets() {
    Map<NonTerminal, Set<Terminal>> $ = new HashMap<>();
    // initialization
    for (NonTerminal nt : getNonTerminals())
      $.put(nt, new HashSet<>());
    $.get(augmentedStartSymbol).add(Terminal.$);
    // iterative step
    boolean moreChanges;
    do {
      moreChanges = false;
      for (DerivationRule dRule : getRules())
        for (int i = 0; i < dRule.getChildren().size(); i++) {
          if (dRule.getChildren().get(i).isTerminal())
            continue;
          Symbol subExpression[];
          if (i != dRule.getChildren().size() - 1)
            subExpression = Arrays.copyOfRange(dRule.getChildren().toArray(), i + 1, dRule.getChildren().size(), Symbol[].class);
          else
            subExpression = new Symbol[] {};
          moreChanges |= $.get(dRule.getChildren().get(i)).addAll(firstSetOf(subExpression));
          if (isNullable(subExpression) || subExpression.length == 0)
            moreChanges |= $.get(dRule.getChildren().get(i)).addAll($.get(dRule.lhs));
        }
    } while (moreChanges);
    return $;
  }
  public Set<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  public Set<Terminal> getTerminals() {
    return terminals;
  }
  public NonTerminal getAugmentedStartSymbol() {
    return augmentedStartSymbol;
  }
  public String getApiName() {
    return apiName;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder() //
        .append("Terminals set: " + terminals + "\n") //
        .append("Nonterminals set: " + nonterminals + "\n") //
        .append("Rules for " + getApiName() + ":\n");
    for (Rule rule : getRules())
      sb.append(rule.toString() + "\n");
    return sb.toString();
  }
  public Collection<DerivationRule> getRules() {
    return derivationRules;
  }
  @SuppressWarnings({ "static-method", "unused" }) public boolean isNullable(final Symbol... expression) {
    return false;
  }
  public Set<Terminal> firstSetOf(final Symbol... expression) {
    HashSet<Terminal> $ = new HashSet<>();
    for (Symbol symbol : expression) {
      $.addAll(baseFirstSets.get(symbol));
      if (!isNullable(symbol))
        break;
    }
    return $;
  }
  public Set<Terminal> followSetOf(final NonTerminal nt) {
    return followSets.get(nt);
  }
  public Set<Type> getOverloadsOf(final Terminal t) {
    return getTerminals().stream().filter(terminal -> terminal.name().equals(t.name())).map(terminal -> terminal.type())
        .collect(Collectors.toSet());
  }
}
