package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.Symbol.oneOrMore;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.V.*;
import static il.ac.technion.cs.fling.examples.languages.SubFigure.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;

public class SubFigure {
  public enum Σ implements Terminal {
    load, row, column, seal
  }

  public enum V implements Variable {
    Figure, Orientation
  }

  public static final BNF bnf = bnf(). //
      start(Figure). //
      derive(Figure).to(load.with(String.class)). //
      derive(Figure).to(Orientation, oneOrMore(Figure), seal). //
      derive(Orientation).to(row).or(column). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "il.ac.technion.cs.fling.examples.generated", "SubFigure", Σ.class);
}
