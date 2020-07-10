package il.ac.technion.cs.fling;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;
import static java.util.Collections.singleton;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** A compact version of Backus-Naur Form grammar specification of a formal
 * language, represented as a map from the set of non-terminals, to
 * {@link Rule}.
 *
 * @author Yossi Gil */
public interface BNF {
  Stream<SF> forms(Variable v);
  Set<SF> iforms(Variable v);
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
    public Builder epsilon(Variable v, Variable... vs) {
      epsilon(v);
      Stream.of(vs).forEach(this::epsilon);
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
        this.variable = add(variable);
      }
      public Alternative to(final Symbol... ss) {
        return new Alternative().or(ss);
      }
      public Alternative to(final TempSymbol... ss) {
        return new Alternative().or(ss);
      }
      public Builder toEpsilon() {
        return epsilon(variable);
      }
      public Builder toNothingOr(final TempSymbol... cs) {
        return to(cs).epsilon(variable);
      }
      public Builder ε_or(final Symbol... cs) {
        return to(cs).epsilon(variable);
      }
      public class Alternative {
        public BNF build() {
          return Builder.this.build();
        }
        public Derive derive(Variable v) {
          return new Derive(v);
        }
        public Builder epsilon(Variable v, Variable... vs) {
          return Builder.this.epsilon(v, vs);
        }
        public Alternative or(final Symbol... ss) {
          Decorator.variables(Stream.of(ss)).forEach(Builder.this::add);
          inner.rules.get(variable).add(SF.of(ss));
          return new Alternative();
        }
        public Alternative or(final TempSymbol... cs) {
          return or(Stream.of(cs).map(TempSymbol::normalize).toArray(Symbol[]::new));
        }
        public Specialize specialize(Variable v) {
          return new Specialize(v);
        }
      }
    }
    public class Specialize {
      private final Variable variable;
      public Specialize(final Variable variable) {
        this.variable = variable;
      }
      public Builder into(final Variable... vs) {
        Stream.of(vs).forEach(v -> inner.rules.get(variable).add(SF.of(add(v))));
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
    @Override public Set<SF> iforms(Variable v) {
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
    private Stream<SF> expand(SF sf) {
      Stream<SF> $ = Stream.empty();
      for (int i = 0; i < sf.size(); ++i)
        if (sf.get(i) instanceof Variable v) {
          final int j = i;
          $ = Stream.concat($, forms(v).map(f -> sf.replace(j, f)));
        }
      return $;
    }
    private Stream<Symbol> symbols(Variable v) {
      return Stream.of(v).flatMap(this::forms).flatMap(SF::symbols).distinct();
    }
    protected Stream<Word<Token>> expand(Variable v) {
      return closure(SF.of(v), (Function<SF, Stream<SF>>) sf -> expand(sf)).stream().filter(SF::isGrounded)
          .map(SF::tokens);
    }
    protected boolean recursive() {
      return uses(start()).contains(start());
    }
    protected Set<Variable> uses(Variable v) {
      return closure(v, u -> variables(symbols(u)));
    }
    BNF reduce(Variable v) {
      Set<Variable> s = uses(v);
      s.add(v);
      Map<Variable, Set<SF>> rules = new LinkedHashMap<>();
      s.stream().forEach(u -> rules.put(u, iforms(v)));
      return new BNF.Inner(start(), rules);
    }
    protected static <T> Set<T> closure(T t, Function<T, Stream<T>> expand) {
      return closure(singleton(t), expand);
    }
    protected static <T> boolean exists(Stream<T> ss) {
      return !ss.collect(toList()).isEmpty();
    }
    protected static <T> void worklist(Supplier<Stream<T>> s, Predicate<T> u) {
      while (exists(s.get().filter(u)))
        continue;
    }
    static <T> Set<T> closure(Set<T> ts, Function<T, Stream<T>> expand) {
      Set<T> $ = new LinkedHashSet<>();
      Set<T> current = ts;
      do
        current = current.stream().flatMap(expand::apply).collect(toSet());
      while ($.addAll(current));
      return $;
    }
    static Stream<Variable> variables(Stream<Symbol> ss) {
      return ss.filter(Variable.class::isInstance).map(Variable.class::cast);
    }
  }
  record SF(Word<Symbol> inner) {
    /** @return delegation to {@link Word#size()} */
    public int size() {
      return inner.size();
    }
    public boolean isGrounded() {
      return symbols().allMatch(Token.class::isInstance);
    }
    public Word<Token> tokens() {
      return Word.of(symbols().filter(Token.class::isInstance).map(Token.class::cast));
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
    public SF replace(int i, SF f) {
      List<Symbol> $ = new ArrayList<>(prefix(i));
      $.addAll(f.inner);
      $.addAll(suffix(i + 1));
      return new SF(new Word<>($));
    }
    private List<Symbol> prefix(int i) {
      return inner.subList(0, i);
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
    @Override public Set<SF> iforms(Variable v) {
      return rules.get(v);
    }
  }
}