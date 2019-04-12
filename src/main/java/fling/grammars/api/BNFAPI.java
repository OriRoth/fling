package fling.grammars.api;

import fling.internal.grammar.sententials.*;

@SuppressWarnings("all") public interface BNFAPI {
  enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon
  }

  enum V implements Variable {
    Specification, Rule, DerivationTarget
  }

  static q0ø__Rule1$start_q0$q0ø<q0$_$_q0$<$>, $> bnf() {
    final α α = new α();
    α.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.bnf));
    return α;
  }

  interface $ {
    fling.grammars.BNF build();
  }

  interface q0$_$_q0$<q0$> extends $ {
  }

  interface q0ø__Rule1$start_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> start(fling.internal.grammar.sententials.Variable variable);
  }

  interface q0$__Rule1$_q0$q0ø<q0$, q0ø> extends $ {
    q0ø__Rule1$DerivationTarget$_q0$q0ø<q0$, q0ø> derive(fling.internal.grammar.sententials.Variable variable);
    q0ø__Rule1$into_q0$q0ø<q0$, q0ø> specialize(fling.internal.grammar.sententials.Variable variable);
  }

  interface q0ø__Rule1$DerivationTarget$_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> to(fling.internal.grammar.sententials.Symbol... symbols);
    q0$__Rule1$_q0$q0ø<q0$, q0ø> toEpsilon();
  }

  interface q0ø__Rule1$into_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> into(fling.internal.grammar.sententials.Variable... variables);
  }

  static class α implements $, q0$_$_q0$, q0ø__Rule1$start_q0$q0ø, q0$__Rule1$_q0$q0ø, q0ø__Rule1$DerivationTarget$_q0$q0ø,
      q0ø__Rule1$into_q0$q0ø {
    public java.util.List<fling.internal.compiler.Assignment> w = new java.util.LinkedList();

    public α bnf() {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.bnf, new Object[] {}));
      return this;
    }
    @Override public α start(final fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.start, new Object[] { variable }));
      return this;
    }
    @Override public α derive(final fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.derive, new Object[] { variable }));
      return this;
    }
    @Override public α specialize(final fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.specialize, new Object[] { variable }));
      return this;
    }
    @Override public α into(final fling.internal.grammar.sententials.Variable... variables) {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.into, new Object[] { variables }));
      return this;
    }
    @Override public α to(final fling.internal.grammar.sententials.Symbol... symbols) {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.to, new Object[] { symbols }));
      return this;
    }
    @Override public α toEpsilon() {
      this.w.add(new fling.internal.compiler.Assignment(BNFAPI.Σ.toEpsilon, new Object[] {}));
      return this;
    }
    @Override public fling.grammars.BNF build() {
      return fling.grammars.BNF.toBNF(BNFCompiler.parse_Specification(w));
    }
  }
}
