package il.ac.technion.cs.fling.internal.grammar;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
public abstract class Grammar {
  public final FancyEBNF ebnf;
  private final Linker namer;
  public final FancyEBNF bnf;
  public final FancyEBNF normalizedBNF;
  public final FancyEBNF normalizedEBNF;
  private final Map<Variable, FancyEBNF> subBNFs;
  public Grammar(final FancyEBNF ebnf, final Linker namer) {
    this.ebnf = ebnf;
    this.namer = namer;
    bnf = BNFUtils.expandQuantifiers(ebnf);
    normalizedEBNF = BNFUtils.normalize(ebnf);
    normalizedBNF = BNFUtils.expandQuantifiers(normalizedEBNF);
    subBNFs = new LinkedHashMap<>();
    for (final Variable head : bnf.headVariables)
      subBNFs.put(head, BNFUtils.reduce(bnf, head));
  }
  public abstract DPDA<Named, Token, Named> buildAutomaton(FancyEBNF bnf);
  // TODO compute lazily.
  public DPDA<Named, Token, Named> toDPDA() {
    return buildAutomaton(bnf);
  }
  public FancyEBNF getSubBNF(final Variable variable) {
    return subBNFs.get(variable);
  }
  @SuppressWarnings("unused") public static DPDA<Named, Token, Named> cast(
      final DPDA<? extends Named, ? extends Terminal, ? extends Named> dpda) {
    return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
        dpda.Σ().map(Token::new).collect(toSet()), //
        new LinkedHashSet<>(dpda.Γ), //
        dpda.δs.stream() //
            .map(δ -> new DPDA.δ<Named, Token, Named>(δ.q, δ.σ == ε() ? ε() : new Token(δ.σ), δ.γ, δ.q$,
                new Word<>(δ.getΑ().stream() //
                    .map(Named.class::cast) //
                    .collect(toList())))) //
            .collect(toSet()), //
        new LinkedHashSet<>(dpda.F), //
        dpda.q0, //
        Word.of(dpda.γ0.stream().map(Named.class::cast)));
  }
}
