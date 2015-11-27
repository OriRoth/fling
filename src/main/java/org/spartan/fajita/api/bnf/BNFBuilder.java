package org.spartan.fajita.api.bnf;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.spartan.fajita.api.bnf.symbols.Type;

/**
 * @author Tomer
 *
 */
public class BNFBuilder {
  private final List<DerivationRule> derivationRules;
  private final Set<Terminal> terminals;
  private final Set<NonTerminal> nonterminals;
  private final Set<NonTerminal> startSymbols;
  private final Set<Terminal> overloads;
  private String apiName;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> BNFBuilder(final Class<Term> terminalEnum,
      final Class<NT> nonterminalEnum) {
    terminals = new HashSet<>(EnumSet.allOf(terminalEnum));
    nonterminals = new HashSet<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new LinkedList<>();
    startSymbols = new HashSet<>();
    overloads = new HashSet<>();
  }
  public InitialConfigurator startConfig() {
    return new InitialConfigurator();
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
  private void addRule(final NonTerminal lhs, final List<Symbol> symbols) {
    DerivationRule r = new DerivationRule(lhs, symbols, getRules().size());
    checkNewRule(r);
    getRules().add(r);
  }
  private void addOverload(final String name, final Type type) {
    overloads.add(new Terminal() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return methodSignatureString();
      }
      @Override public Type type() {
        return type;
      }
    });
  }
  private void validate() {
    validateNonterminals();
    validateOverloads();
  }
  private void validateOverloads() {
    overloads.forEach(t -> {
      if (overloads.stream().anyMatch(t2 -> t2.name().equals(t.name()) && t2.type().equals(t.type()) && t != t2)
          || terminals.stream().anyMatch(t2 -> t2.name().equals(t.name()) && t2.type().equals(t.type())))
        throw new IllegalStateException("overload: " + t.methodSignatureString() + " already exists.");
    });
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
    terminals.addAll(overloads);
    for (NonTerminal startSymbol : startSymbols)
      addRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(startSymbol));
    return new BNF(BNFBuilder.this);
  }
  String getApiName() {
    return apiName;
  }
  private void setApiName(final String apiName) {
    this.apiName = apiName;
  }
  List<DerivationRule> getRules() {
    return derivationRules;
  }
  private void setStartSymbols(final NonTerminal[] startSymbols) {
    this.startSymbols.addAll(Arrays.asList(startSymbols));
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
    public NormalDeriver to(final Symbol term) {
      return new NormalDeriver(lhs, term);
    }
  }

  /**
   * Currently deriving a normal rule
   * 
   * @author Tomer
   *
   */
  public final class NormalDeriver extends Deriver {
    NormalDeriver(final NonTerminal lhs, final Symbol child) {
      super(lhs, child);
    }
    NormalDeriver(final NonTerminal lhs, final Symbol firstChild, final Symbol secondChild) {
      super(lhs, firstChild, secondChild);
    }
    public InitialDeriver or() {
      return derive(lhs);
    }
    public NormalDeriver and(final Symbol symb) {
      symbols.add(symb);
      return this;
    }
    @SuppressWarnings("synthetic-access") @Override protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
  }

  public class InitialConfigurator {
    @SuppressWarnings("synthetic-access") public AfterName setApiNameTo(final String apiName) {
      setApiName(apiName);
      return new AfterName();
    }
  }

  public class AfterName {
    @SuppressWarnings("synthetic-access") public NewOverload setStartSymbols(final NonTerminal nt, final NonTerminal... nts) {
      NonTerminal[] newNts = Arrays.copyOf(nts, nts.length + 1);
      newNts[nts.length] = nt;
      BNFBuilder.this.setStartSymbols(newNts);
      return new NewOverload();
    }
  }

  public class NewOverload {
    public OverloadWith overload(final Terminal t) {
      return new OverloadWith(t.name());
    }
    public EndConfig endConfig() {
      return new EndConfig();
    }
  }

  public class OverloadWith {
    private final String terminalName;

    OverloadWith(final String terminalName) {
      this.terminalName = terminalName;
    }
    @SuppressWarnings("synthetic-access") public NewOverload with(final Class<?> clss1, final Class<?>... classes) {
      addOverload(terminalName, new Type(clss1, classes));
      return new NewOverload();
    }
  }

  public class EndConfig {
    public InitialDeriver derive(final NonTerminal nt) {
      return new InitialDeriver(nt);
    }
  }
}
