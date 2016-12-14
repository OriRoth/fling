package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.generation.RLLPEncoder;

/**
 * @author Tomer
 */
public class Fajita {
  private final List<DerivationRule> derivationRules;
  private final List<Terminal> terminals;
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  private final List<NonTerminal> startSymbols;
  private String ApiName;
  public static final Class<VARARGS> VARARGS = Fajita.VARARGS.class;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> Fajita(final Class<Term> terminalEnum,
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
  public List<NonTerminal> getNonTerminals() {
    return nonterminals;
  }
  public Set<Verb> getVerbs() {
    return verbs;
  }
  public List<NonTerminal> getStartSymbols() {
    return startSymbols;
  }
  private Fajita checkNewRule(final DerivationRule r) {
    if (!symbolExists(r.lhs))
      throw new IllegalArgumentException(r.lhs.name() + " is undefined.");
    if (derivationRules.contains(r))
      throw new IllegalArgumentException("rule " + r + " already exists");
    return this;
  }
  void addRule(final NonTerminal lhs, final List<Symbol> symbols) {
    DerivationRule r = new DerivationRule(lhs, symbols);
    symbols.stream().filter(s -> s.isVerb()).forEach(v -> verbs.add((Verb) v));
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
  public List<DerivationRule> getRules() {
    return derivationRules;
  }
  public String getApiName() {
    return this.ApiName;
  }
  void setApiName(String ApiName) {
    this.ApiName = ApiName;
  }
  private void finish() {
    validate();
    nonterminals.add(SpecialSymbols.augmentedStartSymbol);
    verbs.add(SpecialSymbols.$);
    for (NonTerminal startSymbol : getStartSymbols())
      addRule(SpecialSymbols.augmentedStartSymbol, Arrays.asList(startSymbol));
    Collection<BNF> bnfs = getAllBNFs();
    // create an RLLP for each BNF (removed duplications for subAPIs)
    // generate a code for each RLLP
    // merge under a single file
    // write static methods.
  }
  private Collection<BNF> getAllBNFs() {
    ArrayList<BNF> $ = new  ArrayList<>();
    BNF main = new BNF(getVerbs(), getNonTerminals(), getRules(), getStartSymbols(), getApiName());
    // Get all nested verbs/nonterminals/whatever
    for (Verb v : getVerbs()){
      // generate BNF for it
      // ApiName should be deterministically generate-able 
      
    }
    return $;
  }
  /* ***************************************************************************
   * ***************************************************************************
   * ----------------------- Fluent Interface Of Fajita ------------------------
   * ***************************************************************************
   * *************************************************************************** */
  public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> SetSymbols buildBNF(
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
    Fajita builder = new Fajita(terminalEnum, nonterminalEnum);
    return builder.new SetSymbols();
  }

  public class SetSymbols {
    public ApiName setApiName(String name) {
      Fajita.this.setApiName(name);
      return new ApiName();
    }
  }

  public class ApiName {
    public FirstDerive start(final NonTerminal nt, final NonTerminal... nts) {
      NonTerminal[] newNts = Arrays.copyOf(nts, nts.length + 1);
      newNts[nts.length] = nt;
      Fajita.this.getStartSymbols().addAll(Arrays.asList(newNts));
      return new FirstDerive();
    }
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
      return Fajita.this.finish();
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
   */
  public final class AndDeriver extends OrDeriver {
    AndDeriver(final NonTerminal lhs, final NonTerminal child) {
      super(lhs);
      symbols.add(child);
    }
    public AndDeriver(NonTerminal lhs, final Verb child) {
      super(lhs);
      symbols.add(child);
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

  class VARARGS {
    /**/}
}
