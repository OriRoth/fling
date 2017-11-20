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

public final class BNF {
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  private final List<NonTerminal> startSymbols;
  private final List<DerivationRule> derivationRules;
  private String name;

  public BNF(Collection<Verb> verbs, Collection<NonTerminal> nonTerminals, //
      Collection<DerivationRule> rules, Collection<NonTerminal> start, String name) {
    this.name = toCamelCase(name);
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
          for (Symbol s : rule.getRHS())
            if (s.isVerb())
              subVerbs.add((Verb) s);
            else
              subNonTerminals.add((NonTerminal) s);
        }
      }
    } while (change);
    return new BNF(subVerbs, subNonTerminals, subRules, subStart, startNT.name());
  }
  public List<NonTerminal> getStartSymbols() {
    return startSymbols;
  }
  public String getApiName() {
    return name;
  }
  public static String toCamelCase(String name) {
    boolean startOfWord = true;
    String $ = "";
    for (char c : name.toCharArray()) {
      if (startOfWord) {
        startOfWord = false;
        $ += Character.toUpperCase(c);
      } else if (c == '_')
        startOfWord = true;
      else
        $ += Character.toLowerCase(c);
    }
    return $;
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
  public String render(BNFRenderer renderer) {
    StringBuilder $ = new StringBuilder();
    $.append(renderer.grammarAnte());
    for (NonTerminal nt : startSymbols)
      $.append(renderer.startSymbolAnte()).append(nt.name()).append(renderer.startSymbolPost());
    for (DerivationRule r : derivationRules) {
      $.append(renderer.ruleAnte());
      $.append(renderer.headAnte());
      $.append(renderer.symbolAnte());
      $.append(r.lhs.name());
      $.append(renderer.symbolPost());
      $.append(renderer.headPost());
      $.append(renderer.bodyAnte());
      List<Symbol> rhs = r.getRHS();
      if (rhs.isEmpty())
        $.append(renderer.termAnte()).append("ε").append(renderer.termPost());
      else
        for (Symbol s : rhs) {
          $.append(renderer.termAnte());
          if (s.isVerb())
            $.append(renderer.terminalAnte()).append(s.name()).append(renderer.terminalPost());
          else if (s instanceof NonTerminal)
            $.append(renderer.terminalAnte()).append(s.name()).append(renderer.terminalPost());
          $.append(renderer.termPost());
        }
      $.append(renderer.bodyPost());
      $.append(renderer.rulePost());
    }
    $.append(renderer.grammarPost());
    return $.toString();
  }
}
