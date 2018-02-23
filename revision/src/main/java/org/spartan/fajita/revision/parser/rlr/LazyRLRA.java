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

public class LazyRLRA {
  public final LRP lrp;
  private final Stack<Set<Item>> qs;
  private final Stack<Map<NonTerminal, Map<Terminal, J>>> js;
  public boolean accepted;
  public boolean rejected;

  public LazyRLRA(Set<Terminal> terminals, Set<NonTerminal> variables, Set<DerivationRule> rules, Set<NonTerminal> startVariables) {
    lrp = new LRP(terminals, variables, rules, startVariables);
    qs = new Stack<>();
    js = new Stack<>();
  }
  public LazyRLRA initialize() {
    accepted = rejected = false;
    qs.clear();
    js.clear();
    qs.push(lrp.q0);
    js.push(jumps());
    return this;
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
      qs.push(a.state);
      js.push(jumps());
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
      for (int i = 0; i < j.toPop; ++i) {
        qs.pop();
        js.pop();
      }
      for (Set<Item> qq : j.toPush) {
        qs.push(qq);
        js.push(jumps());
      }
      return;
    }
    throw new RuntimeException("Unreachable");
  }
  private Map<NonTerminal, Map<Terminal, J>> jumps() {
    Map<NonTerminal, Map<Terminal, J>> $ = new LinkedHashMap<>();
    Set<Item> q = qs.peek();
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
            $.get(v).put(t, J.record(0, toPush));
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
            Map<NonTerminal, Map<Terminal, J>> ms = js.get(js.size() - l);
            if (ms.get(vv).containsKey(t))
              $.get(v).put(t, J.elaborate(ms.get(vv).get(t), l));
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
    public final int toPop;
    public final List<Set<Item>> toPush;
    public final boolean isAccept;

    private J(int toPop, List<Set<Item>> toPush) {
      this.toPop = toPop;
      this.toPush = toPush;
      this.isAccept = false;
    }
    private J() {
      this.toPop = -1;
      this.toPush = null;
      this.isAccept = true;
    }
    public static J record(int toPop, List<Set<Item>> toPush) {
      return new J(toPop, toPush);
    }
    public static J elaborate(J j, int moreToPop) {
      return j.isAccept ? new J() : new J(j.toPop + moreToPop, j.toPush);
    }
    public static J acceptance() {
      return new J();
    }
    @Override public String toString() {
      return isAccept ? "(accept)" : "(" + toPop + "," + toPush + ")";
    }
  }
}
