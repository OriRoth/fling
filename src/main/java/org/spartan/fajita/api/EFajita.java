package org.spartan.fajita.api;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.api.bnf.BNF.nonTerminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.ENonTerminal;
import org.spartan.fajita.api.bnf.EVerb;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.bnf.symbols.type.NestedType;
import org.spartan.fajita.api.bnf.symbols.type.ParameterType;

public class EFajita extends Fajita {
  private Map<NonTerminal, Integer> counter = an.empty.map();
  Function<NonTerminal, String> namer = lhs -> {
    counter.putIfAbsent(lhs, Integer.valueOf(1));
    return lhs.name() /* + "$" */ + counter.put(lhs, Integer.valueOf(counter.get(lhs).intValue() + 1));
  };
  final List<DerivationRule> classDerivationRules;

  @Override protected Fajita checkNewRule(final DerivationRule r) {
    if (derivationRules.contains(r))
      throw new IllegalArgumentException("rule " + r + " already exists");
    return this;
  }
  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> EFajita(final Class<Term> terminalEnum,
      final Class<NT> nonterminalEnum) {
    super(terminalEnum, nonterminalEnum);
    classDerivationRules = an.empty.list();
  }
  public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> SetSymbols build(
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
    EFajita builder = new EFajita(terminalEnum, nonterminalEnum);
    return builder.new SetSymbols();
  }

  public class SetSymbols {
    public ApiName setApiName(String name) {
      EFajita.this.setApiName(name);
      return new ApiName();
    }
  }

