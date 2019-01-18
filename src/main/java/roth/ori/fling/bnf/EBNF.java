package roth.ori.fling.bnf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import roth.ori.fling.api.Fling.FlingProducer;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.extendibles.Extendible;
import roth.ori.fling.symbols.types.NestedType;
import roth.ori.fling.symbols.types.ParameterType;
import roth.ori.fling.symbols.types.VarArgs;

public final class EBNF {
  public final Set<Verb> verbs;
  public final Set<Symbol> symbols;
  public final Set<Extendible> extendibles;
  public final Set<Symbol> startSymbols;
  private final Set<DerivationRule> derivationRules;
  public final String name;
  public boolean isSubEBNF;
  // Valid only after toBNF
  public Map<GrammarElement, GrammarElement> nestedSymbolsMapping;
  public GrammarElement subHead;
  private FlingProducer beforeSolution;
  private FlingProducer afterSolution;
  private BNF bnf;

  public EBNF(Set<Verb> verbs, Set<Symbol> symbols, Set<Extendible> extendibles, Set<DerivationRule> rules,
      Set<Symbol> start, String name) {
    this.verbs = new LinkedHashSet<>(verbs);
    this.verbs.add(SpecialSymbols.$);
    this.symbols = new LinkedHashSet<>(symbols);
    this.symbols.add(SpecialSymbols.augmentedStartSymbol);
    this.extendibles = new LinkedHashSet<>(extendibles);
    this.derivationRules = new LinkedHashSet<>(rules);
    this.startSymbols = new LinkedHashSet<>(start);
    this.startSymbols
        .forEach(ss -> derivationRules.add(new DerivationRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(ss))));
    this.name = name;
    this.isSubEBNF = false;
  }
  @Override public String toString() {
    // TODO Roth: set EBNF toString
    return name;
  }
  public List<DerivationRule> getRulesOf(Symbol nt) {
    return derivationRules.stream().filter(r -> r.head.equals(nt)).collect(Collectors.toList());
  }
  // NOTE no equals/hashCode
  // TODO Roth: use normalized form here
  public BNF toBNF(FlingProducer producer) {
    if (bnf != null)
      return bnf;
    beforeSolution = producer.clone();
    Set<DerivationRule> rs = new LinkedHashSet<>();
    nestedSymbolsMapping = new LinkedHashMap<>();
    for (DerivationRule r : derivationRules) {
      List<GrammarElement> rhs = new LinkedList<>();
      for (GrammarElement s : r.body()) {
        rs.addAll(s.solve(r.head, producer));
        rhs.add(s.head());
        if (s.isVerb())
          for (ParameterType t : s.asVerb().type)
            if (t instanceof NestedType) {
              GrammarElement nested = ((NestedType) t).nested;
              nestedSymbolsMapping.put(nested.head().asNonTerminal(), nested);
            } else if (t instanceof VarArgs) {
              Symbol nested = ((VarArgs) t).nt;
              nestedSymbolsMapping.put(nested, nested);
            }
      }
      rs.add(new DerivationRule(r.head, rhs));
    }
    Set<Symbol> nts = new LinkedHashSet<>();
    Set<Verb> vs = new LinkedHashSet<>();
    Set<Symbol> ns = new LinkedHashSet<>();
    for (DerivationRule r : rs) {
      nts.add(r.head);
      for (GrammarElement s : r.body()) {
        assert !s.isExtendible();
        if (s.isVerb()) {
          for (ParameterType t : s.asVerb().type)
            if (t instanceof NestedType && ((NestedType) t).nested.head().isNonTerminal())
              ns.add(((NestedType) t).nested.head().asNonTerminal());
            else if (t instanceof VarArgs && ((VarArgs) t).nt != null)
              ns.add(((VarArgs) t).nt);
          vs.add(s.asVerb());
        } else
          nts.add(s.asNonTerminal());
      }
    }
    BNF $ = new BNF(vs, nts, ns, rs, startSymbols, name);
    $.origin = this;
    afterSolution = producer.clone();
    return bnf = $;
  }
  public Map<Symbol, Set<List<GrammarElement>>> regularForm() {
    Map<Symbol, Set<List<GrammarElement>>> $ = new LinkedHashMap<>();
    for (DerivationRule r : derivationRules) {
      $.putIfAbsent(r.head, new LinkedHashSet<>());
      $.get(r.head).add(r.body());
    }
    return $;
  }
  public Map<GrammarElement, Set<List<GrammarElement>>> regularFormWithExtendibles(FlingProducer producer) {
    toBNF(producer);
    Map<GrammarElement, Set<List<GrammarElement>>> $ = new LinkedHashMap<>(normalizedForm(producer));
    for (Extendible e : extendibles) {
      $.put(e, Collections.singleton(Collections.singletonList(e.head())));
      for (DerivationRule r : e.rawSolution()) {
        $.putIfAbsent(r.head, new HashSet<>());
        $.get(r.head).add(r.body());
      }
    }
    return $;
  }
  public Map<Symbol, Set<List<GrammarElement>>> normalizedForm(Function<Symbol, Symbol> producer) {
    Map<Symbol, Set<List<GrammarElement>>> rf = regularForm(), $ = new LinkedHashMap<>();
    for (Entry<Symbol, Set<List<GrammarElement>>> e : rf.entrySet()) {
      Symbol lhs = e.getKey();
      Set<List<GrammarElement>> rhs = e.getValue();
      if (rhs.size() <= 1 || rhs.stream().allMatch(x -> x.isEmpty() || x.size() == 1 && x.get(0).isNonTerminal())) {
        $.put(lhs, rhs);
        continue;
      }
      $.put(lhs, new HashSet<>());
      for (List<GrammarElement> clause : rhs) {
        List<GrammarElement> l = new LinkedList<>();
        Symbol nt = producer.apply(lhs);
        l.add(nt);
        $.get(lhs).add(l);
        $.put(nt, new HashSet<>());
        $.get(nt).add(clause);
      }
    }
    return $;
  }
  public EBNF makeSubBNF(String s) {
    return makeSubBNF(Symbol.of(s));
  }
  public EBNF makeSubBNF(Symbol nt) {
    assert nestedSymbolsMapping != null;
    isSubEBNF = true;
    subHead = nestedSymbolsMapping.get(nt);
    return this;
  }
  public Set<DerivationRule> rules() {
    return new LinkedHashSet<>(derivationRules);
  }
  public FlingProducer beforeSolution() {
    return beforeSolution.clone();
  }
  public FlingProducer afterSolution() {
    return afterSolution.clone();
  }
}
