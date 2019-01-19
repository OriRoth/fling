package fling.bnf;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fling.sententials.Constants;
import fling.sententials.RHS;
import fling.sententials.Symbol;
import fling.sententials.Terminal;
import fling.sententials.Variable;
import fling.sententials.Word;

public class BNF<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
  public final Map<Variable, RHS> R;
  public final Set<Symbol> nullables;
  public final Map<Symbol, Set<Terminal>> firsts;
  public final Map<Variable, Set<Terminal>> follows;
  private final Class<Σ> Σ;
  private final Class<V> V;

  public BNF(Class<Σ> Σ, Class<V> V, Map<Variable, RHS> R) {
    this.Σ = Σ;
    this.V = V;
    this.R = R;
    this.nullables = getNullables();
    this.firsts = getFirsts();
    this.follows = getFollows();
  }
  public Set<Terminal> Σ() {
    Set<Terminal> $ = new LinkedHashSet<>();
    $.addAll(EnumSet.allOf(Σ));
    $.add(Constants.$);
    return unmodifiableSet($);
  }
  public Set<Variable> V() {
    Set<Variable> $ = new LinkedHashSet<>();
    $.addAll(EnumSet.allOf(V));
    $.add(Constants.S);
    return unmodifiableSet($);
  }
  public Collection<Symbol> symbols() {
    Set<Symbol> $ = new LinkedHashSet<>();
    $.addAll(Σ());
    $.addAll(V());
    return unmodifiableSet($);
  }
  public Stream<Word<Symbol>> rhs(Variable v) {
    return R.get(v).stream();
  }
  public boolean isNullable(Symbol... symbols) {
    return isNullable(symbols);
  }
  public boolean isNullable(List<Symbol> symbols) {
    return symbols.stream().allMatch(nullables::contains);
  }
  public Set<Terminal> firsts(Symbol... symbols) {
    return firsts(Arrays.asList(symbols));
  }
  public Set<Terminal> firsts(Collection<Symbol> symbols) {
    Set<Terminal> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return unmodifiableSet($);
  }
  private Set<Symbol> getNullables() {
    return V().stream().filter(v -> R.get(v).stream().anyMatch(List::isEmpty)).collect(toSet());
  }
  @SuppressWarnings("unlikely-arg-type") private Map<Symbol, Set<Terminal>> getFirsts() {
    Map<Symbol, Set<Terminal>> $ = new LinkedHashMap<>();
    Σ().forEach(σ -> $.put(σ, singleton(σ)));
    V().forEach(v -> $.put(v, new LinkedHashSet<>()));
    for (boolean changed = true; changed;) {
      changed = false;
      for (V v : EnumSet.allOf(V))
        for (Word<Symbol> sf : R.get(v))
          for (Symbol s : sf) {
            changed |= $.get(v).addAll($.get(s));
            if (!isNullable(s))
              break;
          }
    }
    V().forEach(v -> $.put(v, unmodifiableSet($.get(v))));
    return unmodifiableMap($);
  }
  private Map<Variable, Set<Terminal>> getFollows() {
    Map<Variable, Set<Terminal>> $ = new LinkedHashMap<>();
    V().forEach(v -> $.put(v, new LinkedHashSet<>()));
    $.get(Constants.S).add(Constants.$);
    for (boolean changed = true; changed;) {
      changed = false;
      for (Variable v : V())
        for (Word<Symbol> sf : R.get(v))
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
    V().forEach(s -> $.put(s, unmodifiableSet($.get(s))));
    return unmodifiableMap($);
  }

  public static class Builder<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
    private final Class<Σ> Σ;
    private final Class<V> V;
    private final Map<Variable, RHS> R;

    public Builder(Class<Σ> Σ, Class<V> V) {
      this.Σ = Σ;
      this.V = V;
      this.R = new LinkedHashMap<>();
      for (V v : EnumSet.allOf(V))
        R.put(v, new RHS());
    }
    public Builder<Σ, V> derive(Variable lhs, Symbol... rhs) {
      R.get(lhs).add(new Word<>(rhs));
      return this;
    }
    public BNF<Σ, V> build() {
      return new BNF<>(Σ, V, R);
    }
  }
}