  public class ApiName {
    public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
      EFajita.this.getStartSymbols().add(nt);
      Collections.addAll(EFajita.this.getStartSymbols(), nts);
      return new FirstDerive();
    }
  }

  public abstract class Deriver {
    protected final NonTerminal lhs;
    protected final ArrayList<Symbol> symbols;

    public Deriver(final NonTerminal lhs, final Symbol... symbols) {
      this.lhs = lhs;
      this.symbols = new ArrayList<>(Arrays.asList(symbols));
    }
    public InitialDeriver derive(final NonTerminal newRuleLHS) {
      if (!symbols.isEmpty())
        addRuleToBNF();
      return new InitialDeriver(newRuleLHS);
    }
    public InitialSpecializeDeriver specialize(final NonTerminal newRuleLHS) {
      if (!symbols.isEmpty())
        addRuleToBNF();
      return new InitialSpecializeDeriver(newRuleLHS);
    }
    public Map<String, String> go(String pckg) {
      solve();
      return finish(pckg);
    }
    protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
    public BNF go() {
      solve();
      return new BNF(getVerbs(), getNonTerminals(), getRules(), getStartSymbols(), getApiName());
    }
  }

  public class InitialDeriver {
    private final NonTerminal lhs;

    InitialDeriver(final NonTerminal lhs) {
      this.lhs = lhs;
    }
    public AndDeriver to(final Symbol s, final Symbol... ss) {
      AndDeriver $ = new AndDeriver(lhs, s);
      for (Symbol x : ss)
        $.and(x);
      return $;
    }
    public OrDeriver toNone() {
      addRule(lhs, Collections.emptyList());
      return new OrDeriver(lhs);
    }
  }

  public class InitialSpecializeDeriver {
    private final NonTerminal lhs;

    InitialSpecializeDeriver(final NonTerminal lhs) {
      this.lhs = lhs;
    }
    // TODO Roth: allow ENonTerminals?
    public Deriver into(final NonTerminal s, final NonTerminal... ss) {
      OrDeriver $ = new InitialDeriver(lhs).to(s);
      for (Symbol x : ss)
        $ = $.or(x);
      return $;
    }
  }

  public class OrDeriver extends Deriver {
    OrDeriver(final NonTerminal lhs) {
      super(lhs);
    }
    public AndDeriver or(final Symbol s, Symbol... ss) {
      AndDeriver $ = new AndDeriver(lhs, s);
      for (Symbol x : ss)
        $.and(x);
      return or($);
    }
    private AndDeriver or(AndDeriver deriver) {
      addRule(lhs, symbols);
      return deriver;
    }
    public OrDeriver orNone() {
      return derive(lhs).toNone();
    }
  }

  public final class AndDeriver extends OrDeriver {
    AndDeriver(final NonTerminal lhs, final Symbol child) {
      super(lhs);
      symbols.add(child);
    }
    public AndDeriver and(final Symbol s, Symbol... ss) {
      symbols.add(s);
      for (Symbol x : ss)
        symbols.add(x);
      return this;
    }
    @Override public Map<String, String> go(String pckg) {
      addRuleToBNF();
      return super.go(pckg);
    }
    @Override public BNF go() {
      addRuleToBNF();
      solve();
      return new BNF(getVerbs(), getNonTerminals(), getRules(), classDerivationRules, getStartSymbols(), getApiName());
    }
  }

  public class FirstDerive {
    public InitialDeriver derive(final NonTerminal nt) {
      return new InitialDeriver(nt);
    }
    public InitialSpecializeDeriver specialize(final NonTerminal nt) {
      return new InitialSpecializeDeriver(nt);
    }
  }

  public static abstract class Head extends ENonTerminal {
    Symbol head;
    public List<Symbol> symbols;

    public Head(List<Symbol> symbols) {
      this.symbols = symbols;
    }
    @Override public Symbol head() {
      return head;
    }
  }

  public static class Either extends Head {
    public Either(List<Symbol> symbols) {
      super(symbols);
    }
    @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      List<Symbol> $ = builder.solve(lhs, symbols);
      head = nonTerminal(builder.namer.apply(lhs));
      for (Symbol s : $)
        builder.addRule((NonTerminal) head, a.singleton.list(s));
      return this;
    }
    @Override public int hashCode() {
      // TODO Roth: proper hash code
      return 0;
    }
    @Override public boolean equals(Object o) {
      return o instanceof Either && new HashSet<>(symbols).equals(new HashSet<>(((Optional) o).symbols));
    }
  }

  public static ENonTerminal either(Symbol s1, Symbol s2, Symbol... ss) {
    return new Either(merge(s1, s2, ss));
  }

  public static class Optional extends Head {
    public Optional(List<Symbol> symbols) {
      super(symbols);
    }
    @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      symbols = symbols.stream().map(x -> x instanceof Terminal && !x.isVerb() ? new Verb((Terminal) x) : x).collect(toList());
      symbols = builder.solve(lhs, symbols);
      head = builder.group(lhs, symbols);
      if (symbols.size() == 1) {
        Symbol t = head;
        head = nonTerminal(builder.namer.apply(lhs));
        builder.addRule((NonTerminal) head, a.singleton.list(t));
      }
      builder.addRule((NonTerminal) head, an.empty.list());
      return this;
    }
    @Override public int hashCode() {
      // TODO Roth: proper hash code
      return 0;
    }
    @Override public boolean equals(Object o) {
      // TODO Roth: check all symbols have "equals" so that would work
      return o instanceof Optional && new HashSet<>(symbols).equals(new HashSet<>(((Optional) o).symbols));
    }
  }

  public static ENonTerminal option(Symbol s, Symbol... ss) {
    return new Optional(merge(s, ss));
  }

  public static class OneOrMore extends Head {
    public List<Symbol> separators;
    EFajita builder;
    NonTerminal lhs;

    public OneOrMore(List<Symbol> symbols) {
      super(symbols);
    }
    @SuppressWarnings("hiding") @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      this.builder = builder;
      this.lhs = lhs;
      head = nonTerminal(builder.namer.apply(lhs));
      NonTerminal head2 = nonTerminal(builder.namer.apply(lhs));
      symbols = builder.solve(lhs, symbols);
      separators = separators == null ? an.empty.list() : builder.solve(lhs, separators);
      List<Symbol> $1 = new ArrayList<>(symbols);
      $1.add(head2);
      builder.addRule((NonTerminal) head, $1);
      List<Symbol> $2 = new ArrayList<>(separators);
      $2.addAll(symbols);
      $2.add(head2);
      builder.addRule(head2, $2);
      builder.addRule(head2, an.empty.list());
      return this;
    }
    public ENonTerminal separator(Symbol s, Symbol... ss) {
      this.separators = merge(s, ss);
      return this;
    }
    @Override public int hashCode() {
      // TODO Roth: proper hash code
      return 0;
    }
    @Override public boolean equals(Object o) {
      return o instanceof OneOrMore && new HashSet<>(symbols).equals(new HashSet<>(((OneOrMore) o).symbols))
          && new HashSet<>(separators).equals(new HashSet<>(((OneOrMore) o).separators));
    }
  }

  public static OneOrMore oneOrMore(Symbol s, Symbol... ss) {
    return new OneOrMore(merge(s, ss));
  }

  @SuppressWarnings("hiding") public static class NoneOrMore extends Head {
    public List<Symbol> separators;
    public List<Symbol> ifNone;
    EFajita builder;
    NonTerminal lhs;

    public NoneOrMore(List<Symbol> symbols) {
      super(symbols);
    }
    @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      this.builder = builder;
      this.lhs = lhs;
      head = nonTerminal(builder.namer.apply(lhs));
      symbols = builder.solve(lhs, symbols);
      separators = separators == null ? an.empty.list() : builder.solve(lhs, separators);
      ifNone = ifNone == null ? an.empty.list() : builder.solve(lhs, ifNone);
      if (separators.isEmpty() && ifNone.isEmpty()) {
        List<Symbol> $1 = new ArrayList<>(symbols);
        $1.add(head);
        builder.addRule((NonTerminal) head, $1);
        builder.addRule((NonTerminal) head, an.empty.list());
        return this;
      }
      NonTerminal head2 = nonTerminal(builder.namer.apply(lhs));
      List<Symbol> $1 = new ArrayList<>(symbols);
      $1.add(head2);
      builder.addRule((NonTerminal) head, $1);
      builder.addRule((NonTerminal) head, ifNone);
      List<Symbol> $2 = an.empty.list();
      $2.addAll(separators);
      $2.addAll(symbols);
      $2.add(head2);
      builder.addRule(head2, $2);
      builder.addRule(head2, an.empty.list());
      return this;
    }
    public IfNone separator(Symbol s, Symbol... ss) {
      NoneOrMore.this.separators = merge(s, ss);
      return new IfNone();
    }
    public Separator ifNone(Symbol s, Symbol... ss) {
      NoneOrMore.this.ifNone = merge(s, ss);
      return new Separator();
    }

    public class Separator extends Head {
      public Separator() {
        super(null);
      }
      public ENonTerminal separator(Symbol s, Symbol... ss) {
        NoneOrMore.this.separators = merge(s, ss);
        return NoneOrMore.this;
      }
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        return NoneOrMore.this.bind(builder, lhs);
      }
      public NoneOrMore parent() {
        return NoneOrMore.this;
      }
    }

    public class IfNone extends Head {
      public IfNone() {
        super(null);
      }
      public ENonTerminal ifNone(Symbol s, Symbol... ss) {
        NoneOrMore.this.ifNone = merge(s, ss);
        return NoneOrMore.this;
      }
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        return NoneOrMore.this.bind(builder, lhs);
      }
      public NoneOrMore parent() {
        return NoneOrMore.this;
      }
    }
  }

  public static NoneOrMore noneOrMore(Symbol s, Symbol... ss) {
    return new NoneOrMore(merge(s, ss));
  }
  public Symbol solve(NonTerminal lhs, Symbol s) {
    Symbol $ = s;
    if ($ instanceof EVerb)
      $ = ((EVerb) $).bind(this, lhs);
    if ($.isVerb()) {
      ParameterType t = ((Verb) $).type;
      if (t instanceof NestedType)
        nestedParameters.add((NonTerminal) solve(lhs, ((NestedType) t).nested));
    }
    if ($ instanceof ENonTerminal)
      $ = ((ENonTerminal) $).bind(this, lhs).head();
    if ($ instanceof Terminal && !$.isVerb())
      $ = new Verb((Terminal) $);
    return $;
  }
  void solve() {
    classDerivationRules.addAll(new ArrayList<>(derivationRules));
    derivationRules.clear();
    for (DerivationRule r : classDerivationRules)
      addRule(new DerivationRule(r.lhs, r.getRHS().stream().map(s -> solve(r.lhs, s)).collect(toList())));
    for (DerivationRule r : derivationRules) {
      nonterminals.add(r.lhs);
      for (Symbol s : r.getRHS())
        if (s.isNonTerminal())
          nonterminals.add((NonTerminal) s);
        else
          terminals.add((Verb) s);
    }
  }
  public List<Symbol> solve(NonTerminal lhs, List<Symbol> ss) {
    return ss.stream().map(x -> solve(lhs, x)).collect(toList());
  }
  public Symbol group(NonTerminal lhs, List<Symbol> ss) {
    List<Symbol> nss = solve(lhs, ss);
    if (nss.size() == 1)
      return nss.get(0);
    NonTerminal $ = nonTerminal(namer.apply(lhs));
    addRule($, nss);
    return $;
  }
  static List<Symbol> merge(Symbol s, Symbol... ss) {
    List<Symbol> $ = a.singleton.list(s);
    Collections.addAll($, ss);
    return $;
  }
  static List<Symbol> merge(Symbol s1, Symbol s2, Symbol... ss) {
    List<Symbol> $ = a.singleton.list(s1);
    $.add(s2);
    Collections.addAll($, ss);
    return $;
  }
  public static Verb attribute(Terminal t, Class<?>... cs) {
    return new Verb(t, cs);
  }
  public static Verb attribute(Terminal t, ParameterType pt) {
    return new Verb(t, pt);
  }
  public static Verb attribute(Terminal t, NonTerminal nt) {
    return new Verb(t, nt);
  }
  public static Verb attribute(Terminal t, ENonTerminal ent) {
    return new EVerb(t, ent);
  }
}