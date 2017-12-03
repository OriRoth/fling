package org.spartan.fajita.revision.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.api.encoding.FajitaEncoder;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.Extendible;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;

public class Fajita {
  public final Set<DerivationRule> derivationRules;
  public final Set<Verb> verbs;
  public final Set<NonTerminal> nonTerminals;
  public final Set<Extendible> extendibles;
  public final Set<NonTerminal> startSymbols;
  public final String apiName;
  public final Set<Terminal> terminals;
  public final Set<Symbol> nestedParameters; // NonTerminal and Extendibles
  public final String packagePath;
  public final String projectPath;
  public Class<? extends Grammar> provider;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> Fajita(Class<? extends Grammar> provider,
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum, String apiName, String packagePath, String projectPath) {
    terminals = new LinkedHashSet<>(EnumSet.allOf(terminalEnum));
    verbs = new LinkedHashSet<>();
    nonTerminals = new LinkedHashSet<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new LinkedHashSet<>();
    startSymbols = new LinkedHashSet<>();
    nestedParameters = new HashSet<>();
    extendibles = new HashSet<>();
    this.provider = provider;
    this.apiName = apiName;
    this.packagePath = packagePath;
    this.projectPath = projectPath;
  }
  public BNF bnf() {
    Map<NonTerminal, Integer> counter = an.empty.map();
    Function<NonTerminal, String> namer = lhs -> {
      counter.putIfAbsent(lhs, Integer.valueOf(1));
      return lhs.name() + counter.put(lhs, Integer.valueOf(counter.get(lhs).intValue() + 1));
    };
    return ebnf().toBNF(x -> NonTerminal.of(namer.apply(x)));
  }
  public EBNF ebnf() {
    return new EBNF(verbs, nonTerminals, extendibles, derivationRules, startSymbols, apiName);
  }
  Map<String, String> finish() {
    return FajitaEncoder.encode(this);
  }
  public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> SetSymbols build(
      Class<? extends Grammar> provider, final Class<Term> terminalEnum, final Class<NT> nonterminalEnum, String apiName,
      String packagePath, String projectPath) {
    Fajita builder = new Fajita(provider, terminalEnum, nonterminalEnum, apiName, packagePath, projectPath);
    return builder.new SetSymbols();
  }
  public void addRule(NonTerminal lhs, List<Symbol> rhs) {
    rhs.stream().filter(s -> s.isVerb()).forEach(v -> verbs.add(v.asVerb()));
    rhs.stream().filter(s -> s.isExtendible()).forEach(v -> extendibles.add(v.asExtendible()));
    rhs.stream().filter(s -> s.isVerb()).forEach(s -> Arrays.stream(s.asVerb().type).filter(t -> t instanceof NestedType)
        .forEach(t -> nestedParameters.add(((NestedType) t).nested)));
    derivationRules.add(new DerivationRule(lhs, rhs));
  }

