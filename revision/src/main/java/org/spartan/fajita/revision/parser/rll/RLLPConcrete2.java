package org.spartan.fajita.revision.parser.rll;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.RLLP.Action;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.ActionType;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.Jump;
import org.spartan.fajita.revision.parser.rll.RLLP.Action.Push;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;

public class RLLPConcrete2 {
  protected final RLLP rllp;
  protected JSM2 jsm;
  protected boolean accept;
  protected boolean reject;
  protected boolean initialized;
  protected Symbol startSymbol;

  public RLLPConcrete2(BNF bnf) {
    this.rllp = new RLLP(bnf);
    this.jsm = new JSM2(rllp);
    accept = false;
    reject = false;
    initialized = false;
  }
  void push(Item... items) {
    push(Arrays.asList(items));
  }
  void push(List<Item> items) {
    jsm = jsm.pushAll(items);
  }
  void jump(Verb v) {
    jsm = jsm.jump(v);
  }
  Item pop() {
    Item $ = jsm.peek();
    jsm = jsm.pop();
    return $;
  }
  void reduce(@SuppressWarnings("unused") Item i) {
    //
  }
  // NOTE should be consistent with paper
  public RLLPConcrete2 consume(Verb t) {
    if (jsm == null)
      reject = true;
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
      if (SpecialSymbols.$.equals(t)) {
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
    assert ActionType.JUMP.equals(a.type()) : "JSM2 failure";
    jump(((Jump) a).v);
    return this;
  }
  public RLLPConcrete2 consume(Terminal t) {
    return consume(new Verb(t));
  }
  public RLLPConcrete2 consume(Verb... ts) {
    for (Verb t : ts)
      consume(t);
    return this;
  }
  public RLLPConcrete2 consume(Terminal... ts) {
    for (Terminal t : ts)
      try {
        consume(t);
      } catch (@SuppressWarnings("unused") RuntimeException e) {
        assert reject;
        break;
      }
    return this;
  }
  public boolean accepted() {
    if (reject || jsm == null)
      return false;
    if (accept)
      return true;
    List<Item> $ = jsm.getS0();
    Collections.reverse($);
    while (!$.isEmpty() && rllp.analyzer.isNullable(BNFAnalyzer.ruleSuffix($.get(0).rule, $.get(0).dotIndex))) {
      $.remove(0);
      if (!$.isEmpty())
        $.set(0, $.get(0).advance());
    }
    return $.isEmpty();
  }
  public boolean rejected() {
    return reject;
  }
}
