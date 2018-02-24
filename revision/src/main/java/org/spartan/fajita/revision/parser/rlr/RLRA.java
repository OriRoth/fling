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

public class RLRA implements Cloneable {
  public final LRP lrp;
  private final Stack<Set<Item>> qs;
  private final Stack<Map<NonTerminal, Map<Terminal, J>>> js;
  public boolean accepted;
  public boolean rejected;

  public RLRA(Set<Terminal> terminals, Set<NonTerminal> variables, Set<DerivationRule> rules, Set<NonTerminal> startVariables) {
    lrp = new LRP(terminals, variables, rules, startVariables);
    qs = new Stack<>();
    js = new Stack<>();
  }
  private RLRA(LRP lrp) {
    this.lrp = lrp;
    qs = new Stack<>();
    js = new Stack<>();
  }
  @Override protected RLRA clone() {
    RLRA $ = new RLRA(lrp);
    $.qs.addAll(qs);
    $.js.addAll(js);
    return $;
  }
  /* Structural Interface */
  public RLRA shift(Set<Item> q) {
    RLRA $ = clone();
    $.qs.push(q);
    $.js.push(jumps());
    return $;
  }
  public J reduce(Terminal t, DerivationRule rule) {
    int l = rule.getRHS().size() + 1;
    if (qs.size() < l)
      return J.unknown(l - qs.size());
    return js.get(qs.size() - l).get(rule.lhs).get(t);
  }
  public RLRA jump(DerivationRule rule) {
    RLRA $ = clone();
    for (int i = 0; i < rule.getRHS().size(); ++i) {
      $.qs.pop();
      $.js.pop();
    }
    return $;
  }
  public RLRA jump(DerivationRule rule, J j) {
    RLRA $ = jump(rule);
    for (int i = 0; i < j.toPop; ++i) {
      $.qs.pop();
      $.js.pop();
    }
    for (Set<Item> qq : j.toPush) {
      $.qs.push(qq);
      $.js.push(jumps());
    }
    return $;
  }
  public int size() {
    return qs.size();
  }
  public Set<Item> peek() {
    return qs.peek();
  }
  /* Parsing Interface */
  public RLRA initialize() {
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
            if (l > js.size()) {
              $.get(v).put(t, J.unknown(l - js.size()));
              continue outer;
            }
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
    public final int unknownDepth;

    private J(int toPop, List<Set<Item>> toPush) {
      this.toPop = toPop;
      this.toPush = toPush;
      this.isAccept = false;
      this.unknownDepth = -1;
    }
    private J() {
      this.toPop = -1;
      this.toPush = null;
      this.isAccept = true;
      this.unknownDepth = -1;
    }
    private J(int depth) {
      this.toPop = -1;
      this.toPush = null;
      this.isAccept = false;
      this.unknownDepth = depth;
    }
    public boolean isUnknown() {
      return unknownDepth > 0;
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
    public static J unknown(int depth) {
      return new J(depth);
    }
    @Override public String toString() {
      return isAccept ? "(accept)" : "(" + toPop + "," + toPush + ")";
    }
  }
}
