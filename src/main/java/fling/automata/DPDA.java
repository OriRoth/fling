package fling.automata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import fling.sententials.Word;
import static fling.sententials.Alphabet.ε;

/**
 * Deterministic pushdown automaton (DPDA) supporting acceptance by final state.
 * 
 * @author Ori Roth
 * @param <Q> states enum
 * @param <Σ> alphabet enum
 * @param <Γ> stack symbols enum
 */
public class DPDA<Q, Σ, Γ> {
  public final Set<Q> Q;
  public final Set<Σ> Σ;
  public final Set<Γ> Γ;
  public final Set<δ<Q, Σ, Γ>> δs;
  public final Set<Q> q$;
  public final Q q0;
  public final Γ γ0;

  public DPDA(final Set<Q> Q, final Set<Σ> Σ, final Set<Γ> Γ, final Set<δ<Q, Σ, Γ>> δs, final Set<Q> q$, final Q q0, final Γ γ0) {
    this.Q = Collections.unmodifiableSet(Q);
    this.Σ = Collections.unmodifiableSet(Σ);
    this.Γ = Collections.unmodifiableSet(Γ);
    this.δs = Collections.unmodifiableSet(δs);
    this.q$ = Collections.unmodifiableSet(q$);
    this.q0 = q0;
    this.γ0 = γ0;
  }
  public static <Q, Σ, Γ> Builder<Q, Σ, Γ> dpda(final Set<Q> Q, final Set<Σ> Σ, final Set<Γ> Γ) {
    return new Builder<>(Q, Σ, Γ);
  }
  /**
   * @param q current state
   * @param σ current input letter
   * @param γ current stack symbol
   * @return matching transition
   */
  public δ<Q, Σ, Γ> δ(final Q q, final Σ σ, final Γ γ) {
    for (final δ<Q, Σ, Γ> δ : δs)
      if (δ.match(q, σ, γ))
        return δ;
    return null;
  }
  /**
   * @param q a state
   * @return whether this is an accepting state
   */
  public boolean isAccepting(final Q q) {
    return q$.contains(q);
  }
  /**
   * Returns matching consolidated transition, i.e., the result of the multiple
   * transitions initiated by the received configuration.
   * 
   * @param q current state
   * @param σ current input letter
   * @param γ current stack symbol
   * @return matching consolidated transition
   */
  public δ<Q, Σ, Γ> δδ(final Q q, final Σ σ, final Γ γ) {
    Q q$ = q;
    Word<Γ> s = new Word<>(γ);
    if (σ != null) { // Consuming transition.
      final δ<Q, Σ, Γ> δ = δ(q, σ, s.top());
      if (δ == null)
        return null;
      q$ = δ.q$;
      s = s.pop().push(δ.α);
    }
    // subsequent ε transitions.
    for (;;) {
      if (s.isEmpty())
        return new δ<>(q, σ, γ, q$, s);
      final δ<Q, Σ, Γ> δ = δ(q$, ε(), s.top());
      if (δ == null)
        return new δ<>(q, σ, γ, q$, s);
      s = s.pop().push(δ.α);
      q$ = δ.q$;
    }
  }

  /**
   * {@link DPDA} builder. Does not check the correctness of the automaton,
   * i.e., it assumes it is deterministic and cannot loop infinitely.
   */
  public static class Builder<Q, Σ, Γ> {
    private final Set<Q> Q;
    private final Set<Σ> Σ;
    private final Set<Γ> Γ;
    private final Set<δ<Q, Σ, Γ>> δs;
    private final Set<Q> F;
    private Q q0;
    private Γ γ0;

    public Builder(final Set<Q> Q, final Set<Σ> Σ, final Set<Γ> Γ) {
      this.Q = Q;
      this.Σ = Σ;
      this.Γ = Γ;
      this.δs = new LinkedHashSet<>();
      this.F = new LinkedHashSet<>();
    }
    @SafeVarargs public final Builder<Q, Σ, Γ> δ(final Q q, final Σ σ, final Γ γ, final Q q$, final Γ... α) {
      δs.add(new δ<>(q, σ, γ, q$, new Word<>(α)));
      return this;
    }
    @SafeVarargs public final Builder<Q, Σ, Γ> F(final Q... qs) {
      Collections.addAll(F, qs);
      return this;
    }
    public Builder<Q, Σ, Γ> q0(final Q q0) {
      this.q0 = q0;
      return this;
    }
    public Builder<Q, Σ, Γ> γ0(final Γ γ0) {
      this.γ0 = γ0;
      return this;
    }
    public DPDA<Q, Σ, Γ> go() {
      assert q0 != null;
      assert γ0 != null;
      return new DPDA<>(Q, Σ, Γ, δs, F, q0, γ0);
    }
  }

  /**
   * An automaton edge. A set of edges is a transition function.
   */
  public static class δ<Q, Σ, Γ> {
    final Q q;
    final Σ σ;
    final Γ γ;
    final Q q$;
    final Word<Γ> α;

    public δ(final Q q, final Σ σ, final Γ γ, final Q q$, final Word<Γ> α) {
      this.q = q;
      this.σ = σ;
      this.γ = γ;
      this.q$ = q$;
      this.α = α == null ? null : new Word<>(α);
    }
    /**
     * @param currentq current state
     * @param currentσ current input letter
     * @param currentγ current stack symbol
     * @return whether this edge describes the next transition
     */
    public boolean match(final Q currentq, final Σ currentσ, final Γ currentγ) {
      return q.equals(currentq) && (this.σ == null ? currentσ == null : this.σ.equals(currentσ)) && this.γ.equals(currentγ);
    }
    @Override public int hashCode() {
      return 31 * (q$.hashCode() + 31 * (γ.hashCode() + 31 * (31 * (q.hashCode() + 31) + (σ == null ? 1 : σ.hashCode()))))
          + α.hashCode();
    }
    @Override public boolean equals(final Object o) {
      return o == this || o instanceof δ && equals((δ<?, ?, ?>) o);
    }
    private boolean equals(final δ<?, ?, ?> other) {
      return q.equals(other.q) && (σ == ε() && other.σ == ε() || σ != ε() && σ.equals(other.σ)) && γ.equals(other.γ)
          && q$.equals(other.q$) && α.equals(other.α);
    }
    @Override public String toString() {
      return String.format("<%s,%s,%s,%s,%s>", q, σ != ε() ? σ : "ε", γ, q$, α);
    }
  }
}