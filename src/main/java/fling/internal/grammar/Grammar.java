package fling.internal.grammar;

import static fling.automata.Alphabet.ε;
import static java.util.stream.Collectors.*;

import java.util.*;

import fling.*;
import fling.automata.DPDA;
import fling.internal.compiler.Namer;
import fling.internal.grammar.sententials.*;

public abstract class Grammar {
  public final BNF ebnf;
  public final Namer namer;
  public final BNF bnf;
  public final BNF normalizedBNF;
  public final BNF normalizedEBNF;
  private final Map<Variable, BNF> subBNFs;

  public Grammar(BNF ebnf, Namer namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    this.bnf = getBNF(ebnf);
    this.normalizedEBNF = getNormalizedBNF(ebnf);
    this.normalizedBNF = getBNF(normalizedEBNF);
    subBNFs = new LinkedHashMap<>();
    for (Variable head : bnf.headVariables)
      subBNFs.put(head, computeSubBNF(head));
  }
  public abstract DPDA<Named, Verb, Named> buildAutomaton(BNF bnf);
  // TODO compute lazily.
  public DPDA<Named, Verb, Named> toDPDA() {
    return buildAutomaton(bnf);
  }
  private BNF getBNF(BNF ebnf) {
    Set<Variable> V = new LinkedHashSet<>(ebnf.V);
    Set<DerivationRule> R = new LinkedHashSet<>();
    Map<Variable, Notation> extensionHeadsMapping = new LinkedHashMap<>();
    Set<Variable> extensionProducts = new LinkedHashSet<>();
    for (DerivationRule rule : ebnf.R) {
      List<SententialForm> rhs = new ArrayList<>();
      for (SententialForm sf : rule.rhs) {
        List<Symbol> symbols = new ArrayList<>();
        for (Symbol symbol : sf) {
          if (!symbol.isNotation()) {
            symbols.add(symbol);
            continue;
          }
          Notation notation = symbol.asNotation();
          Variable head = notation.extend(namer, extensionProducts::add, R::add);
          extensionHeadsMapping.put(head, notation);
          symbols.add(head);
        }
        rhs.add(new SententialForm(symbols));
      }
      R.add(new DerivationRule(rule.lhs, rhs));
    }
    V.addAll(extensionProducts);
    return new BNF(ebnf.Σ, V, R, ebnf.startVariable, ebnf.headVariables, extensionHeadsMapping, extensionProducts, false);
  }
  public BNF getSubBNF(Variable variable) {
    return subBNFs.get(variable);
  }
  private BNF computeSubBNF(Variable head) {
    Set<Verb> Σ = new LinkedHashSet<>();
    Set<Variable> V = new LinkedHashSet<>();
    V.add(head);
    Set<DerivationRule> R = new LinkedHashSet<>();
    int previousSize = -1;
    for (; previousSize < R.size();) {
      previousSize = R.size();
      for (DerivationRule rule : bnf.R)
        if (!R.contains(rule) && V.contains(rule.lhs)) {
          R.add(rule);
          for (SententialForm sf : rule.rhs)
            for (Symbol symbol : sf)
              if (symbol.isVariable())
                V.add(symbol.asVariable());
              else if (symbol.isVerb())
                Σ.add(symbol.asVerb());
        }
    }
    return new BNF(Σ, V, R, head, null, null, null, true);
  }
  private BNF getNormalizedBNF(BNF bnf) {
    Set<Variable> V = new LinkedHashSet<>(bnf.V);
    Set<DerivationRule> R = new LinkedHashSet<>();
    for (Variable v : bnf.V) {
      List<SententialForm> rhs = bnf.rhs(v);
      assert rhs.size() > 0;
      if (rhs.size() == 1) {
        // Sequence (or redundant alteration).
        R.add(new DerivationRule(v, rhs));
        continue;
      }
      List<Variable> alteration = new ArrayList<>();
      for (SententialForm sf : rhs)
        if (sf.size() == 1 && sf.stream().allMatch(bnf::isOriginalVariable))
          // Ready alteration variable.
          alteration.add(sf.get(0).asVariable());
        else {
          // Create a suitable child variable.
          Variable a = namer.createASTChild(v);
          V.add(a);
          R.add(new DerivationRule(a, Collections.singletonList(sf)));
          alteration.add(a);
        }
      R.add(new DerivationRule(v, alteration.stream().map(a -> new SententialForm(a)).collect(toList())));
    }
    return new BNF(bnf.Σ, V, R, bnf.startVariable, bnf.headVariables, bnf.extensionHeadsMapping, bnf.extensionProducts, false);
  }
  public static boolean isSequenceRHS(BNF bnf, Variable v) {
    List<SententialForm> rhs = bnf.rhs(v);
    return rhs.size() == 1 && (rhs.get(0).size() != 1 || !bnf.isOriginalVariable(rhs.get(0).get(0)));
  }
  @SuppressWarnings({ "null", "unused" }) public static DPDA<Named, Verb, Named> cast(
      DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
    return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
        dpda.Σ().map(Verb::new).collect(toSet()), //
        new LinkedHashSet<>(dpda.Γ), //
        dpda.δs.stream() //
            .map(δ -> new DPDA.δ<Named, Verb, Named>(δ.q, δ.σ == ε() ? ε() : new Verb(δ.σ), δ.γ, δ.q$, new Word<>(δ.getΑ().stream() //
                .map(Named.class::cast) //
                .collect(toList())))) //
            .collect(toSet()), //
        new LinkedHashSet<>(dpda.F), //
        dpda.q0, //
        Word.of(dpda.γ0.stream().map(Named.class::cast)));
  }
}
