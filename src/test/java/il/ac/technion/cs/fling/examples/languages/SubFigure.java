package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.SubFigure.V.Figure;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.V.Orientation;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.column;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.load;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.row;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.seal;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.Named;
import il.ac.technion.cs.fling.adapters.ScalaAPIAdapter;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.SubFigure.V;
import il.ac.technion.cs.fling.examples.languages.SubFigure.Σ;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.APICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.PolymorphicLanguageAPIBaseAdapter;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class SubFigure implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    load, row, column, seal
  }

  public enum V implements Variable {
    Figure, Orientation
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  @Override public FancyEBNF BNF() {
    return bnf(). //
        start(Figure). //
        derive(Figure).to(load.with(String.class)). //
        derive(Figure).to(Orientation, Quantifiers.oneOrMore(Figure), seal). //
        derive(Orientation).to(row).or(column). //
        build();
  }

  public static void main(String[] args) {
    SubFigure language = new SubFigure();
    Namer namer = new NaiveNamer("SubFigure");
    LL1 ll1 = new LL1(language.BNF(), namer);
    DPDA<Named, Token, Named> dpda = ll1.buildAutomaton(ll1.bnf.reduce());
    APICompiler compiler = new ReliableAPICompiler(dpda);
    PolymorphicLanguageAPIBaseAdapter adapter = new ScalaAPIAdapter("$", namer) {
      // Ignore parameters:
      @Override public String printParametersList(
          @SuppressWarnings("unused") final APICompiler.MethodDeclaration declaration) {
        return "";
      }
    };
    String output = adapter.printFluentAPI(compiler.compileFluentAPI());
    System.out.println(output);
  }
}
