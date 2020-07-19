package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
   * @param σ current input letter
   * @param γ current stack symbol
   * @return matching transition */
  public δ<Q, Σ, Γ> δ(final Q q, final Σ σ, final Γ γ) {
    for (final δ<Q, Σ, Γ> δ : δs)
      if (δ.match(q, σ, γ))
        return δ;
    return null;
  }
  /** Returns matching consolidated transition, i.e., the result of the multiple
   * transitions initiated by the received configuration. The returned transition
   * does not contain stack symbol.
   *
   * @param q current state
   * @param σ current input letter
   * @param α current stack
   * @return matching consolidated transition */
  public δ<Q, Σ, Γ> δδ(final Q q, final Σ σ, final Word<Γ> α) {
    var s = new Word<>(α);
    final var δ = δ(q, σ, s.top());
    if (δ == null)
      return null;
    var q$ = δ.q$;
    s = s.pop().push(δ.getΑ());
    // process subsequent ε transitions.
    for (;;) {
      if (s.isEmpty())
        return new δ<>(q, σ, null, q$, s);
      final var δ$ = δ(q$, ε(), s.top());
      if (δ$ == null)
        return new δ<>(q, σ, null, q$, s);
      s = s.pop().push(δ$.getΑ());
      q$ = δ$.q$;
    }
  }
  /** Returns matching consolidated transition, i.e., the result of the multiple
   * transitions initiated by the received configuration.
   *
   * @param q current state
   * @param σ current input letter
   * @param γ current stack symbol
   * @return matching consolidated transition */
  public δ<Q, Σ, Γ> δδ(final Q q, final Σ σ, final Γ γ) {
    var s = new Word<>(γ);
    final var δ = δ(q, σ, s.top());
    if (δ == null)
      return null;
    var q$ = δ.q$;
    s = s.pop().push(δ.getΑ());
    // process subsequent ε transitions.
    for (;;) {
      if (s.isEmpty())
        return new δ<>(q, σ, γ, q$, s);
      final var δ$ = δ(q$, ε(), s.top());
      if (δ$ == null)
        return new δ<>(q, σ, γ, q$, s);
      s = s.pop().push(δ$.getΑ());
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
  public boolean run(final Iterable<Σ> w) {
    var stack = γ0;
    var q = q0;
    for (var σ : w) {
      if (stack.isEmpty())
        return false;
      var δ = δ(q, σ, stack.top());
      if (δ == null)
        return false;
      q = δ.q$;
      stack = stack.pop().push(δ.getΑ());
      if (stack.isEmpty())
        break;
      for (δ = δ(q, ε(), stack.top()); δ != null; δ = δ(q, ε(), stack.top())) {
        q = δ.q$;
        stack = stack.pop().push(δ.getΑ());
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
    public δ(final Q q, final Σ σ, final Γ γ, final Q q$, final Word<Γ> α) {
      this.q = q;
      this.σ = σ;
      this.γ = γ;
      this.q$ = q$;
      this.α = α == null ? null : new Word<>(α);
    }
    @Override public boolean equals(final Object o) {
      return o == this || o instanceof δ && equals((δ<?, ?, ?>) o);
    }
    public Word<Γ> getΑ() {
      return α;
    }
    @Override public int hashCode() {
      return 31
          * (q$.hashCode() + 31 * (γ.hashCode() + 31 * (31 * (q.hashCode() + 31) + (σ == null ? 1 : σ.hashCode()))))
          + getΑ().hashCode();
    }
    @Override public String toString() {
      return String.format("<%s,%s,%s,%s,%s>", q, σ == ε() ? "ε" : σ, γ, q$, getΑ());
    }
    private boolean equals(final δ<?, ?, ?> other) {
      return q.equals(other.q) && (σ == ε() ? other.σ == ε() : σ.equals(other.σ)) && γ.equals(other.γ)
          && q$.equals(other.q$) && getΑ().equals(other.getΑ());
    }
    /** @param currentq current state
     * @param currentσ current input letter
     * @param currentγ current stack symbol
     * @return whether this edge describes the next transition */
    boolean match(final Q currentq, final Σ currentσ, final Γ currentγ) {
      return q.equals(currentq) && Objects.equals(σ, currentσ) && γ.equals(currentγ);
    }
  }
}