  public class SetSymbols {
    public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
      Fajita.this.startSymbols.add(nt);
      Collections.addAll(Fajita.this.startSymbols, nts);
      return new FirstDerive();
    }
  }

  public abstract class FajitaBNF {
    protected final NonTerminal lhs;
    protected final ArrayList<Symbol> rhs;

    public FajitaBNF(final NonTerminal lhs, final Symbol... rhs) {
      this.lhs = lhs;
      this.rhs = new ArrayList<>(Arrays.asList(rhs));
    }
    public InitialDeriver derive(final NonTerminal newRuleLHS) {
      if (!rhs.isEmpty())
        addRuleToBNF();
      return new InitialDeriver(newRuleLHS);
    }
    public InitialSpecializeDeriver specialize(final NonTerminal newRuleLHS) {
      if (!rhs.isEmpty())
        addRuleToBNF();
      return new InitialSpecializeDeriver(newRuleLHS);
    }
    public Map<String, String> go() {
      return finish();
    }
    protected void addRuleToBNF() {
      addRule(lhs, rhs);
    }
    public BNF bnf() {
      return Fajita.this.bnf();
    }
    public EBNF ebnf() {
      return Fajita.this.ebnf();
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
    public FajitaBNF into(final NonTerminal s, final NonTerminal... ss) {
      OrDeriver $ = new InitialDeriver(lhs).to(s);
      for (Symbol x : ss)
        $ = $.or(x);
      return $;
    }
  }

  public class OrDeriver extends FajitaBNF {
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
      addRule(lhs, rhs);
      return deriver;
    }
    public OrDeriver orNone() {
      return derive(lhs).toNone();
    }
  }

  public final class AndDeriver extends OrDeriver {
    AndDeriver(final NonTerminal lhs, final Symbol child) {
      super(lhs);
      rhs.add(child);
    }
    public AndDeriver and(final Symbol s, Symbol... ss) {
      rhs.add(s);
      for (Symbol x : ss)
        rhs.add(x);
      return this;
    }
    @Override public Map<String, String> go() {
      addRuleToBNF();
      return super.go();
    }
    @Override public BNF bnf() {
      addRuleToBNF();
      return super.bnf();
    }
    @Override public EBNF ebnf() {
      addRuleToBNF();
      return super.ebnf();
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

  // public static abstract class Head implements Extendible {
  // NonTerminal head;
  // public List<Symbol> symbols;
  // protected List<DerivationRule> rules;
  //
  // public Head(List<Symbol> symbols) {
  // this.symbols = symbols;
  // }
  // @Override public NonTerminal head() {
  // return head;
  // }
  // }
  //
  // public static class Either extends Head {
  // public Either(List<Symbol> symbols) {
  // super(symbols);
  // }
  // @Override public int hashCode() {
  // // TODO Roth: proper hash code
  // return 0;
  // }
  // @Override public boolean equals(Object o) {
  // return o instanceof Either && new HashSet<>(symbols).equals(new
  // HashSet<>(((Optional) o).symbols));
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // return symbols.stream().map(x -> x instanceof Head ? ((Head)
  // x).first(getFirst) : getFirst.apply(x)).reduce(new HashSet<>(),
  // (l1, l2) -> {
  // l1.addAll(l2);
  // return l1;
  // });
  // }
  // @Override public List<DerivationRule> solve(NonTerminal lhs,
  // Function<NonTerminal, NonTerminal> producer) {
  // head = producer.apply(lhs);
  // rules = new LinkedList<>();
  // for (Symbol s : symbols) {
  // if (s.isExtendible()) {
  // rules.addAll(s.asExtendible().solve(lhs, producer));
  // rules.add(new DerivationRule(head,
  // a.singleton.list(s.asExtendible().head())));
  // } else
  // rules.add(new DerivationRule(head, a.singleton.list(s)));
  // }
  // return rules;
  // }
  // @Override public boolean isNullable() {
  // // TODO Auto-generated method stub
  // return false;
  // }
  // @Override public List<Terminal> firstSet() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  // @Override public String name() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  // }
  //
  // public static ENonTerminal either(Symbol s1, Symbol s2, Symbol... ss) {
  // return new Either(merge(s1, s2, ss));
  // }
  //
  // public static class Optional extends Head {
  // public Optional(List<Symbol> symbols) {
  // super(symbols);
  // }
  // public ENonTerminal bind(Fajita builder, NonTerminal lhs) {
  // symbols = symbols.stream().map(x -> x instanceof Terminal && !x.isVerb() ?
  // new Verb((Terminal) x) : x).collect(toList());
  // symbols = builder.solve(lhs, symbols);
  // head = builder.group(lhs, symbols);
  // if (symbols.size() == 1) {
  // Symbol t = head;
  // head = nonTerminal(builder.namer.apply(lhs));
  // builder.addRule((NonTerminal) head, a.singleton.list(t));
  // }
  // builder.addRule((NonTerminal) head, an.empty.list());
  // return this;
  // }
  // @Override public int hashCode() {
  // // TODO Roth: proper hash code
  // return 0;
  // }
  // @Override public boolean equals(Object o) {
  // // TODO Roth: check all symbols have "equals" so that would work
  // return o instanceof Optional && new HashSet<>(symbols).equals(new
  // HashSet<>(((Optional) o).symbols));
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // return symbols.get(0) instanceof Head ? ((Head)
  // symbols.get(0)).first(getFirst) : getFirst.apply(symbols.get(0));
  // }
  // }
  //
  // public static ENonTerminal option(Symbol s, Symbol... ss) {
  // return new Optional(merge(s, ss));
  // }
  //
  // public static class OneOrMore extends Head {
  // public List<Symbol> separators;
  // Fajita builder;
  // NonTerminal lhs;
  //
  // public OneOrMore(List<Symbol> symbols) {
  // super(symbols);
  // }
  // @SuppressWarnings("hiding") public ENonTerminal bind(Fajita builder,
  // NonTerminal lhs) {
  // this.builder = builder;
  // this.lhs = lhs;
  // head = nonTerminal(builder.namer.apply(lhs));
  // NonTerminal head2 = nonTerminal(builder.namer.apply(lhs));
  // symbols = builder.solve(lhs, symbols);
  // separators = separators == null ? an.empty.list() : builder.solve(lhs,
  // separators);
  // List<Symbol> $1 = new ArrayList<>(symbols);
  // $1.add(head2);
  // builder.addRule((NonTerminal) head, $1);
  // List<Symbol> $2 = new ArrayList<>(separators);
  // $2.addAll(symbols);
  // $2.add(head2);
  // builder.addRule(head2, $2);
  // builder.addRule(head2, an.empty.list());
  // return this;
  // }
  // public ENonTerminal separator(Symbol s, Symbol... ss) {
  // this.separators = merge(s, ss);
  // return this;
  // }
  // @Override public int hashCode() {
  // // TODO Roth: proper hash code
  // return 0;
  // }
  // @Override public boolean equals(Object o) {
  // return o instanceof OneOrMore && new HashSet<>(symbols).equals(new
  // HashSet<>(((OneOrMore) o).symbols))
  // && new HashSet<>(separators).equals(new HashSet<>(((OneOrMore)
  // o).separators));
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // return symbols.get(0) instanceof Head ? ((Head)
  // symbols.get(0)).first(getFirst) : getFirst.apply(symbols.get(0));
  // }
  // }
  //
  // public static OneOrMore oneOrMore(Symbol s, Symbol... ss) {
  // return new OneOrMore(merge(s, ss));
  // }
  //
  // @SuppressWarnings("hiding") public static class NoneOrMore extends Head {
  // public List<Symbol> separators;
  // public List<Symbol> ifNone;
  // Fajita builder;
  // NonTerminal lhs;
  //
  // public NoneOrMore(List<Symbol> symbols) {
  // super(symbols);
  // }
  // public ENonTerminal bind(Fajita builder, NonTerminal lhs) {
  // this.builder = builder;
  // this.lhs = lhs;
  // head = nonTerminal(builder.namer.apply(lhs));
  // symbols = builder.solve(lhs, symbols);
  // separators = separators == null ? an.empty.list() : builder.solve(lhs,
  // separators);
  // ifNone = ifNone == null ? an.empty.list() : builder.solve(lhs, ifNone);
  // if (separators.isEmpty() && ifNone.isEmpty()) {
  // List<Symbol> $1 = new ArrayList<>(symbols);
  // $1.add(head);
  // builder.addRule((NonTerminal) head, $1);
  // builder.addRule((NonTerminal) head, an.empty.list());
  // return this;
  // }
  // NonTerminal head2 = nonTerminal(builder.namer.apply(lhs));
  // List<Symbol> $1 = new ArrayList<>(symbols);
  // $1.add(head2);
  // builder.addRule((NonTerminal) head, $1);
  // builder.addRule((NonTerminal) head, ifNone);
  // List<Symbol> $2 = an.empty.list();
  // $2.addAll(separators);
  // $2.addAll(symbols);
  // $2.add(head2);
  // builder.addRule(head2, $2);
  // builder.addRule(head2, an.empty.list());
  // return this;
  // }
  // public IfNone separator(Symbol s, Symbol... ss) {
  // NoneOrMore.this.separators = merge(s, ss);
  // return new IfNone();
  // }
  // public Separator ifNone(Symbol s, Symbol... ss) {
  // NoneOrMore.this.ifNone = merge(s, ss);
  // return new Separator();
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // Set<Verb> $ = symbols.get(0) instanceof Head ? ((Head)
  // symbols.get(0)).first(getFirst) : getFirst.apply(symbols.get(0));
  // if (!ifNone.isEmpty())
  // $.addAll(ifNone.get(0) instanceof Head ? ((Head)
  // ifNone.get(0)).first(getFirst) : getFirst.apply(ifNone.get(0)));
  // return $;
  // }
  //
  // public class Separator extends Head {
  // public Separator() {
  // super(null);
  // }
  // public ENonTerminal separator(Symbol s, Symbol... ss) {
  // NoneOrMore.this.separators = merge(s, ss);
  // return NoneOrMore.this;
  // }
  // public ENonTerminal bind(Fajita builder, NonTerminal lhs) {
  // return NoneOrMore.this.bind(builder, lhs);
  // }
  // public NoneOrMore parent() {
  // return NoneOrMore.this;
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // return NoneOrMore.this.first(getFirst);
  // }
  // }
  //
  // public class IfNone extends Head {
  // public IfNone() {
  // super(null);
  // }
  // public ENonTerminal ifNone(Symbol s, Symbol... ss) {
  // NoneOrMore.this.ifNone = merge(s, ss);
  // return NoneOrMore.this;
  // }
  // public ENonTerminal bind(Fajita builder, NonTerminal lhs) {
  // return NoneOrMore.this.bind(builder, lhs);
  // }
  // public NoneOrMore parent() {
  // return NoneOrMore.this;
  // }
  // public Set<Verb> first(Function<Symbol, Set<Verb>> getFirst) {
  // return NoneOrMore.this.first(getFirst);
  // }
  // }
  // }
  //
  // public static NoneOrMore noneOrMore(Symbol s, Symbol... ss) {
  // return new NoneOrMore(merge(s, ss));
  // }
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
  public static Verb attribute(Terminal terminal, Object... parameterTypes) {
    return new Verb(terminal, parameterTypes);
  }
}
