package org.spartan.fajita.api.rllp;

import static org.spartan.fajita.api.bnf.symbols.SpecialSymbols.$;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.ActionType;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

public class RLLPConcrete {
  private final BNF bnf;
  private final RLLP rllp;
  private final JSM jsm;
  private boolean accept;
  private boolean reject;
  private boolean initialized;

  public RLLPConcrete(BNF bnf) {
    this.bnf = bnf;
    this.rllp = new RLLP(bnf);
    this.jsm = new JSM(rllp);
    accept = false;
    reject = false;
    initialized = false;
  }
  // NOTE should be according to paper
  public void consume(Verb t) {
    if (accept)
      throw new RuntimeException("Parser has already accepted");
    if (reject)
      throw new RuntimeException("Parser has already rejected");
    if (!initialized) {
      jsm.push(rllp.getStartItem(t));
      initialized = true;
    }
    Item i = jsm.pop();
    if (i.readyToReduce()) {
      if (!bnf.getStartSymbols().contains(i.rule.lhs)) {
        jsm.jump(t);
        return;
      }
      if ($.equals(t)) {
        accept = true;
        return;
      }
      reject = true;
      return;
    }
    if (i.afterDot().isVerb()) {
      if (!i.afterDot().equals(t)) {
        reject = true;
        return;
      }
      jsm.push(i.advance());
      return;
    }
    Action a = rllp.predict(i, t);
    if (a == null) {
      reject = true;
      return;
    }
    if (ActionType.PUSH.equals(a.type())) {
      jsm.pushAll(((Push) a).itemsToPush);
      return;
    }
    assert ActionType.JUMP.equals(a.type()) : "JSM failure";
    jsm.jump(((Jump) a).v);
  }
  public void consume(Terminal t) {
    consume(new Verb(t));
  }
  public void consume(Verb... ts) {
    for (Verb t : ts)
      consume(t);
  }
  public void consume(Terminal... ts) {
    for (Terminal t : ts)
      consume(t);
  }
}
