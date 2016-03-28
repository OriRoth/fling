package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

/**
 * @author Tomer
 *
 */
public class BNFBuilder {
  private final List<DerivationRule> derivationRules;
  private final List<Terminal> terminals;
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  private final List<NonTerminal> startSymbols;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> BNFBuilder(final Class<Term> terminalEnum,
      final Class<NT> nonterminalEnum) {
    terminals = new ArrayList<>(EnumSet.allOf(terminalEnum));
    verbs = new LinkedHashSet<>();
    nonterminals = new ArrayList<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new ArrayList<>();
    startSymbols = new ArrayList<>();
  }
  private boolean symbolExists(final Symbol symb) {
    return getNonTerminals().contains(symb) //
        || terminals.stream().anyMatch(term -> term.name().equals(symb.name()));
  }
  List<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  Set<Verb> getVerbs() {
    return verbs;
  }
  public List<NonTerminal> getStartSymbols() {
    return startSymbols;
  }
  private BNFBuilder checkNewRule(final DerivationRule r) {
    if (!symbolExists(r.lhs))
      throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
    if (derivationRules.contains(r))
      throw new IllegalArgumentException("rule " + r + " already exists");
    return this;
  }
  void addRule(final NonTerminal lhs, final List<Symbol> symbols) {
    DerivationRule r = new DerivationRule(lhs, symbols);
    symbols.stream().filter(s -> s.isVerb()).forEach(v -> verbs.add((Verb)v));
    checkNewRule(r);
    getRules().add(r);
  }
  private void validate() {
    validateNonterminals();
  }
  private void validateNonterminals() {
    for (NonTerminal nonTerminal : getNonTerminals())
      if ((!getRules().stream().anyMatch(rule -> rule.lhs.equals(nonTerminal))))
        throw new IllegalStateException("nonTerminal " + nonTerminal + " has no rule");
  }
  private BNF finish() {
    validate();
//    nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    verbs.add(SpecialSymbols.$);
//    for (NonTerminal startSymbol : getStartSymbols())
//      addRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(startSymbol));
    return new BNF(BNFBuilder.this);
  }
  List<DerivationRule> getRules() {
    return derivationRules;
  }
  public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
    NonTerminal[] newNts = Arrays.copyOf(nts, nts.length + 1);
    newNts[nts.length] = nt;
    BNFBuilder.this.getStartSymbols().addAll(Arrays.asList(newNts));
    return new FirstDerive();
  }

  private abstract class Deriver {
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
    @SuppressWarnings("synthetic-access") public BNF go() {
      return BNFBuilder.this.finish();
    }
    /**
     * Adds a rule to the BnfBuilder host.
     */
    protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
  }

  /**
   * Class for the state after derive() is called from BNFBuilder
   * 
   * @author Tomer
   *
   */
  public class InitialDeriver {
    private final NonTerminal lhs;

    InitialDeriver(final NonTerminal lhs) {
      this.lhs = lhs;
    }
    public AndDeriver to(final Terminal term, Class<?>... type) {
      return new AndDeriver(lhs, new Verb(term, type));
    }
    public AndDeriver to(final NonTerminal nt) {
      return new AndDeriver(lhs, nt);
    }
    public OrDeriver toNone() {
      addRule(lhs, Collections.emptyList());
      return new OrDeriver(lhs);
    }
  }

  public class OrDeriver extends Deriver {
    OrDeriver(final NonTerminal lhs) {
      super(lhs);
    }
    public AndDeriver or(final Terminal term, Class<?>... type) {
      addRule(lhs, symbols);
      return new AndDeriver(lhs, new Verb(term, type));
    }
    public AndDeriver or(final NonTerminal nt) {
      addRule(lhs, symbols);
      return new AndDeriver(lhs, nt);
    }
    public OrDeriver orNone() {
      return derive(lhs).toNone();
    }
  }

  /**
   * Currently deriving a normal rule
   * 
   * @author Tomer
   *
   */
  public final class AndDeriver extends OrDeriver {
    AndDeriver(final NonTerminal lhs, final NonTerminal child) {
      super(lhs);
      symbols.add(child);
    }
    public AndDeriver(NonTerminal lhs, final Terminal child, Class<?>... type) {
      super(lhs);
      symbols.add(new Verb(child, type));
    }
    public AndDeriver and(final NonTerminal nt) {
      symbols.add(nt);
      return this;
    }
    public AndDeriver and(final Terminal term, Class<?>... type) {
      symbols.add(new Verb(term, type));
      return this;
    }
    @Override public BNF go() {
      addRuleToBNF();
      return super.go();
    }
  }

  public class FirstDerive {
    public InitialDeriver derive(final NonTerminal nt) {
      return new InitialDeriver(nt);
    }
  }
}
