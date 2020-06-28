package il.ac.technion.cs.fling;
import java.util.*;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** A compact version of Backus-Naur Form grammar specification of a formal
 * language, represented as a map from the set of non-terminals, to
 * {@link Rule}.
 *
 * @author Yossi Gil */
public interface BNF {
  Stream<SF> forms(Variable v);
  Iterable<SF> iforms(Variable v);
  /** @return The start variable of this BNF */
  Variable start();
  Stream<Symbol> symbols();
  Stream<Token> tokens();
  Stream<Variable> variables();
  static Builder of(Variable start) {
    return new Builder(start);
  }
  class Builder {
    private final Inner inner;
    private Builder(Variable start) {
      Objects.requireNonNull(start);
      this.inner = new Inner(start);
    }
    public BNF build() {
      return inner;
    }
    public Derive derive(final Variable v) {
      return new Derive(add(v));
    }
    public Builder epsilon(Variable v) {
      inner.rules.get(add(v)).add(SF.empty());
      return this;
    }
    public Specialize specialize(final Variable v) {
      return new Specialize(add(v));
    }
    private Variable add(final Variable v) {
      inner.rules.putIfAbsent(v, new LinkedHashSet<>());
      return v;
    }
    public class Derive {
      private final Variable variable;
      public Derive(final Variable variable) {
        this.variable = variable;
      }
      public Builder to(final Symbol... cs) {
        for (final Symbol s : cs)
          if (s instanceof Variable v)
            add(v);
        inner.rules.get(variable).add(SF.of(cs));
        return Builder.this;
      }
      public Builder to(final TempSymbol... cs) {
        Symbol[] $ = new Symbol[cs.length];
        int i = 0;
        for (final TempSymbol t : cs)
          $[i++] = t.normalize();
        return to($);
      }
      public Builder toEpsilon() {
        inner.rules.get(variable).add(SF.of());
        return Builder.this;
      }
      public Builder toNothingOr(final TempSymbol... cs) {
        return to(cs).epsilon(variable);
      }
      public Builder ε_or(final Symbol... cs) {
        return to(cs).epsilon(variable);
      }
    }
    public class Specialize {
      private final Variable variable;
      public Specialize(final Variable variable) {
        this.variable = variable;
      }
      public Builder into(final Variable... vs) {
        for (final Variable v : vs)
          inner.rules.get(variable).add(SF.of(add(v)));
        return Builder.this;
      }
    }
  }
  abstract class Decorator implements BNF {
    private BNF inner;
    public Decorator(BNF inner) {
      this.inner = inner;
    }
    @Override public Stream<SF> forms(Variable v) {
      return inner.forms(v);
    }
    @Override public Iterable<SF> iforms(Variable v) {
      return inner.iforms(v);
    }
    @Override public Variable start() {
      return inner.start();
    }
    @Override public Stream<Symbol> symbols() {
      return inner.symbols();
    }
    @Override public Stream<Token> tokens() {
      return inner.tokens();
    }
    @Override public Stream<Variable> variables() {
      return inner.variables();
    }
  }
  record SF(Word<Symbol> inner) {
    /** @return delegation to {@link Word#size()} */
    public int size() {
      return inner.size();
    }
    static SF of(Symbol... ss) {
      return new SF(Word.of(ss));
    }
    public Stream<Symbol> symbols() {
      return inner.stream();
    }
    public Symbol get(int i) {
      return inner.get(i);
    }
    public List<Symbol> suffix(int i) {
      return inner.subList(i, inner.size());
    }
    public Iterable<Symbol> isymbols() {
      return inner;
    }
    public static SF empty() {
      return new SF(Word.empty());
    }
    public List<Symbol> sublist(int i, int j) {
      return inner.subList(i, j);
    }
  }
  record Inner(Variable start, Map<Variable, Set<SF>> rules) implements BNF {
    public Inner(Variable start) {
      this(start, new LinkedHashMap<>());
    }
    public Inner {
      rules.putIfAbsent(start, new LinkedHashSet<>());
    }
    /** @return all rules defining a variable */
    @Override public Stream<SF> forms(final Variable v) {
      return rules.get(v).stream();
    }
    public static Builder from(Variable start) {
      return new Builder(start);
    }
    @Override public Stream<Variable> variables() {
      return rules.keySet().stream();
    }
    @Override public Stream<Token> tokens() {
      return rules.values().stream().flatMap(Set::stream).flatMap(SF::symbols).filter(Token.class::isInstance)
          .map(Token.class::cast).distinct();
    }
    @Override public Stream<Symbol> symbols() {
      return Stream.concat(variables(), rules.values().stream().flatMap(Set::stream).flatMap(SF::symbols)).distinct();
    }
    @Override public Iterable<SF> iforms(Variable v) {
      return rules.get(v);
    }
  }
}