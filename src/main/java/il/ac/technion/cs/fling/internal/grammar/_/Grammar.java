package il.ac.technion.cs.fling.internal.grammar._;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import java.util.LinkedHashSet;
import static java.util.stream.Collectors.*;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
public class Grammar {
  public final FancyEBNF ebnf;
  public final FancyEBNF bnf;
  public final FancyEBNF normalizedBNF;
  public final FancyEBNF normalizedEBNF;
  public Grammar(final FancyEBNF ebnf) {
    this.ebnf = ebnf;
    bnf = BNFUtils.expandQuantifiers(ebnf);
    normalizedEBNF = BNFUtils.normalize(ebnf);
    normalizedBNF = BNFUtils.expandQuantifiers(normalizedEBNF);
  }
  public FancyEBNF getSubBNF(final Variable v) {
    return BNFUtils.reduce(bnf, v);
  }
  public static DPDA<Named, Token, Named> cast(final DPDA<Named, Terminal, Named> dpda) {
    return new DPDA<>(new LinkedHashSet<>(dpda.Q), //
        dpda.Σ().map(Token::new).collect(toSet()), //
        new LinkedHashSet<>(dpda.Γ), //
        dpda.δs.stream() //
            .map(δ -> new DPDA.δ<>(δ.q, δ.σ == ε() ? ε() : new Token(δ.σ), δ.γ, δ.q$, new Word<>(δ.α.stream() //
                .map(Named.class::cast) //
                .collect(toList())))) //
            .collect(toSet()), //
        new LinkedHashSet<>(dpda.F), //
        dpda.q0, //
        Word.of(dpda.γ0.stream().map(Named.class::cast)));
  }
}
