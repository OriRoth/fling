package org.spartan.fajita.revision.parser.rlr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.parser.rlr.LRP.Action;
import org.spartan.fajita.revision.parser.rlr.LRP.Item;
import org.spartan.fajita.revision.symbols.Constants;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.util.ParserTerminated;

public class RLRA {
  public final LRP lrp;
  private Stack<Set<Item>> qs;
  private Stack<Map<NonTerminal, Map<Terminal, J>>> js;
  public boolean accepted;
  public boolean rejected;
  private Stack<Map<NonTerminal, Map<Terminal, J>>> ukjs;

  public RLRA(Set<Terminal> terminals, Set<NonTerminal> variables, Set<DerivationRule> rules, Set<NonTerminal> startVariables) {
    lrp = new LRP(terminals, variables, rules, startVariables);
    qs = new Stack<>();
    js = new Stack<>();
    ukjs = new Stack<>();
  }
  public RLRA(LRP lrp) {
    this.lrp = lrp;
    this.qs = new Stack<>();
    this.js = new Stack<>();
    this.ukjs = new Stack<>();
  }
  public RLRA initialize() {
    accepted = rejected = false;
    qs.clear();
    js.clear();
    push(lrp.q0);
    return this;
  }
  private void push(Set<Item> q) {
    js.push(jumps(q));
    qs.push(q);
    Stack<Map<NonTerminal, Map<Terminal, J>>> tjs = js;
    Stack<Set<Item>> tqs = qs;
    js = new Stack<>();
    js.addAll(tjs);
    qs = new Stack<>();
    qs.addAll(tqs);
  }
  public boolean accept(List<Terminal> ts) {
    List<Terminal> input = new ArrayList<>(ts);
    input.add(Constants.$);
    for (Terminal c : input)
      try {
        consume(c);
      } catch (@SuppressWarnings("unused") ParserTerminated e) {
        return false;
      }
    return accepted;
  }
  public boolean reject(List<Terminal> ts) {
    List<Terminal> input = new ArrayList<>(ts);
    input.add(Constants.$);
    for (Terminal c : input)
      try {
        consume(c);
      } catch (@SuppressWarnings("unused") ParserTerminated e) {
        return rejected;
      }
    return rejected;
  }
  public void consume(Terminal t) {
    if (accepted)
      throw new ParserTerminated("Parser has already accepted");
    if (rejected)
      throw new ParserTerminated("Parser has already rejected");
    Set<Item> q = qs.peek();
    Action a = lrp.action(q, t);
    if (a.isReject()) {
      rejected = true;
      return;
    }
    if (a.isAccept()) {
      accepted = true;
      return;
    }
    if (a.isShift()) {
      push(a.state);
      return;
    }
    if (a.isReduce()) {
      for (int i = 0; i < a.rule.getRHS().size(); ++i) {
        qs.pop();
        js.pop();
      }
      J j = js.peek().get(a.rule.lhs).get(t);
      if (j == null) {
        rejected = true;
        return;
      }
      if (j.isAccept) {
        accepted = true;
        return;
      }
      qs.clear();
      js.clear();
      qs.addAll(j.qsAddress);
      js.addAll(j.jsAddress);
      for (Set<Item> qq : j.toPush)
        push(qq);
      return;
    }
    throw new RuntimeException("Unreachable");
  }
  private Map<NonTerminal, Map<Terminal, J>> jumps(Set<Item> q) {
    Map<NonTerminal, Map<Terminal, J>> $ = new LinkedHashMap<>();
    for (NonTerminal v : lrp.variables) {
      $.put(v, new LinkedHashMap<>());
      Action a = lrp.action(q, v);
      if (a.isReject())
        continue;
      assert a.isMove();
      outer: for (Terminal t : lrp.terminals) {
        NonTerminal vv = v;
        Stack<Set<Item>> toPush = new Stack<>();
        toPush.push(a.state);
        for (;;) {
          Action aa = lrp.action(toPush.peek(), t);
          if (aa.isReject())
            continue outer;
          if (aa.isAccept()) {
            $.get(v).put(t, J.acceptance());
            continue outer;
          }
          if (aa.isShift()) {
            toPush.push(aa.state);
            $.get(v).put(t, J.record(qs, js, toPush));
            continue outer;
          }
          assert aa.isReduce();
          DerivationRule rr = aa.rule;
          vv = rr.lhs;
          int l = rr.getRHS().size();
          while (l > 0 && !toPush.isEmpty()) {
            --l;
            toPush.pop();
          }
          if (l > 0) {
            Map<NonTerminal, Map<Terminal, J>> ms = l <= js.size() ? js.get(js.size() - l) : ukjs.get(ukjs.size() + js.size() - l);
            if (ms.get(vv).containsKey(t))
              $.get(v).put(t, ms.get(vv).get(t));
            continue outer;
          }
          Action aaa = lrp.action(toPush.isEmpty() ? q : toPush.peek(), vv);
          if (aaa.isReject())
            continue outer;
          assert aaa.isMove();
          toPush.push(aaa.state);
        }
      }
    }
    return $;
  }

  public static class J {
    public static final J UNKNOWN = new J();
    public final Stack<Set<Item>> qsAddress;
    public final Stack<Map<NonTerminal, Map<Terminal, J>>> jsAddress;
    public final List<Set<Item>> toPush;
    public final boolean isAccept;

    private J(Stack<Set<Item>> qsAddress, Stack<Map<NonTerminal, Map<Terminal, J>>> jsAddress, List<Set<Item>> toPush) {
      this.qsAddress = qsAddress;
      this.jsAddress = jsAddress;
      this.toPush = toPush;
      this.isAccept = false;
    }
    private J() {
      this.qsAddress = null;
      this.jsAddress = null;
      this.toPush = null;
      this.isAccept = true;
    }
    public static J record(Stack<Set<Item>> qsAddress, Stack<Map<NonTerminal, Map<Terminal, J>>> jsAddress,
        List<Set<Item>> toPush) {
      return new J(qsAddress, jsAddress, toPush);
    }
    public static J acceptance() {
      return new J();
    }
  }
}