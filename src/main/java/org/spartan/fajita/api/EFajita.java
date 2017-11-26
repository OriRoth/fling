package org.spartan.fajita.api;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.api.bnf.BNF.nonTerminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
  private static Map<NonTerminal, Integer> counter = an.empty.map();
  Function<NonTerminal, String> namer = lhs -> {
    counter.putIfAbsent(lhs, Integer.valueOf(1));
    return lhs.name() + "$" + counter.put(lhs, Integer.valueOf(counter.get(lhs).intValue() + 1));
  };
  final List<DerivationRule> classDerivationRules;

  void addClassRule(final NonTerminal lhs, final List<Symbol> symbols) {
    addClassRule(new DerivationRule(lhs, symbols));
  }
  void addClassRule(DerivationRule r) {
    classDerivationRules.add(r);
  }
  void addRawRule(final NonTerminal lhs, final List<Symbol> symbols) {
    addRawRule(new DerivationRule(lhs, symbols));
  }
  void addRawRule(final DerivationRule r) {
    super.addRule(r);
  }
  @Override void addRule(DerivationRule r) {
    super.addRule(r);
    addClassRule(r);
  }
  @Override void addRule(NonTerminal lhs, List<Symbol> symbols) {
    addRule(new DerivationRule(lhs, symbols));
  }
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
      return finish(pckg);
    }
    protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
    public BNF go() {
      return new BNF(getVerbs(), getNonTerminals(), getRules(), getStartSymbols(), getApiName());
    }
  }

  public class InitialDeriver {
    private final NonTerminal lhs;

    InitialDeriver(final NonTerminal lhs) {
      this.lhs = lhs;
    }
    public AndDeriver to(final Symbol s, final Symbol... ss) {
      AndDeriver $ = new AndDeriver(lhs, solve(lhs, s));
      for (Symbol x : ss)
        $.and(solve(lhs, x));
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
      OrDeriver $ = new InitialDeriver(lhs).to(solve(lhs, s));
      for (Symbol x : ss)
        $ = $.or(solve(lhs, x));
      return $;
    }
  }

  public class OrDeriver extends Deriver {
    OrDeriver(final NonTerminal lhs) {
      super(lhs);
    }
    public AndDeriver or(final Symbol s, Symbol... ss) {
      AndDeriver $ = new AndDeriver(lhs, solve(lhs, s));
      for (Symbol x : ss)
        $.and(solve(lhs, x));
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
      symbols.add(solve(lhs, s));
      for (Symbol x : ss)
        symbols.add(solve(lhs, x));
      return this;
    }
    @Override public Map<String, String> go(String pckg) {
      addRuleToBNF();
      return super.go(pckg);
    }
    @Override public BNF go() {
      addRuleToBNF();
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

    @Override public Symbol head() {
      return head;
    }
    @SuppressWarnings("unused") @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      return null;
    }
  }

  public static ENonTerminal either(Symbol s1, Symbol s2, Symbol... ss) {
    return new Head() {
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        List<Symbol> $ = builder.solve(lhs, merge(s1, s2, ss));
        head = nonTerminal(builder.namer.apply(lhs));
        for (Symbol s : $)
          builder.addRule((NonTerminal) head, a.singleton.list(s));
        return this;
      }
    };
  }
  public static ENonTerminal concat(Symbol s1, Symbol s2, Symbol... ss) {
    return new Head() {
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        head = builder.group(lhs, merge(s1, s2, ss));
        return this;
      }
    };
  }

  public static class Optional extends Head {
    public List<Symbol> symbols;
  }

  public static ENonTerminal optional(Symbol s, Symbol... ss) {
    return new Optional() {
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        symbols = merge(s, ss).stream().map(x -> x instanceof Terminal && !x.isVerb() ? new Verb((Terminal) x) : x)
            .collect(toList());
        head = builder.groupRaw(lhs, symbols);
        if (symbols.size() == 1) {
          Symbol t = head;
          head = nonTerminal(builder.namer.apply(lhs));
          builder.addRawRule((NonTerminal) head, a.singleton.list(t));
        }
        builder.addRawRule((NonTerminal) head, an.empty.list());
        builder.addClassRule((NonTerminal) head, a.singleton.list(this));
        return this;
      }
    };
  }

  public static class OneOrMore extends Head {
    List<Symbol> symbs;
    List<Symbol> separators;
    EFajita builder;
    NonTerminal lhs;

    OneOrMore(Symbol s, Symbol... ss) {
      symbs = merge(s, ss);
    }
    @SuppressWarnings("hiding") @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      this.builder = builder;
      this.lhs = lhs;
      head = nonTerminal(builder.namer.apply(lhs));
      symbs = builder.solve(lhs, symbs);
      separators = separators == null ? an.empty.list() : builder.solve(lhs, separators);
      List<Symbol> $1 = new ArrayList<>(symbs);
      $1.addAll(separators);
      $1.add(head);
      builder.addRule((NonTerminal) head, $1);
      List<Symbol> $2 = new ArrayList<>(symbs);
      builder.addRule((NonTerminal) head, $2);
      return this;
    }
    public ENonTerminal separator(Symbol s, Symbol... ss) {
      this.separators = merge(s, ss);
      return this;
    }
  }

  public static OneOrMore oneOrMore(Symbol s, Symbol... ss) {
    return new OneOrMore(s, ss);
  }

  @SuppressWarnings("hiding") public static class NoneOrMore extends Head {
    List<Symbol> symbs;
    List<Symbol> separators;
    List<Symbol> ifNone;
    EFajita builder;
    NonTerminal lhs;

    NoneOrMore(Symbol s, Symbol... ss) {
      symbs = merge(s, ss);
    }
    @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
      this.builder = builder;
      this.lhs = lhs;
      head = nonTerminal(builder.namer.apply(lhs));
      NonTerminal head2 = nonTerminal(builder.namer.apply(lhs));
      symbs = builder.solve(lhs, symbs);
      separators = separators == null ? an.empty.list() : builder.solve(lhs, separators);
      ifNone = ifNone == null ? an.empty.list() : builder.solve(lhs, ifNone);
      builder.addRule((NonTerminal) head, ifNone);
      builder.addRule((NonTerminal) head, a.singleton.list(head2));
      List<Symbol> $1 = new ArrayList<>(symbs);
      if (separators != null)
        $1.addAll(separators);
      $1.add(head2);
      builder.addRule(head2, $1);
      List<Symbol> $2 = new ArrayList<>(symbs);
      builder.addRule(head2, $2);
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
      public ENonTerminal separator(Symbol s, Symbol... ss) {
        NoneOrMore.this.separators = merge(s, ss);
        return NoneOrMore.this;
      }
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        return NoneOrMore.this.bind(builder, lhs);
      }
    }

    public class IfNone extends Head {
      public ENonTerminal ifNone(Symbol s, Symbol... ss) {
        NoneOrMore.this.ifNone = merge(s, ss);
        return NoneOrMore.this;
      }
      @Override public ENonTerminal bind(EFajita builder, NonTerminal lhs) {
        return NoneOrMore.this.bind(builder, lhs);
      }
    }
  }

  public static NoneOrMore noneOrMore(Symbol s, Symbol... ss) {
    return new NoneOrMore(s, ss);
  }
  List<Symbol> solve(NonTerminal lhs, List<Symbol> ss) {
    List<Symbol> $ = ss.stream().map(x -> x instanceof EVerb ? ((EVerb) x).bind(this, lhs) : x).collect(toList());
    $.stream().filter(x -> x.isVerb()) //
        .map(x -> ((Verb) x).type).filter(x -> x instanceof NestedType).map(x -> (NestedType) x)
        .forEach(x -> nestedParameters.add(solve(lhs, x.nested)));
    return $.stream().map(x -> x instanceof ENonTerminal ? ((ENonTerminal) x).bind(this, lhs).head() : x)
        .map(x -> x instanceof Terminal && !(x instanceof Verb) ? new Verb((Terminal) x) : x).collect(toList());
  }
  NonTerminal solve(NonTerminal lhs, NonTerminal s) {
    return (NonTerminal) solve(lhs, a.singleton.list(s)).get(0);
  }
  public Symbol solve(NonTerminal lhs, Symbol s) {
    return solve(lhs, a.singleton.list(s)).get(0);
  }
  public Symbol group(NonTerminal lhs, List<Symbol> ss) {
    List<Symbol> nss = solve(lhs, ss);
    if (nss.size() == 1)
      return nss.get(0);
    NonTerminal $ = nonTerminal(namer.apply(lhs));
    addRule($, nss);
    return $;
  }
  public Symbol groupRaw(NonTerminal lhs, List<Symbol> ss) {
    List<Symbol> nss = solve(lhs, ss);
    if (nss.size() == 1)
      return nss.get(0);
    NonTerminal $ = nonTerminal(namer.apply(lhs));
    addRawRule($, nss);
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