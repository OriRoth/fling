package fling.grammar;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fling.grammar.sententials.Constants;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.types.TypeParameter;

public class BNF {
  public final Set<DerivationRule> R;
  public final Set<Symbol> nullables;
  public final Map<Symbol, Set<Verb>> firsts;
  public final Map<Variable, Set<Verb>> follows;
  public final Set<Verb> Σ;
  public final Set<Variable> V;
  public final Variable startVariable;
  public final Set<Variable> headVariables;

  public BNF(Set<Verb> Σ, Set<? extends Variable> V, Set<DerivationRule> R, Variable startVariable, Set<Variable> headVariables,
      boolean addStartSymbolDerivationRules) {
    this.Σ = Σ;
    Σ.add(Constants.$$);
    this.V = new LinkedHashSet<>(V);
    this.V.add(Constants.S);
    this.R = R;
    if (addStartSymbolDerivationRules) {
      R.add(new DerivationRule(Constants.S, new ArrayList<>()));
      rhs(Constants.S).add(new SententialForm(startVariable));
    }
    this.headVariables = headVariables;
    this.startVariable = startVariable;
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
  }
  public static <V extends Enum<V> & Variable> Builder<V> bnf(Class<V> V) {
    return new Builder<>(V);
  }
  public Set<Symbol> symbols() {
    Set<Symbol> $ = new LinkedHashSet<>();
    $.addAll(Σ);
    $.addAll(V);
    return unmodifiableSet($);
  }
  public List<SententialForm> rhs(Variable v) {
    return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
  }
  public boolean isNullable(Symbol... symbols) {
    return isNullable(Arrays.asList(symbols));
  }
  public boolean isNullable(List<Symbol> symbols) {
    return symbols.stream().allMatch(nullables::contains);
  }
  public Set<Verb> firsts(Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Verb> firsts(Collection<Symbol> symbols) {
    Set<Verb> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  private Set<Symbol> getNullables() {
    Set<Symbol> $ = new LinkedHashSet<>();
    for (; $.addAll(V.stream() //
        .filter(v -> rhs(v).stream() //
            .anyMatch(sf -> sf.isEmpty() || sf.stream().allMatch($::contains))) //
        .collect(toSet()));)
      ;
    return $;
  }
  private Map<Symbol, Set<Verb>> getFirsts() {
    Map<Symbol, Set<Verb>> $ = new LinkedHashMap<>();
    Σ.forEach(σ -> $.put(σ, singleton(σ)));
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V)
        for (SententialForm sf : rhs(v))
          for (Symbol s : sf) {
            changed |= $.get(v).addAll($.get(s));
            if (!isNullable(s))
              break;
          }
    }
    V.forEach(v -> $.put(v, unmodifiableSet($.get(v))));
    return unmodifiableMap($);
  }
  private Map<Variable, Set<Verb>> getFollows() {
    Map<Variable, Set<Verb>> $ = new LinkedHashMap<>();
    V.forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$$);
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V)
        for (SententialForm sf : rhs(v))
          for (int i = 0; i < sf.size(); ++i) {
            if (!sf.get(i).isVariable())
              continue;
            Variable current = sf.get(i).asVariable();
            List<Symbol> rest = sf.subList(i, sf.size());
            changed |= $.get(current).addAll(firsts(rest));
            if (isNullable(rest))
              changed |= $.get(v).addAll($.get(current));
          }
    }
    V.forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }

  public static class Builder<V extends Enum<V> & Variable> {
    private final Set<Verb> Σ;
    private final Class<V> V;
    private final Set<DerivationRule> R;
    private V start;
    private final Set<Variable> heads;

    public Builder(Class<V> V) {
      this.Σ = new LinkedHashSet<>();
      this.V = V;
      this.R = new LinkedHashSet<>();
      for (V v : EnumSet.allOf(V))
        R.add(new DerivationRule(v, new ArrayList<>()));
      this.heads = new LinkedHashSet<>();
    }
    public Builder<V> derive(Variable lhs, Symbol... sententialForm) {
      rhs(lhs).add(new SententialForm(Arrays.stream(sententialForm) //
          .map(symbol -> {
            Symbol ret = !symbol.isTerminal() ? symbol : new Verb(symbol.asTerminal());
            if (ret.isVerb())
              Σ.add(ret.asVerb());
            return ret;
          }) //
          .collect(Collectors.toList())));
      for (Symbol symbol : sententialForm)
        // TODO support more complex structures.
        if (symbol.isVerb())
          symbol.asVerb().parameters.stream() //
              .map(TypeParameter::declaredHeadVariables) //
              .forEach(heads::addAll);
      return this;
    }
    public final Builder<V> start(V startVariable) {
      start = startVariable;
      return this;
    }
    public BNF build() {
      assert start != null;
      return new BNF(Σ, EnumSet.allOf(V), R, start, heads, true);
    }
    private List<SententialForm> rhs(Variable v) {
      return R.stream().filter(r -> r.lhs.equals(v)).findFirst().map(DerivationRule::rhs).orElse(null);
    }
  }
}
