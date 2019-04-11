package fling.grammars.api;

@SuppressWarnings("all")
public interface BNFAPI {
  public static q0ø__Rule1$start_q0$q0ø<q0$_$_q0$<$>, $> bnf() {
    α α = new α();
    α.w.add(new fling.internal.compiler.Assignment(BNF.Σ.bnf));
    return α;
  }

  interface $ {
    fling.grammars.BNF build();
  }

  interface q0$_$_q0$<q0$> extends $ {}

  interface q0ø__Rule1$start_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> start(fling.internal.grammar.sententials.Variable variable);
  }

  interface q0$__Rule1$_q0$q0ø<q0$, q0ø> extends $ {
    q0ø__Rule1$DerivationTarget$_q0$q0ø<q0$, q0ø> derive(
        fling.internal.grammar.sententials.Variable variable);

    q0ø__Rule1$into_q0$q0ø<q0$, q0ø> specialize(
        fling.internal.grammar.sententials.Variable variable);
  }

  interface q0ø__Rule1$DerivationTarget$_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> to(fling.internal.grammar.sententials.Symbol... symbols);

    q0$__Rule1$_q0$q0ø<q0$, q0ø> toEpsilon();
  }

  interface q0ø__Rule1$into_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> into(fling.internal.grammar.sententials.Variable... variables);
  }

  static class α
      implements $,
          q0$_$_q0$,
          q0ø__Rule1$start_q0$q0ø,
          q0$__Rule1$_q0$q0ø,
          q0ø__Rule1$DerivationTarget$_q0$q0ø,
          q0ø__Rule1$into_q0$q0ø {
    public java.util.List<fling.internal.compiler.Assignment> w = new java.util.LinkedList();

    public α bnf() {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.bnf, new Object[] {}));
      return this;
    }

    public α start(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.start, new Object[] {variable}));
      return this;
    }

    public α derive(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.derive, new Object[] {variable}));
      return this;
    }

    public α specialize(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.specialize, new Object[] {variable}));
      return this;
    }

    public α into(fling.internal.grammar.sententials.Variable... variables) {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.into, new Object[] {variables}));
      return this;
    }

    public α to(fling.internal.grammar.sententials.Symbol... symbols) {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.to, new Object[] {symbols}));
      return this;
    }

    public α toEpsilon() {
      this.w.add(
          new fling.internal.compiler.Assignment(
              BNF.Σ.toEpsilon, new Object[] {}));
      return this;
    }

    public fling.grammars.BNF build() {
      return fling.grammars.BNF.toBNF(BNFCompiler.parse_Specification(w));
    }
  }
}

