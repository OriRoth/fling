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
  private final RLLP rllp;
  private final JSM jsm;
  private boolean accept;
  private boolean reject;
  private boolean initialized;
  private Symbol startSymbol;

  public RLLPConcrete(BNF bnf) {
    this.rllp = new RLLP(bnf);
    this.jsm = new JSM(rllp);
    accept = false;
    reject = false;
    initialized = false;
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
    return accept;
  }
  public boolean rejected() {
    return reject;
  }
}
