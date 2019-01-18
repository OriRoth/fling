package roth.ori.fling.bnf;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import roth.ori.fling.bnf.Rule;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Verb;

public final class BNF {
  public final Set<Verb> verbs;
  public final Set<Symbol> symbols;
  public final Set<Symbol> starts;
  public final Set<Symbol> nestedsymbols;
  private final Set<Rule> rules;
  public final String name;
  public boolean isSubBNF;
  public EBNF origin;

  public BNF(Set<Verb> verbs, Set<Symbol> symbols, Set<Symbol> nestedsymbols, Set<Rule> rules,
      Set<Symbol> start, String name) {
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.symbols = new LinkedHashSet<>(symbols);
    this.symbols.add(SpecialSymbols.augmentedStartSymbol);
    this.nestedsymbols = new LinkedHashSet<>(nestedsymbols);
    this.rules = new LinkedHashSet<>(rules);
    this.starts = new LinkedHashSet<>(start);
    this.starts
        .forEach(ss -> this.rules.add(new Rule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
    this.name = name;
    this.isSubBNF = false;
  }
  public BNF getSubBNF(Symbol startNT) {
    Set<Verb> subVerbs = new LinkedHashSet<>();
    Set<Symbol> subsymbols = new LinkedHashSet<>();
    Set<Rule> subRules = new LinkedHashSet<>();
    Set<Symbol> subStart = new LinkedHashSet<>();
    subStart.add(startNT);
    subsymbols.add(startNT);
    boolean change;
    do {
      change = false;
      for (Rule rule : rules) {
        if (subsymbols.contains(rule.head) && subRules.add(rule)) {
          change = true;
          for (GrammarElement s : rule.body())
            if (s.isVerb())
              subVerbs.add(s.asVerb());
            else
              subsymbols.add(s.asNonTerminal());
        }
      }
    } while (change);
    // NOTE sub BNF nested non terminals are invalid
    BNF $ = new BNF(subVerbs, subsymbols, new LinkedHashSet<>(), subRules, subStart, startNT.name());
    $.isSubBNF = true;
    $.origin = origin;
    return $;
  }
  @Override public String toString() {
    // TODO Roth: set BNF toString
    return name;
  }
  public List<Rule> getRulesOf(Symbol nt) {
    return rules.stream().filter(r -> r.head.equals(nt)).collect(Collectors.toList());
  }
  // NOTE no equals/hashCode
  public Set<Rule> rules() {
    return new LinkedHashSet<>(rules);
  }
}
