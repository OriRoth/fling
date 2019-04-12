package fling.grammars.api;

import static fling.grammars.api.BNFAPICompiler.parse_PlainBNF;

import fling.grammars.BNF;
import fling.grammars.api.BNFAPIAST.PlainBNF;
import fling.internal.grammar.sententials.*;
@SuppressWarnings("all") public interface BNFAPI {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone
  }

  public enum V implements Variable {
    PlainBNF, Rule, RuleBody, RuleTail
  }

  public static q0ø__Rule1$start_q0$q0ø<q0$_$_q0$<$>, $> bnf() {
    α α = new α();
    α.w.add(new fling.internal.compiler.Assignment(Σ.bnf));
    return α;
  }

  interface $ {
    BNF build();
  }

  interface q0$_$_q0$<q0$> extends $ {
  }

  interface q0ø__Rule1$start_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> start(fling.internal.grammar.sententials.Variable variable);
  }

  interface q0$__Rule1$_q0$q0ø<q0$, q0ø> extends $ {
    q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø> derive(fling.internal.grammar.sententials.Variable variable);
    q0ø__Rule1$into_q0$q0ø<q0$, q0ø> specialize(fling.internal.grammar.sententials.Variable variable);
  }

  interface q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø> {
    q0$__RuleTail1$_derivespecializeq0$q0ø<q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø>, q0ø__Rule1$into_q0$q0ø<q0$, q0ø>, q0$__Rule1$_q0$q0ø<q0$, q0ø>, q0$__Rule1$_q0$q0ø<q0$, q0ø>> to(
        fling.internal.grammar.sententials.Symbol... symbols);
    q0$__Rule1$_q0$q0ø<q0$, q0ø> toEpsilon();
  }

  interface q0ø__Rule1$into_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> into(fling.internal.grammar.sententials.Variable... variables);
  }

  interface q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> extends $ {
    derive derive(fling.internal.grammar.sententials.Variable variable);
    specialize specialize(fling.internal.grammar.sententials.Variable variable);
    q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> or(fling.internal.grammar.sententials.Symbol... symbols);
    q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> orNone();
  }

  static class α implements $, q0$_$_q0$, q0ø__Rule1$start_q0$q0ø, q0$__Rule1$_q0$q0ø, q0ø__Rule1$RuleBody$_q0$q0ø,
      q0ø__Rule1$into_q0$q0ø, q0$__RuleTail1$_derivespecializeq0$q0ø {
    public java.util.List<fling.internal.compiler.Assignment> w = new java.util.LinkedList();

    public α bnf() {
      this.w.add(new fling.internal.compiler.Assignment(Σ.bnf, new Object[] {}));
      return this;
    }
    public α start(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.start, new Object[] { variable }));
      return this;
    }
    public α derive(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.derive, new Object[] { variable }));
      return this;
    }
    public α specialize(fling.internal.grammar.sententials.Variable variable) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.specialize, new Object[] { variable }));
      return this;
    }
    public α into(fling.internal.grammar.sententials.Variable... variables) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.into, new Object[] { variables }));
      return this;
    }
    public α to(fling.internal.grammar.sententials.Symbol... symbols) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.to, new Object[] { symbols }));
      return this;
    }
    public α toEpsilon() {
      this.w.add(new fling.internal.compiler.Assignment(Σ.toEpsilon, new Object[] {}));
      return this;
    }
    public α or(fling.internal.grammar.sententials.Symbol... symbols) {
      this.w.add(new fling.internal.compiler.Assignment(Σ.or, new Object[] { symbols }));
      return this;
    }
    public α orNone() {
      this.w.add(new fling.internal.compiler.Assignment(Σ.orNone, new Object[] {}));
      return this;
    }
    public BNF build() {
      return BNF.toBNF(parse_PlainBNF(w));
    }
  }
}
