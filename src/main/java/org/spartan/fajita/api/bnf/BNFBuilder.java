package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

/**
 * @author Tomer
 *
 */
public class BNFBuilder {
  private final List<DerivationRule> derivationRules;
  private final Set<Terminal> terminals;
  private final Set<NonTerminal> nonterminals;
  private final Set<NonTerminal> startSymbols;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> BNFBuilder(final Class<Term> terminalEnum,
      final Class<NT> nonterminalEnum) {
    terminals = new HashSet<>(EnumSet.allOf(terminalEnum));
    nonterminals = new HashSet<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new LinkedList<>();
    startSymbols = new HashSet<>();
  }
  private boolean symbolExists(final Symbol symb) {
    return getNonTerminals().contains(symb) || getTerminals().contains(symb);
  }
  Set<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  Set<Terminal> getTerminals() {
    return terminals;
  }
  private BNFBuilder checkNewRule(final DerivationRule r) {
    if (!symbolExists(r.lhs))
      throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
    if (derivationRules.contains(r))
      throw new IllegalArgumentException("rule " + r + " already exists");
    return this;
  }
  void addRule(final NonTerminal lhs, final List<Symbol> symbols) {
    DerivationRule r = new DerivationRule(lhs, symbols, getRules().size());
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
    nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    terminals.add(SpecialSymbols.$);
    for (NonTerminal startSymbol : startSymbols)
      addRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(startSymbol));
    return new BNF(BNFBuilder.this);
  }
  List<DerivationRule> getRules() {
    return derivationRules;
  }
  public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
    NonTerminal[] newNts = Arrays.copyOf(nts, nts.length + 1);
    newNts[nts.length] = nt;
    BNFBuilder.this.startSymbols.addAll(Arrays.asList(newNts));
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
      addRuleToBNF();
      return new InitialDeriver(newRuleLHS);
    }
    @SuppressWarnings("synthetic-access") public BNF finish() {
      addRuleToBNF();
      return BNFBuilder.this.finish();
    }
    /**
     * Adds a rule to the BnfBuilder host.
     */
    protected abstract void addRuleToBNF();
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
    public NormalDeriver to(final Terminal term, Class<?>... type) {
      return new NormalDeriver(lhs, term);
    }
    public NormalDeriver to(final NonTerminal nt) {
      return new NormalDeriver(lhs, nt);
    }
    FirstDerive toNone() {
      addRule(lhs, Collections.emptyList());
      return new FirstDerive();
    }
  }

  /**
   * Currently deriving a normal rule
   * 
   * @author Tomer
   *
   */
  public final class NormalDeriver extends Deriver {
    NormalDeriver(final NonTerminal lhs, final NonTerminal child) {
      super(lhs, child);
    }
    public NormalDeriver(NonTerminal lhs,final Terminal child,Class<?> ... type) {
     
    }
    public InitialDeriver or() {
      return derive(lhs);
    }
    public FirstDerive orNone() {
      return derive(lhs).toNone();
    }
    public NormalDeriver and(final Symbol symb) {
      symbols.add(symb);
      return this;
    }
    @Override protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
  }

  public class FirstDerive {
    public InitialDeriver derive(final NonTerminal nt) {
      return new InitialDeriver(nt);
    }
  }
}
