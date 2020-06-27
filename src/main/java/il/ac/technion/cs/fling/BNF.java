package il.ac.technion.cs.fling;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Symbol;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** A compact version of Backus-Naur Form grammar specification of a formal
 * language, represented as a map from the set of non-terminals, to
 * {@link Rule}.
 *
 * @author Yossi Gil */
public interface BNF {
  /** @return The start variable of this BNF */
  Variable start();
  Stream<SF> forms(Variable v);
  Iterable<SF> iforms(Variable v);
  Stream<Variable> variables();
  Stream<Token> tokens();
  static Builder of(Variable start) {
    return new Builder(start);
  }
  abstract class Decorator implements BNF {
    private BNF inner;
    @Override public Iterable<SF> iforms(Variable v) {
      return inner.iforms(v);
    }
    public Decorator(BNF inner) {
      this.inner = inner;
    }
    @Override public Variable start() {
      return inner.start();
    }
    @Override public Stream<SF> forms(Variable v) {
      return inner.forms(v);
    }
    @Override public Stream<Variable> variables() {
      return inner.variables();
    }
    @Override public Stream<Token> tokens() {
      return inner.tokens();
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
  }
  record implementation(Variable start, Map<Variable, Set<SF>> rules) implements BNF {
    public implementation(Variable start) {
      this(start, new LinkedHashMap<>());
    }
    /** @return all rules defining a variable */
    @Override public Stream<SF> forms(final Variable v) {
      return rules.computeIfAbsent(v, x -> new LinkedHashSet<>()).stream();
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
    @Override public Iterable<SF> iforms(Variable v) {
      return rules.get(v);
    }
  }
  class Builder {
    private final implementation inner;
    private Builder(Variable start) {
      Objects.requireNonNull(start);
      this.inner = new implementation(start);
    }
    public Derive derive(final Variable v) {
      return new Derive(add(v));
    }
    private Variable add(final Variable v) {
      inner.rules.putIfAbsent(v, new LinkedHashSet<>());
      return v;
    }
    public Specialize specialize(final Variable v) {
      return new Specialize(add(v));
    }
    public BNF build() {
      return inner;
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
      public Builder toEpsilon() {
        inner.rules.get(variable).add(SF.of());
        return Builder.this;
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
}