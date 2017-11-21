package org.spartan.fajita.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;

import static org.spartan.fajita.api.bnf.BNFRenderer.builtin.*;

/**
 * @author Tomer
 */
public class Fajita {
  private final List<DerivationRule> derivationRules;
  private final List<Terminal> terminals;
  private final Set<Verb> verbs;
  private final List<NonTerminal> nonterminals;
  /**
   * All Nonterminals that start the LL derivation
   */
  private final List<NonTerminal> startSymbols;
  /**
   * Keeps record of all NonTerminals used as parameters to some other verb.
   */
  private final Set<NonTerminal> nestedParameters;
  private String apiName;
  private String packagePath;

  public <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> Fajita(final Class<Term> terminalEnum,
      final Class<NT> nonterminalEnum) {
    terminals = new ArrayList<>(EnumSet.allOf(terminalEnum));
    verbs = new LinkedHashSet<>();
    nonterminals = new ArrayList<>(EnumSet.allOf(nonterminalEnum));
    derivationRules = new ArrayList<>();
    startSymbols = new ArrayList<>();
    nestedParameters = new HashSet<>();
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
  void addNestedParameter(NonTerminal nested) {
    this.getNestedParameters().add(nested);
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
    return this.apiName;
  }
  void setApiName(String apiName) {
    this.apiName = apiName;
  }
  Map<String, String> finish(String pckg) {
    validate();
    setPackagePath(pckg);
    return FajitaEncoder.encode(this);
  }
  public String getPackagePath() {
    return packagePath;
  }
  public void setPackagePath(String packagePath) {
    this.packagePath = packagePath;
  }
  /* ***************************************************************************
   * ***************************************************************************
   * ----------------------- Fluent Interface Of Fajita ------------------------
   * ***************************************************************************
   * **************************************************************************
   * * */
  public static <Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> SetSymbols buildBNF(
      final Class<Term> terminalEnum, final Class<NT> nonterminalEnum) {
    Fajita builder = new Fajita(terminalEnum, nonterminalEnum);
    return builder.new SetSymbols();
  }
  public Set<NonTerminal> getNestedParameters() {
    return nestedParameters;
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
    public Map<String, String> go(String pckg) {
      return finish(pckg);
    }
    /**
     * Adds a rule to the BnfBuilder host.
     */
    protected void addRuleToBNF() {
      addRule(lhs, symbols);
    }
    @Override public String toString() {
      return new BNF(getVerbs(), getNonTerminals(), getRules(), getStartSymbols(), getApiName()).render(ASCII);
    }
    public BNF go() {
      if (!symbols.isEmpty())
        addRuleToBNF();
      return new BNF(getVerbs(), getNonTerminals(), getRules(), getStartSymbols(), getApiName());
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
    public AndDeriver to(final Terminal term, VarArgs varargs) {
      return new AndDeriver(lhs, new Verb(term, varargs));
    }
    public AndDeriver to(final Terminal term, NonTerminal nested) {
      addNestedParameter(nested);
      return new AndDeriver(lhs, new Verb(term, nested));
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
      return or(new AndDeriver(lhs, new Verb(term, type)));
    }
    public AndDeriver or(final Terminal term, VarArgs varargs) {
      return or(new AndDeriver(lhs, new Verb(term, varargs)));
    }
    public AndDeriver or(final Terminal term, NonTerminal nested) {
      addNestedParameter(nested);
      return or(new AndDeriver(lhs, new Verb(term, nested)));
    }
    public AndDeriver or(final NonTerminal nt) {
      return or(new AndDeriver(lhs, nt));
    }
    private AndDeriver or(AndDeriver deriver) {
      addRule(lhs, symbols);
      return deriver;
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
    AndDeriver(final NonTerminal lhs, final Symbol child) {
      super(lhs);
      symbols.add(child);
    }
    public AndDeriver and(final NonTerminal nt) {
      return and((Symbol) nt);
    }
    public AndDeriver and(final Terminal term, VarArgs varargs) {
      return and(new Verb(term, varargs));
    }
    public AndDeriver and(final Terminal term, NonTerminal nested) {
      addNestedParameter(nested);
      return and(new Verb(term, nested));
    }
    public AndDeriver and(final Terminal term, Class<?>... type) {
      return and(new Verb(term, type));
    }
    private AndDeriver and(final Symbol symb) {
      symbols.add(symb);
      return this;
    }
    @Override public Map<String, String> go(String pckg) {
      addRuleToBNF();
      return super.go(pckg);
    }
  }

  public class FirstDerive {
    public InitialDeriver derive(final NonTerminal nt) {
      return new InitialDeriver(nt);
    }
  }

  public static VarArgs ellipsis(Class<?> clazz) {
    return new VarArgs(clazz);
  }
}