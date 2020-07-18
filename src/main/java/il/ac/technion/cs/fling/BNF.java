package il.ac.technion.cs.fling;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import static java.util.stream.Collectors.*;
import static java.util.Collections.singleton;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** A compact version of Backus-Naur Form grammar specification of a formal
 * language, represented as a map from the set of non-terminals, to {@link SF}
 *
 * @author Yossi Gil */
public interface BNF {
  Stream<SF> forms(Variable v);
  /** @return The start variable of this BNF */
  Variable start();
  Stream<Symbol> symbols();
  Stream<Token> tokens();
  Stream<Variable> variables();
  static Builder of(final Variable start) {
    return new Builder(start);
  }
  final class Builder {
    private final Inner inner;
    private Builder(final Variable start) {
      Objects.requireNonNull(start);
      inner = new Inner(start);
    }
    public BNF build() {
      return inner;
    }
    public Derive derive(final Variable v) {
      return new Derive(add(v));
    }
    Builder epsilon(final Variable v) {
      inner.rules.get(add(v)).add(SF.empty());
      return this;
    }
    Builder epsilon(final Variable v, final Variable... vs) {
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
      Derive(final Variable variable) {
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
        public Derive derive(final Variable v) {
          return new Derive(v);
        }
        public Builder epsilon(final Variable v, final Variable... vs) {
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
        public Specialize specialize(final Variable v) {
          return new Specialize(v);
        }
      }
    }
    public class Specialize {
      private final Variable variable;
      Specialize(final Variable variable) {
        this.variable = variable;
      }
      public Builder into(final Variable... vs) {
        Stream.of(vs).forEach(v -> inner.rules.get(variable).add(SF.of(add(v))));
        return Builder.this;
      }
    }
  }
  public abstract class Decorator implements BNF {
    private final BNF inner;
    protected Decorator(final BNF inner) {
      this.inner = inner;
    }
    @Override public Stream<SF> forms(final Variable v) {
      return inner.forms(v);
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
    private Stream<SF> expand(final SF sf) {
      Stream<SF> $ = Stream.empty();
      for (int i = 0; i < sf.size(); ++i)
        if (sf.get(i) instanceof Variable v) {
          final int j = i;
          $ = Stream.concat($, forms(v).map(f -> sf.replace(j, f)));
        }
      return $;
    }
    private Stream<Symbol> symbols(final Variable v) {
      return Stream.of(v).flatMap(this::forms).flatMap(SF::symbols).distinct();
    }
    Stream<Word<Token>> expand(final Variable v) {
      return closure(SF.of(v), this::expand).stream().filter(SF::isGrounded).map(SF::tokens);
    }
    boolean recursive() {
      return uses(start()).contains(start());
    }
    Set<Variable> uses(final Variable v) {
      return closure(v, u -> variables(symbols(u)));
    }
    BNF reduce(final Variable v) {
      final Set<Variable> s = uses(v);
      s.add(v);
      final Map<Variable, Set<SF>> rules = new LinkedHashMap<>();
      s.stream().forEach(u -> rules.put(u, forms(v).collect(toSet())));
      return new BNF.Inner(start(), rules);
    }
    static <T> Set<T> closure(final T t, final Function<T, Stream<T>> expand) {
      return closure(singleton(t), expand);
    }
    static <T> boolean exists(final Stream<T> ss) {
      return !ss.collect(toList()).isEmpty();
    }
    static <T> void worklist(final Supplier<? extends Stream<T>> s, final Predicate<? super T> u) {
      while (exists(s.get().filter(u))) {
      }
    }
    static <T> Set<T> closure(final Set<T> ts, final Function<? super T, ? extends Stream<T>> expand) {
      final Set<T> $ = new LinkedHashSet<>();
      Set<T> current = ts;
      do
        current = current.stream().flatMap(expand).collect(toSet());
      while ($.addAll(current));
      return $;
    }
    static Stream<Variable> variables(final Stream<Symbol> ss) {
      return ss.filter(Variable.class::isInstance).map(Variable.class::cast);
    }
  }
  public record SF(Word<Symbol> inner) {
    /** @return delegation to {@link Word#size()} */
    int size() {
      return inner.size();
    }
    boolean isGrounded() {
      return symbols().allMatch(Token.class::isInstance);
    }
    Word<Token> tokens() {
      return Word.of(symbols().filter(Token.class::isInstance).map(Token.class::cast));
    }
    static SF of(Symbol... ss) {
      return new SF(Word.of(ss));
    }
    Stream<Symbol> symbols() {
      return inner.stream();
    }
    Symbol get(int i) {
      return inner.get(i);
    }
    @NonNull List<Symbol> suffix(int i) {
      return inner.subList(i, inner.size());
    }
    public Iterable<Symbol> isymbols() {
      return inner;
    }
    static SF empty() {
      return new SF(Word.empty());
    }
    SF replace(int i, SF f) {
      List<Symbol> $ = new ArrayList<>(prefix(i));
      $.addAll(f.inner);
      $.addAll(suffix(i + 1));
      return new SF(new Word<>($));
    }
    private List<Symbol> prefix(int i) {
      return inner.subList(0, i);
    }
    public Iterable<List<Symbol>> suffixes() {
      return new Iterable<>() {
        @Override public Iterator<List<Symbol>> iterator() {
          return new Iterator<>() {
            int i = 0;
            @Override public boolean hasNext() {
              return i < size();
            }
            @Override public List<Symbol> next() {
              return suffix(i++);
            }
          };
        }
      };
    }
  }
  record Inner(Variable start, Map<Variable, Set<SF>> rules) implements BNF {
    Inner(Variable start) {
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
  }
}