package org.spartan.fajita.api.rllp;

import static org.spartan.fajita.api.bnf.symbols.SpecialSymbols.$;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.ActionType;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

public class RLLPConcrete {
  protected final RLLP rllp;
  protected final JSM jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected Symbol startSymbol;

  public RLLPConcrete(BNF bnf) {
    this.rllp = new RLLP(bnf);
    this.jsm = getJSM();
    accept = false;
    reject = false;
    initialized = false;
  }
  public JSM getJSM() {
    return new JSM(rllp);
  }
  // NOTE should be consistent with paper
  public RLLPConcrete consume(Verb t) {
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      Item i = rllp.getStartItem(t);
      startSymbol = i.rule.lhs;
      jsm.push(i);
      initialized = true;
    }
    Item i = jsm.pop();
    if (i.readyToReduce()) {
      if (!startSymbol.equals(i.rule.lhs)) {
        jsm.jump(t);
        return this;
      }
      if ($.equals(t)) {
        accept = true;
        return this;
      }
      reject = true;
      return this;
    }
    if (i.afterDot().isVerb()) {
      if (!i.afterDot().equals(t)) {
        reject = true;
        return this;
      }
      jsm.push(i.advance());
      return this;
    }
    Action a = rllp.predict(i, t);
    if (a == null) {
      reject = true;
      return this;
    }
    if (ActionType.PUSH.equals(a.type())) {
      jsm.pushAll(((Push) a).itemsToPush);
      return this;
    }
    assert ActionType.JUMP.equals(a.type()) : "JSM failure";
    jsm.jump(((Jump) a).v);
    return this;
  }
  public RLLPConcrete consume(Terminal t) {
    return consume(new Verb(t));
  }
  public RLLPConcrete consume(Verb... ts) {
    for (Verb t : ts)
      consume(t);
    return this;
  }
  public RLLPConcrete consume(Terminal... ts) {
    for (Terminal t : ts)
      consume(t);
    return this;
  }
  public boolean accepted() {
    return !reject && (accept || jsm.getS0().stream().allMatch(x -> x.readyToReduce()));
  }
  public boolean rejected() {
    return reject;
  }
}
