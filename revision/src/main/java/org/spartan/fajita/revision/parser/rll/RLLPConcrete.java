package org.spartan.fajita.revision.parser.rll;

import static org.spartan.fajita.revision.symbols.SpecialSymbols.$;

import java.util.Arrays;
import java.util.List;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.rll.RLLP.Action;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.ActionType;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.Jump;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.Push;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

public class RLLPConcrete {
  protected final RLLP rllp;
  protected final JSM jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected Symbol startSymbol;

  public RLLPConcrete(BNF bnf) {
    this.rllp = new RLLP(bnf);
    this.jsm = new JSM(rllp);
    accept = false;
    reject = false;
    initialized = false;
  }
  void push(Item... items) {
    push(Arrays.asList(items));
  }
  void push(List<Item> items) {
    jsm.pushAll(items);
  }
  void jump(Verb v) {
    jsm.jump(v);
  }
  Item pop() {
    return jsm.pop();
  }
  void reduce(@SuppressWarnings("unused") Item i) {
    //
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
      push(i);
      initialized = true;
    }
    Item i = pop();
    if (i.readyToReduce()) {
      reduce(i);
      if (!startSymbol.equals(i.rule.lhs)) {
        jump(t);
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
      push(i.advance());
      return this;
    }
    Action a = rllp.predict(i, t);
    if (a == null) {
      reject = true;
      return this;
    }
    if (ActionType.PUSH.equals(a.type())) {
      push(((Push) a).itemsToPush);
      return this;
    }
    assert ActionType.JUMP.equals(a.type()) : "JSM failure";
    jump(((Jump) a).v);
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
