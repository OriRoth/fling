package org.spartan.fajita.revision.parser.rlr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Constants;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;

public class LRP {
  public final Set<Terminal> terminals;
  public final Set<NonTerminal> variables;
  public final Set<Symbol> symbols;
  public final Set<Rule> rules;
  public final Set<Item> q0;
  public final Set<NonTerminal> startVariables;
  private final Set<Symbol> nullables;
  private final Map<Symbol, Set<Terminal>> firsts;
  private final Map<Set<Item>, Map<Symbol, Set<Item>>> automata;
  private final Map<Set<Item>, Map<Symbol, Action>> actionTable;

  public LRP(Set<Terminal> terminals, Set<NonTerminal> variables, Set<Rule> rules, Set<NonTerminal> startVariables) {
    this.terminals = terminals;
    this.terminals.add(Constants.$);
    this.variables = variables;
    this.variables.add(Constants.augmentedStartSymbol);
    this.symbols = new LinkedHashSet<>(this.terminals);
    this.symbols.addAll(this.variables);
    this.startVariables = startVariables;
    this.rules = rules;
    for (NonTerminal nt : startVariables)
      this.rules.add(Rule.of(Constants.augmentedStartSymbol, nt));
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
      for (Rule r : rules)
        if (r.rhs.isEmpty() || r.rhs.stream().allMatch(s -> $.contains(s)))
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
      for (Rule r : rules) {
        Set<Terminal> fs = $.get(r.lhs);
        for (Symbol s : r.rhs) {
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
      q0.add(Item.of(Rule.of(Constants.augmentedStartSymbol, nt), 0, Constants.$));
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
  private Set<Item> closure(Set<Item> q) {
    int lastSize = 0;
    while (q.size() > lastSize) {
      lastSize = q.size();
      for (Item i : new LinkedHashSet<>(q))
        if (!i.readyToReduce())
          for (Rule r : rules)
            if (i.afterDot().equals(r.lhs))
              for (Terminal f : first(i.afterDotTail(), i.lookahead))
                q.add(Item.of(r, 0, f));
    }
    return q;
  }
  private Map<Set<Item>, Map<Symbol, Action>> generateActionTable() {
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
  private boolean isNullable(Symbol s) {
    return nullables.contains(s);
  }
  private Set<Terminal> first(List<Symbol> tail, Terminal lookahead) {
    List<Symbol> $ = new ArrayList<>(tail);
    $.add(lookahead);
    return first($);
  }
  private Set<Terminal> first(@SuppressWarnings("hiding") List<Symbol> symbols) {
    Set<Terminal> $ = new LinkedHashSet<>();
    for (Symbol s : symbols) {
      $.addAll(firsts.get(s));
      if (!isNullable(s))
        break;
    }
    return $;
  }
  public Action action(Set<Item> q, Symbol t) {
    if (!actionTable.containsKey(q))
      return Action.reject();
    Map<Symbol, Action> m = actionTable.get(q);
    if (!m.containsKey(t))
      return Action.reject();
    return m.get(t);
  }

  public static class Rule {
    public final NonTerminal lhs;
    public final List<Symbol> rhs;

    public Rule(NonTerminal lhs, List<Symbol> rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
    }
    public static Rule of(NonTerminal lhs, List<Symbol> rhs) {
      return new Rule(lhs, rhs);
    }
    public static Rule of(NonTerminal lhs, Symbol... rhs) {
      return new Rule(lhs, Arrays.asList(rhs));
    }
    @Override public int hashCode() {
      return Objects.hash(lhs, rhs);
    }
    @Override public boolean equals(Object obj) {
      return obj instanceof Rule && lhs.equals(((Rule) obj).lhs) && rhs.equals(((Rule) obj).rhs);
    }
    @Override public String toString() {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < rhs.size(); ++i)
        $.add(rhs.get(i).name());
      if ($.isEmpty())
        $.add(Constants.epsilon);
      return lhs + " ::= " + String.join(" ", $);
    }
  }

  public static class Item {
    public final Rule rule;
    public final int dot;
    public final Terminal lookahead;

    public Item(Rule rule, int dot, Terminal lookahead) {
      this.rule = rule;
      this.dot = dot;
      this.lookahead = lookahead;
    }
    public static Item of(Rule rule, int dot, Terminal lookahead) {
      return new Item(rule, dot, lookahead);
    }
    public boolean readyToReduce() {
      return dot == rule.rhs.size();
    }
    public Symbol afterDot() {
      return rule.rhs.get(dot);
    }
    public List<Symbol> afterDotTail() {
      return rule.rhs.subList(dot + 1, rule.rhs.size());
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
      for (int i = 0; i < rule.rhs.size() + 1; ++i) {
        if (i == dot)
          $.add(".");
        if (i < rule.rhs.size())
          $.add(rule.rhs.get(i).name());
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
    public final Rule rule;

    private Action(String tag, Set<Item> state, Rule rule) {
      this.tag = tag;
      this.state = state;
      this.rule = rule;
    }
    public static Action shift(Set<Item> state) {
      return new Action(SHIFT_TAG, state, null);
    }
    public static Action reduce(Rule rule) {
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
