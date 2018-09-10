package roth.ori.fling.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import roth.ori.fling.api.encoding.FlingEncoder;
import roth.ori.fling.bnf.BNF;
import roth.ori.fling.bnf.DerivationRule;
import roth.ori.fling.bnf.EBNF;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.extendibles.Either;
import roth.ori.fling.symbols.extendibles.Extendible;
import roth.ori.fling.symbols.extendibles.NoneOrMore;
import roth.ori.fling.symbols.extendibles.OneOrMore;
import roth.ori.fling.symbols.extendibles.Option;
import roth.ori.fling.symbols.types.NestedType;

public class Fling {
  public final Set<DerivationRule> derivationRules;
  public final Set<Verb> verbs;
  public final Set<NonTerminal> nonTerminals;
  public final Set<Extendible> extendibles;
  public final Set<NonTerminal> startSymbols;
  public final String apiName;
  public final Set<Terminal> terminals;
  public final Set<Symbol> nestedParameters; // NonTerminal and Extendibles (?)
  public final String packagePath;
  public final String projectPath;
  public Class<? extends Grammar> provider;
  private EBNF ebnf;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> Fling(Class<? extends Grammar> provider,
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum, String apiName, String packagePath, String projectPath) {
    terminals = new LinkedHashSet<>(EnumSet.allOf(terminalEnum));
    verbs = new LinkedHashSet<>();
    nonTerminals = new LinkedHashSet<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new LinkedHashSet<>();
    startSymbols = new LinkedHashSet<>();
    nestedParameters = new LinkedHashSet<>();
    extendibles = new LinkedHashSet<>();
    this.provider = provider;
    this.apiName = apiName;
    this.packagePath = packagePath;
    this.projectPath = projectPath;
  }

  public static class FlingProducer implements Function<NonTerminal, NonTerminal>, Cloneable {
    // TODO Roth: make private
    public Map<NonTerminal, Integer> counter = new LinkedHashMap<>();

    @Override public NonTerminal apply(NonTerminal lhs) {
      counter.putIfAbsent(lhs, Integer.valueOf(1));
      return NonTerminal.of(lhs.name() + counter.put(lhs, Integer.valueOf(counter.get(lhs).intValue() + 1)));
    }
    @Override public FlingProducer clone() {
      FlingProducer $ = new FlingProducer();
      $.counter.putAll(counter);
      return $;
    }
  }

  private static FlingProducer producer() {
    return new FlingProducer();
  }
  public BNF bnf() {
    return ebnf().toBNF(producer());
  }
  public EBNF ebnf() {
    if (ebnf != null)
      return ebnf;
    ebnf = new EBNF(verbs, nonTerminals, extendibles, derivationRules, startSymbols, apiName);
    ebnf.toBNF(new FlingProducer());
    return ebnf;
  }
  Map<String, String> finish() {
    return FlingEncoder.encode(this);
  }
  public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> SetSymbols build(
      Class<? extends Grammar> provider, final Class<Term> terminalEnum, final Class<NT> nonterminalEnum, String apiName,
      String packagePath, String projectPath) {
    Fling builder = new Fling(provider, terminalEnum, nonterminalEnum, apiName, packagePath, projectPath);
    return builder.new SetSymbols();
  }
  public void addRule(NonTerminal lhs, List<Symbol> rhs) {
    List<Symbol> $ = fixRawTerminals(rhs);
    analyze($);
    derivationRules.add(new DerivationRule(lhs, $));
  }
  private void analyze(Symbol s) {
    if (s.isVerb()) {
      verbs.add(s.asVerb());
      Arrays.stream(s.asVerb().type).filter(t -> t instanceof NestedType).map(t -> ((NestedType) t).nested).forEach(nested -> {
        nestedParameters.add(nested);
        analyze(nested);
      });
    } else if (s.isExtendible()) {
      extendibles.add(s.asExtendible());
      s.asExtendible().symbols().forEach(this::analyze);
    }
  }
  private void analyze(List<Symbol> rhs) {
    rhs.forEach(s -> analyze(s));
  }

  public class SetSymbols {
    public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
      Fling.this.startSymbols.add(nt);
      Collections.addAll(Fling.this.startSymbols, nts);
      return new FirstDerive();
    }
  }

  public abstract class FlingBNF {
    protected final NonTerminal lhs;
    protected final ArrayList<Symbol> rhs;

    public FlingBNF(final NonTerminal lhs, final Symbol... rhs) {
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
      return Fling.this.bnf();
    }
    public EBNF ebnf() {
      return Fling.this.ebnf();
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
    public FlingBNF into(final NonTerminal s, final NonTerminal... ss) {
      OrDeriver $ = new InitialDeriver(lhs).to(s);
      for (Symbol x : ss)
        $ = $.or(x);
      return $;
    }
  }

  public class OrDeriver extends FlingBNF {
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

  static List<Symbol> merge(Symbol s, Symbol... ss) {
    List<Symbol> $ = new LinkedList<>();
    $.add(s);
    Collections.addAll($, ss);
    return $;
  }
  static List<Symbol> merge(Symbol s1, Symbol s2, Symbol... ss) {
    List<Symbol> $ = new LinkedList<>();
    $.add(s1);
    $.add(s2);
    Collections.addAll($, ss);
    return $;
  }
  public static Verb attribute(Terminal terminal, Object... parameterTypes) {
    return new Verb(terminal, parameterTypes);
  }
  public static OneOrMore oneOrMore(Symbol s, Symbol... ss) {
    return new OneOrMore(merge(s, ss));
  }
  public static NoneOrMore noneOrMore(Symbol s, Symbol... ss) {
    return new NoneOrMore(merge(s, ss));
  }
  public static Either either(Symbol s1, Symbol s2, Symbol... ss) {
    return new Either(merge(s1, s2, ss));
  }
  public static Option option(Symbol s, Symbol... ss) {
    return new Option(merge(s, ss));
  }
  private List<Symbol> fixRawTerminals(List<Symbol> rhs) {
    List<Symbol> $ = new ArrayList<>();
    for (Symbol s : rhs)
      if (s.isVerb() || s.isNonTerminal())
        $.add(s);
      else if (s.isTerminal())
        $.add(new Verb(s.asTerminal()));
      else {
        s.asExtendible().fixSymbols(this::fixRawTerminals);
        $.add(s);
      }
    return $;
  }
}
