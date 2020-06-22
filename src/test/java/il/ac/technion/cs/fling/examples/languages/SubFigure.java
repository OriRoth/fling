package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Γ.Figure;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Γ.Orientation;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.column;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.load;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.row;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.seal;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.APIGenerator;
import il.ac.technion.cs.fling.adapters.ScalaGenerator;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.SubFigure.Γ;
import il.ac.technion.cs.fling.examples.languages.SubFigure.Σ;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodSignature;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.namers.NaiveNamer;
public class SubFigure implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    load, row, column, seal
  }
  public enum Γ implements Variable {
    Figure, Orientation
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf(). //
        start(Figure). //
        derive(Figure).to(load.with(String.class)). //
        derive(Figure).to(Orientation, Quantifiers.oneOrMore(Figure), seal). //
        derive(Orientation).to(row).or(column). //
        build();
  }
  public static void main(final String[] args) {
    final SubFigure language = new SubFigure();
    final Namer namer = new NaiveNamer("SubFigure");
    final LL1 ll1 = new LL1(FancyEBNF.from(language.BNF()), namer);
    final DPDA<Named, Token, Named> dpda = ll1.buildAutomaton(ll1.bnf.reduce());
    final APICompiler compiler = new ReliableAPICompiler(dpda);
    final APIGenerator adapter = new ScalaGenerator(namer) {
      // Ignore parameters:
      @Override public String printParametersList(@SuppressWarnings("unused") final MethodSignature declaration) {
        return "";
      }
    };
    final String output = adapter.go(compiler.compileFluentAPI());
    System.out.println(output);
  }
}
