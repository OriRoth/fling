package org.spartan.fajita.api.bnf;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;
import static org.spartan.fajita.api.bnf.BNFTransformer.Example.Term.*;
import static org.spartan.fajita.api.bnf.BNFTransformer.Example.NT.*;

import static java.util.stream.Collectors.toList;

public class BNFTransformer<SymbolAug, TerminalAug extends SymbolAug, NonTerminalAug extends SymbolAug, LHSAug, RHSAug, RuleAug, BNFAug> {
  protected Function<Terminal, TerminalAug> terminalAug;
  protected Function<NonTerminal, NonTerminalAug> nonTerminalAug;
  protected Function<NonTerminal, LHSAug> lhsAug;
  protected Function<List<SymbolAug>, RHSAug> rhsAug;
  protected Supplier<RHSAug> epsilonAug;
  protected BiFunction<LHSAug, RHSAug, RuleAug> ruleAug;
  protected Function<List<RuleAug>, BNFAug> bnfAug;

  protected BNFTransformer(Function<Terminal, TerminalAug> terminalAug, Function<NonTerminal, NonTerminalAug> nonTerminalAug,
      Function<NonTerminal, LHSAug> lhsAug, Function<List<SymbolAug>, RHSAug> rhsAug, Supplier<RHSAug> epsilonAug,
      BiFunction<LHSAug, RHSAug, RuleAug> ruleAug, Function<List<RuleAug>, BNFAug> bnfAug) {
    this.terminalAug = terminalAug;
    this.nonTerminalAug = nonTerminalAug;
    this.lhsAug = lhsAug;
    this.rhsAug = rhsAug;
    this.epsilonAug = epsilonAug;
    this.ruleAug = ruleAug;
    this.bnfAug = bnfAug;
  }
  public static <SymbolAug, TerminalAug extends SymbolAug, NonTerminalAug extends SymbolAug, LHSAug, RHSAug, RuleAug, BNFAug> BNFTransformer<SymbolAug, TerminalAug, NonTerminalAug, LHSAug, RHSAug, RuleAug, BNFAug> transformer(
      Function<Terminal, TerminalAug> terminalAug, Function<NonTerminal, NonTerminalAug> nonTerminalAug,
      Function<NonTerminal, LHSAug> lhsAug, Function<List<SymbolAug>, RHSAug> rhsAug, Supplier<RHSAug> epsilonAug,
      BiFunction<LHSAug, RHSAug, RuleAug> ruleAug, Function<List<RuleAug>, BNFAug> bnfAug) {
    return new BNFTransformer<>(terminalAug, nonTerminalAug, lhsAug, rhsAug, epsilonAug, ruleAug, bnfAug);
  }
  public BNFAug transform(BNF bnf) {
    List<RuleAug> nrs = new LinkedList<>();
    for (DerivationRule r : bnf.getRules()) {
      LHSAug nlhs = lhsAug.apply(r.lhs);
      List<Symbol> rhs = r.getRHS();
      RHSAug nrhs;
      if (rhs.isEmpty())
        nrhs = epsilonAug.get();
      else {
        List<SymbolAug> ns = new LinkedList<>();
        for (Symbol s : rhs)
          ns.add(s.isNonTerminal() ? nonTerminalAug.apply((NonTerminal) s) : terminalAug.apply((Terminal) s));
        nrhs = rhsAug.apply(ns);
      }
      nrs.add(ruleAug.apply(nlhs, nrhs));
    }
    return bnfAug.apply(nrs);
  }

