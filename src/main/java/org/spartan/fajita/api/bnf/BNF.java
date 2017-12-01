package org.spartan.fajita.api.bnf;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.ASCII;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

public final class BNF {
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  private final List<NonTerminal> startSymbols;
  private final List<DerivationRule> derivationRules;
  private final List<DerivationRule> classDerivationRules;
  private String name;

  public BNF(Collection<Verb> verbs, Collection<NonTerminal> nonTerminals, //
      Collection<DerivationRule> rules, Collection<NonTerminal> start, String name) {
    this.name = toCamelCase(name);
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.nonterminals = new ArrayList<>(nonTerminals);
    this.nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    this.derivationRules = new ArrayList<>(rules);
    this.classDerivationRules = null;
    this.startSymbols = new ArrayList<>(start);
    this.startSymbols
        .forEach(ss -> derivationRules.add(new DerivationRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
  }
  public BNF(Collection<Verb> verbs, Collection<NonTerminal> nonTerminals, //
      Collection<DerivationRule> rules, Collection<DerivationRule> classRules, Collection<NonTerminal> start, String name) {
    this.name = toCamelCase(name);
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.nonterminals = new ArrayList<>(nonTerminals);
    this.nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    this.derivationRules = new ArrayList<>(rules);
    this.classDerivationRules = new ArrayList<>(classRules);
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
    return toString(ASCII);
  }
  public String toString(BNFRenderer renderer) {
    return render(renderer);
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
  public Map<NonTerminal, List<List<Symbol>>> regularForm(boolean classRules) {
    Map<NonTerminal, List<List<Symbol>>> $ = new HashMap<>();
    for (DerivationRule r : classRules ? classDerivationRules : derivationRules) {
      $.putIfAbsent(r.lhs, new LinkedList<>());
      $.get(r.lhs).add(r.getRHS());
    }
    return $;
  }
  public Map<NonTerminal, List<List<Symbol>>> normalizedForm(boolean classRules) {
    Map<NonTerminal, List<List<Symbol>>> rf = regularForm(classRules), $ = new HashMap<>();
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
        NonTerminal nt = nonTerminal(lhs.name() + "$" + (i + 1));
        l.add(nt);
        $.get(lhs).add(l);
        $.put(nt, new LinkedList<>());
        if (!rhs.get(i).isEmpty())
          $.get(nt).add(rhs.get(i));
      }
    }
    return $;
  }
  public String render(BNFRenderer renderer) {
    Map<NonTerminal, List<List<Symbol>>> n = renderer
        .sortRules(renderer.normalizedForm() ? normalizedForm(renderer.classRules()) : regularForm(renderer.classRules()));
    StringBuilder $ = new StringBuilder();
    $.append(renderer.grammarAnte(this));
    for (Entry<NonTerminal, List<List<Symbol>>> r : n.entrySet()) {
      NonTerminal lhs = r.getKey();
      List<List<Symbol>> rhs = r.getValue();
      $.append(renderer.ruleAnte(lhs, rhs));
      $.append(renderer.headAnte(lhs));
      $.append(lhs.name());
      $.append(renderer.headPost(lhs));
      if (renderer.visitBody(lhs, rhs)) {
        $.append(renderer.bodyAnte(rhs));
        boolean clauseBetween = false;
        for (List<Symbol> clause : r.getValue()) {
          if (clauseBetween)
            $.append(renderer.clauseBetween());
          clauseBetween = true;
          $.append(renderer.clauseAnte(clause));
          if (clause.isEmpty())
            $.append(renderer.epsilonAnte()).append("ε").append(renderer.epsilonPost());
          else {
            boolean termBetween = false;
            for (Symbol s : clause) {
              if (termBetween)
                $.append(renderer.termBetween());
              termBetween = true;
              if (s.isVerb() && !(s instanceof EVerb) && renderer.visitTerminal((Verb) s))
                $.append(renderer.terminalAnte((Terminal) s)).append(s.name()).append(renderer.terminalPost((Terminal) s));
              else if (s instanceof NonTerminal)
                $.append(renderer.symbolAnte((NonTerminal) s)).append(s.name()).append(renderer.symbolPost((NonTerminal) s));
              else
                $.append(renderer.special(s));
            }
          }
          $.append(renderer.clausePost(clause));
        }
        $.append(renderer.bodyPost(rhs));
      }
      $.append(renderer.rulePost(lhs, rhs));
    }
    $.append(renderer.grammarPost(this));
    return $.toString();
  }
  public static NonTerminal nonTerminal(String name) {
    return new NonTerminal() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public boolean equals(Object obj) {
        return obj != null && name.equals(obj.toString());
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
    };
  }
}
