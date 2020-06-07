package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.GeneralizedSymbol.oneOrMore;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.V.*;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.ScalaAPIAdapter;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.SubFigure.*;
import il.ac.technion.cs.fling.grammars.LL1;
import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.*;
import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;
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
  @Override public BNF BNF() {
    return bnf(). //
        start(Figure). //
        derive(Figure).to(load.with(String.class)). //
        derive(Figure).to(Orientation, oneOrMore(Figure), seal). //
        derive(Orientation).to(row).or(column). //
        build();
  }
  public static void main(String[] args) {
    SubFigure language = new SubFigure();
    Namer namer = new NaiveNamer("SubFigure");
    LL1 ll1 = new LL1(language.BNF(), namer);
    DPDA<Named, Verb, Named> dpda = ll1.buildAutomaton(ll1.bnf.reachableSubBNF());
    APICompiler compiler = new ReliableAPICompiler(dpda);
    PolymorphicLanguageAPIBaseAdapter adapter = new ScalaAPIAdapter("$", namer) {
      // Ignore parameters:
      @Override public String printParametersList(@SuppressWarnings("unused") final APICompiler.MethodDeclaration declaration) {
        return "";
      }
    };
    String output = adapter.printFluentAPI(compiler.compileFluentAPI());
    System.out.println(output);
  }
}
