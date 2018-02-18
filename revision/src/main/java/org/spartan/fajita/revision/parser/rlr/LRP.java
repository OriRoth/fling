package org.spartan.fajita.revision.parser.rlr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.Constants;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class LRP {
  public final Set<Terminal> terminals;
  public final Set<NonTerminal> variables;
  public final Set<Symbol> symbols;
  public final Set<DerivationRule> rules;
  public final Set<Item> q0;
  public final Set<NonTerminal> startVariables;
  private final Set<Symbol> nullables;
  private final Map<Symbol, Set<Terminal>> firsts;
  private final Map<Set<Item>, Map<Symbol, Set<Item>>> automata;
  private final Map<Set<Item>, Map<Symbol, Action>> actionTable;

  public LRP(Set<Terminal> terminals, Set<NonTerminal> variables, Set<DerivationRule> rules, Set<NonTerminal> startVariables) {
    this.terminals = terminals;
    this.terminals.add(Constants.$);
    this.variables = variables;
    this.variables.add(Constants.augmentedStartSymbol);
    this.symbols = new LinkedHashSet<>(this.terminals);
    this.symbols.addAll(this.variables);
    this.startVariables = startVariables;
    this.rules = rules;
    for (NonTerminal nt : startVariables)
      this.rules.add(DerivationRule.of(Constants.augmentedStartSymbol, nt));
    this.nullables = generateNullables();
    this.firsts = generateFirsts();
    this.q0 = new LinkedHashSet<>();
    this.automata = generateAutomata();
    this.actionTable = generateActionTable();
  }
  private Set<Symbol> generateNullables() {
    Set<Symbol> $ = new LinkedHashSet<>();
    int previousSize;
    do {
      previousSize = $.size();
      for (DerivationRule r : rules)
        if (r.getRHS().isEmpty() || r.getRHS().stream().allMatch(s -> $.contains(s)))
          $.add(r.lhs);
    } while ($.size() != previousSize);
    return $;
  }
  private Map<Symbol, Set<Terminal>> generateFirsts() {
    Map<Symbol, Set<Terminal>> $ = new LinkedHashMap<>();
    for (Symbol s : symbols)
      $.put(s, new LinkedHashSet<>());
    for (Terminal t : terminals)
      $.get(t).add(t);
    boolean changed;
    do {
      changed = false;
      for (DerivationRule r : rules) {
        Set<Terminal> fs = $.get(r.lhs);
        for (Symbol s : r.getRHS()) {
          changed |= fs.addAll($.get(s));
          if (!isNullable(s))
            break;
        }
      }
    } while (changed);
    return $;
  }
  private Map<Set<Item>, Map<Symbol, Set<Item>>> generateAutomata() {
    Map<Set<Item>, Map<Symbol, Set<Item>>> $ = new LinkedHashMap<>();
    for (NonTerminal nt : startVariables)
      q0.add(Item.of(DerivationRule.of(Constants.augmentedStartSymbol, nt), 0, Constants.$));
    q0.addAll(closure(q0));
    Set<Set<Item>> seen = new HashSet<>();
    Set<Set<Item>> unresolved = new LinkedHashSet<>();
    unresolved.add(q0);
    while (!unresolved.isEmpty()) {
      seen.addAll(unresolved);
      Set<Set<Item>> newUnresolved = new LinkedHashSet<>();
      for (Set<Item> q : unresolved) {
        $.put(q, new LinkedHashMap<>());
        for (Symbol s : symbols) {
          Set<Item> image = new LinkedHashSet<>();
          for (Item i : q)
            if (!i.readyToReduce() && i.afterDot().equals(s))
              image.add(i.next());
          if (image.isEmpty())
            continue;
          Set<Item> c = closure(image);
          $.get(q).put(s, c);
          if (!seen.contains(c))
            newUnresolved.add(closure(image));
        }
      }
      unresolved = newUnresolved;
    }
    return $;
  }
  public Set<Item> closure(Set<Item> q) {
    int lastSize = 0;
    while (q.size() > lastSize) {
      lastSize = q.size();
      for (Item i : new LinkedHashSet<>(q))
        if (!i.readyToReduce())
          for (DerivationRule r : rules)
            if (i.afterDot().equals(r.lhs))
              for (Terminal f : first(i.afterDotTail(), i.lookahead))
                q.add(Item.of(r, 0, f));
    }
    return q;
  }
  public Map<Set<Item>, Map<Symbol, Action>> generateActionTable() {
    Map<Set<Item>, Map<Symbol, Action>> $ = new LinkedHashMap<>();
    Set<Set<Item>> Q = automata.keySet();
    for (Set<Item> q : Q)
      $.put(q, new LinkedHashMap<>());
    for (Set<Item> q : Q) {
      Map<Symbol, Set<Item>> out = automata.get(q);
      for (Symbol s : out.keySet())
        if (terminals.contains(s))
          $.get(q).put(s, Action.shift(out.get(s)));
        else
          $.get(q).put(s, Action.move(out.get(s)));
    }
    for (Set<Item> q : Q)
      for (Item i : q)
        if (i.readyToReduce())
          $.get(q).put(i.lookahead, Action.reduce(i.rule));
    for (Set<Item> q : Q)
      for (Item i : q)
        if (Constants.augmentedStartSymbol.equals(i.rule.lhs) && i.readyToReduce() && Constants.$.equals(i.lookahead))
          $.get(q).put(Constants.$, Action.accept());
    return $;
  }
  public boolean isNullable(Symbol s) {
    return nullables.contains(s);
  }
  public Set<Terminal> first(List<Symbol> tail, Terminal lookahead) {
    List<Symbol> $ = new ArrayList<>(tail);
    $.add(lookahead);
    return first($);
  }
  public Set<Terminal> first(@SuppressWarnings("hiding") List<Symbol> symbols) {
    Set<Terminal> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(first(s));
      if (!isNullable(s))
        break;
    }
    return $;
  }
  public Set<Terminal> first(Symbol s) {
    return firsts.get(s);
  }
  public Action action(Set<Item> q, Symbol t) {
    if (!actionTable.containsKey(q))
      return Action.reject();
    Map<Symbol, Action> m = actionTable.get(q);
    if (!m.containsKey(t))
      return Action.reject();
    return m.get(t);
  }

  public static class Item {
    public final DerivationRule rule;
    public final int dot;
    public final Terminal lookahead;

    public Item(DerivationRule rule, int dot, Terminal lookahead) {
      this.rule = rule;
      this.dot = dot;
      this.lookahead = lookahead;
    }
    public static Item of(DerivationRule rule, int dot, Terminal lookahead) {
      return new Item(rule, dot, lookahead);
    }
    public boolean readyToReduce() {
      return dot == rule.getRHS().size();
    }
    public Symbol afterDot() {
      return rule.getRHS().get(dot);
    }
    public List<Symbol> afterDotTail() {
      return rule.getRHS().subList(dot + 1, rule.getRHS().size());
    }
    public Item next() {
      return new Item(rule, dot + 1, lookahead);
    }
    @Override public int hashCode() {
      return Objects.hash(rule, Integer.valueOf(dot), lookahead);
    }
    @Override public boolean equals(Object obj) {
      return obj instanceof Item && rule.equals(((Item) obj).rule) && dot == ((Item) obj).dot
          && lookahead.equals(((Item) obj).lookahead);
    }
    @Override public String toString() {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < rule.getRHS().size() + 1; ++i) {
        if (i == dot)
          $.add(".");
        if (i < rule.getRHS().size())
          $.add(rule.getRHS().get(i).name());
      }
      return rule.lhs + " ::= " + String.join(" ", $) + "|" + lookahead.name();
    }
  }

  public static class Action {
    private static final String SHIFT_TAG = "s";
    private static final String REDUCE_TAG = "r";
    private static final String MOVE_TAG = "m";
    private static final String ACCEPT_TAG = "a";
    private static final String REJECT_TAG = "x";
    private final String tag;
    public final Set<Item> state;
    public final DerivationRule rule;

    private Action(String tag, Set<Item> state, DerivationRule rule) {
      this.tag = tag;
      this.state = state;
      this.rule = rule;
    }
    public static Action shift(Set<Item> state) {
      return new Action(SHIFT_TAG, state, null);
    }
    public static Action reduce(DerivationRule rule) {
      return new Action(REDUCE_TAG, null, rule);
    }
    public static Action move(Set<Item> state) {
      return new Action(MOVE_TAG, state, null);
    }
    public static Action accept() {
      return new Action(ACCEPT_TAG, null, null);
    }
    public static Action reject() {
      return new Action(REJECT_TAG, null, null);
    }
    public boolean isShift() {
      return SHIFT_TAG.equals(tag);
    }
    public boolean isReduce() {
      return REDUCE_TAG.equals(tag);
    }
    public boolean isMove() {
      return MOVE_TAG.equals(tag);
    }
    public boolean isAccept() {
      return ACCEPT_TAG.equals(tag);
    }
    public boolean isReject() {
      return REJECT_TAG.equals(tag);
    }
    @Override public String toString() {
      return tag;
    }
  }
}
