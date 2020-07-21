package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
import il.ac.technion.cs.fling.internal.util.As;
/** Deterministic pushdown automaton (DPDA) supporting acceptance by final state
 * (not by empty stack as other DPDAs do)
 *
 * @author Ori Roth
 * @param <Q> states type
 * @param <Σ> alphabet type
 * @param <Γ> stack symbols type */
public class DPDA<Q, Σ, Γ> {
  public final Set<Q> F;
  public final Set<Q> Q;
  public final Q q0;
  public final Set<Γ> Γ;
  public final Word<Γ> γ0;
  public final Set<δ<Q, Σ, Γ>> δs;
  public final Set<Σ> Σ;
  public DPDA(final Set<Q> Q, final Set<Σ> Σ, final Set<Γ> Γ, final Set<δ<Q, Σ, Γ>> δs, final Set<Q> F, final Q q0,
      final Word<Γ> γ0) {
    this.Q = Collections.unmodifiableSet(Q);
    this.Σ = Collections.unmodifiableSet(Σ);
    this.Γ = Collections.unmodifiableSet(Γ);
    this.δs = Collections.unmodifiableSet(δs);
    this.F = Collections.unmodifiableSet(F);
    this.q0 = q0;
    this.γ0 = γ0;
    verify();
  }
  /** @param q a state
   * @return whether this is an accepting state */
  public boolean isAccepting(final Q q) {
    return F.contains(q);
  }
  public Stream<Q> Q() {
    return Q.stream();
  }
  @Override public String toString() {
    return String.format("""
        Q=%s
        Σ=%s
        Γ=%s
        F=%s
        q0=%s
        γ0=%s
        δs=\t%s""", Q, Σ, Γ, F, q0, γ0, δs.stream().map(Object::toString).collect(Collectors.joining("\n\t")));
  }
  public Stream<Γ> Γ() {
    return Γ.stream();
  }
  /** @param q current state
   * @param γ current stack symbol
   * @param σ current input letter
   * @return matching transition */
  public δ<Q, Σ, Γ> δ(final Q q, final Γ γ, final Σ σ) {
    for (final δ<Q, Σ, Γ> δ : δs)
      if (δ.match(q, γ, σ))
        return δ;
    return null;
  }
  /** Returns matching consolidated transition, i.e., the result of the multiple
   * transitions initiated by the received configuration. The returned transition
   * does not contain stack symbol.
   *
   * @param q current state
   * @param α current stack
   * @param σ current input letter
   * @return matching consolidated transition */
  public δ<Q, Σ, Γ> δδ(final Q q, final Word<Γ> α, final Σ σ) {
    var s = new Word<>(α);
    final var δ = δ(q, s.top(), σ);
    if (δ == null)
      return null;
    var q$ = δ.q$;
    s = s.pop().push(δ.α);
    // process subsequent ε transitions.
    for (;;) {
      if (s.isEmpty())
        return new δ<>(q, σ, null, q$, s);
      final var δ$ = δ(q$, s.top(), ε());
      if (δ$ == null)
        return new δ<>(q, σ, null, q$, s);
      s = s.pop().push(δ$.α);
      q$ = δ$.q$;
    }
  }
  /** Returns matching consolidated transition, i.e., the result of the multiple
   * transitions initiated by the received configuration.
   *
   * @param q current state
   * @param γ current stack symbol
   * @param σ current input letter
   * @return matching consolidated transition */
  public δ<Q, Σ, Γ> δδ(final Q q, final Γ γ, final Σ σ) {
    var s = new Word<>(γ);
    final var δ = δ(q, s.top(), σ);
    if (δ == null)
      return null;
    var q$ = δ.q$;
    s = s.pop().push(δ.α);
    // process subsequent ε transitions.
    for (;;) {
      if (s.isEmpty())
        return new δ<>(q, σ, γ, q$, s);
      final var δ$ = δ(q$, s.top(), ε());
      if (δ$ == null)
        return new δ<>(q, σ, γ, q$, s);
      s = s.pop().push(δ$.α);
      q$ = δ$.q$;
    }
  }
  public Stream<Σ> Σ() {
    return Σ.stream();
  }
  private void verify() {
    final Map<Q, Set<δ<Q, Σ, Γ>>> seenTransitions = new HashMap<>();
    Q.forEach(q -> seenTransitions.put(q, new HashSet<>()));
    for (final Q q : Q)
      for (final δ<Q, Σ, Γ> δ : δs)
        if (q.equals(δ.q)) {
          final var δ2 = seenTransitions.get(q).stream().filter(δ$ -> δ.γ.equals(δ$.γ))
              .filter(δ$ -> δ$.σ == ε() || δ$.σ.equals(δ.σ)).findAny();
          if (δ2.isPresent())
            throw new RuntimeException(
                String.format("determinism broke in state %s with transitions %s and %s", q, δ2.get(), δ));
          seenTransitions.get(q).add(δ);
        }
  }
  public static <Q extends Enum<Q>, Σ extends Enum<Σ>, Γ extends Enum<Γ>> Builder<Q, Σ, Γ> dpda(final Class<Q> Q,
      final Class<Σ> Σ, final Class<Γ> Γ) {
    return new Builder<>(Q, Σ, Γ);
  }
  @SafeVarargs public final boolean run(final Σ... w) {
    return run(As.iterable(w));
  }
  public boolean run(final Iterable<? extends Σ> w) {
    var stack = γ0;
    var q = q0;
    for (final var σ : w) {
      if (stack.isEmpty())
        return false;
      var δ = δ(q, stack.top(), σ);
      if (δ == null)
        return false;
      q = δ.q$;
      stack = stack.pop().push(δ.α);
      if (stack.isEmpty())
        break;
      for (δ = δ(q, stack.top(), ε()); δ != null; δ = δ(q, stack.top(), ε())) {
        q = δ.q$;
        stack = stack.pop().push(δ.α);
        if (stack.isEmpty())
          break;
      }
    }
    return isAccepting(q);
  }
  /** {@link DPDA} builder. Does not check the correctness of the automaton, i.e.,
   * it assumes it is deterministic and cannot loop infinitely. */
  public static class Builder<Q extends Enum<Q>, Σ extends Enum<Σ>, Γ extends Enum<Γ>> {
    private final Set<Q> F = new LinkedHashSet<>();
    private final Class<Q> Q;
    private Q q0;
    private final Class<Γ> Γ;
    private Word<Γ> γ0;
    private final Set<δ<Q, Σ, Γ>> δs = new LinkedHashSet<>();
    private final Class<Σ> Σ;
    Builder(final Class<Q> Q, final Class<Σ> Σ, final Class<Γ> Γ) {
      this.Q = Q;
      this.Σ = Σ;
      this.Γ = Γ;
    }
    @SafeVarargs public final Builder<Q, Σ, Γ> F(final Q... qs) {
      Collections.addAll(F, qs);
      return this;
    }
    public DPDA<Q, Σ, Γ> go() {
      assert q0 != null;
      assert γ0 != null;
      return new DPDA<>(EnumSet.allOf(Q), EnumSet.allOf(Σ), EnumSet.allOf(Γ), δs, F, q0, γ0);
    }
    @SuppressWarnings("hiding") public Builder<Q, Σ, Γ> q0(final Q q0) {
      this.q0 = q0;
      return this;
    }
    @SafeVarargs @SuppressWarnings("hiding") public final Builder<Q, Σ, Γ> γ0(final Γ... γ0) {
      this.γ0 = new Word<>(γ0);
      return this;
    }
    @SafeVarargs public final Builder<Q, Σ, Γ> δ(final Q q, final Σ σ, final Γ γ, final Q q$, final Γ... α) {
      δs.add(new δ<>(q, σ, γ, q$, new Word<>(α)));
      return this;
    }
  }
  /** An automaton edge. A set of edges is a transition function. */
  public static class δ<Q, Σ, Γ> {
    public final Q q;
    public final Q q$;
    public final Γ γ;
    public final Σ σ;
    private final Word<Γ> α;
    public δ(final @NonNull Q q, final Σ σ, final @NonNull Γ γ, final @NonNull Q q$, final @NonNull Word<Γ> α) {
      Objects.requireNonNull(q);
      Objects.requireNonNull(γ);
      Objects.requireNonNull(q$);
      this.q = q;
      this.σ = σ;
      this.γ = γ;
      this.q$ = q$;
      this.α = α;
    }
    @Override public int hashCode() {
      return Objects.hash(q, q$, α, γ, σ);
    }
    @Override public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      return equals((δ) o);
    }
    private boolean equals(δ other) {
      return Objects.equals(q, other.q) && Objects.equals(q$, other.q$) && Objects.equals(α, other.α)
          && Objects.equals(γ, other.γ) && Objects.equals(σ, other.σ);
    }
    @Override public String toString() {
      return String.format("<%s,%s,%s,%s,%s>", q, σ == ε() ? "ε" : σ, γ, q$, α);
    }
    /** @param currentq current state
     * @param currentγ current stack symbol
     * @param currentσ current input letter
     * @return whether this edge describes the next transition */
    boolean match(final Q currentq, final Γ currentγ, final Σ currentσ) {
      return q.equals(currentq) && Objects.equals(σ, currentσ) && γ.equals(currentγ);
    }
  }
}