  public static class WithRuleConsolidation<SymbolAug, TerminalAug extends SymbolAug, NonTerminalAug extends SymbolAug, LHSAug, RHSAug, RuleAug, BNFAug>
      extends BNFTransformer<Symbol, Terminal, NonTerminal, NonTerminal, List<Symbol>, DerivationRule, BNFAug> {
    protected WithRuleConsolidation(Function<Terminal, TerminalAug> terminalAug,
        Function<NonTerminal, NonTerminalAug> nonTerminalAug, Function<NonTerminal, LHSAug> lhsAug,
        Function<List<SymbolAug>, RHSAug> rhsAug, Supplier<RHSAug> epsilonAug, BiFunction<LHSAug, RHSAug, RuleAug> ruleAug,
        BinaryOperator<RuleAug> ruleConsolidation, Function<List<RuleAug>, BNFAug> bnfAug) {
      super(x -> x, x -> x, x -> x, x -> x, () -> new LinkedList<>(), (lhs, rhs) -> new DerivationRule(lhs, rhs), rs -> {
        Map<NonTerminal, List<List<Symbol>>> m1 = new HashMap<>();
        for (DerivationRule r : rs) {
          m1.putIfAbsent(r.lhs, new LinkedList<>());
          m1.get(r.lhs).add(r.getRHS());
        }
        List<RuleAug> ras = new LinkedList<>();
        for (Entry<NonTerminal, List<List<Symbol>>> e : m1.entrySet())
          e.getValue().stream()
              .map(x -> x.stream()
                  .map(y -> y.isNonTerminal() ? nonTerminalAug.apply((NonTerminal) y) : terminalAug.apply((Terminal) y))
                  .collect(toList()))
              .map(x -> x.isEmpty() ? epsilonAug.get() : rhsAug.apply(x)).map(x -> ruleAug.apply(lhsAug.apply(e.getKey()), x))
              .reduce(ruleConsolidation).ifPresent(x -> ras.add(x));
        return bnfAug.apply(ras);
      });
    }
    public static <SymbolAug, TerminalAug extends SymbolAug, NonTerminalAug extends SymbolAug, LHSAug, RHSAug, RuleAug, BNFAug> WithRuleConsolidation<SymbolAug, TerminalAug, NonTerminalAug, LHSAug, RHSAug, RuleAug, BNFAug> transformer(
        Function<Terminal, TerminalAug> terminalAug, Function<NonTerminal, NonTerminalAug> nonTerminalAug,
        Function<NonTerminal, LHSAug> lhsAug, Function<List<SymbolAug>, RHSAug> rhsAug, Supplier<RHSAug> epsilonAug,
        BiFunction<LHSAug, RHSAug, RuleAug> ruleAug, BinaryOperator<RuleAug> ruleConsolidation,
        Function<List<RuleAug>, BNFAug> bnfAug) {
      return new WithRuleConsolidation<>(terminalAug, nonTerminalAug, lhsAug, rhsAug, epsilonAug, ruleAug, ruleConsolidation,
          bnfAug);
    }
  }

  /**
   * BNF transformation example: BNF to specific rules mapping.
   * 
   * @author Ori Roth
   */
  public static class Example {
    static <T> List<T> singleton(T t) {
      List<T> $ = new LinkedList<>();
      $.add(t);
      return $;
    }

    static class BNFMap extends HashMap<NT, List<List<Symbol>>> {
      private static final long serialVersionUID = 6801030164365571151L;

      @Override public String toString() {
        StringBuilder $ = new StringBuilder();
        for (NT nt : keySet()) {
          $.append(nt).append(" -> ");
          List<List<Symbol>> rhs = get(nt);
          $.append(rhs.stream().map(ss -> String.join(" ", ss.stream().map(s -> s.name()).collect(toList())))
              .map(x -> !"".equals(x) ? x : "ε").reduce((s1, s2) -> (s1 + " | " + s2)).orElse("ε"));
          $.append("\n");
        }
        return $.toString();
      }
    }

    static enum Term implements Terminal {
      head, body, fact, literal, name, terms
    }

    static enum NT implements NonTerminal {
      RULES, RULE, LITERAL, BODY, LITERALS, LITERALS_OR_NONE, augS
    }

    public static BNF bnf() {
      return Fajita.buildBNF(Term.class, NT.class) //
          .setApiName("Example") //
          .start(RULES) //
          .derive(RULES).to(RULE).and(RULES).orNone() //
          .derive(RULE) //
          /**/.to(fact, LITERAL) //
          /**/.or(head, LITERAL).and(BODY) //
          .derive(BODY).to(body, LITERALS) //
          .derive(LITERALS).to(literal, LITERAL).and(LITERALS_OR_NONE) //
          .derive(LITERALS_OR_NONE).to(literal, LITERAL).and(LITERALS_OR_NONE).orNone() //
          .derive(LITERAL).to(name, String.class).and(terms, new VarArgs(String.class)) //
          .go();
    }
    public static WithRuleConsolidation<Symbol, Term, NT, NT, List<List<Symbol>>, Entry<NT, List<List<Symbol>>>, Map<NT, List<List<Symbol>>>> transformer() {
      return new BNFTransformer.WithRuleConsolidation<>(x -> Term.valueOf(x.name()), x -> NT.valueOf(x.name()),
          x -> NT.valueOf(x.name()), x -> singleton(x), () -> singleton(new LinkedList<>()),
          (lhs, rhs) -> new AbstractMap.SimpleEntry<>(lhs, rhs), (r1, r2) -> {
            r1.getValue().addAll(r2.getValue());
            return r1;
          }, es -> {
            Map<NT, List<List<Symbol>>> $ = new BNFMap();
            es.stream().forEach(e -> $.put(e.getKey(), e.getValue()));
            return $;
          });
    }
    public static void main(String[] args) {
      Map<NT, List<List<Symbol>>> $ = transformer().transform(bnf());
      System.out.println($);
    }
  }
}
