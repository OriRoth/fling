package fling.examples.languages;

import static fling.examples.languages.SubFigure.V.*;
import static fling.examples.languages.SubFigure.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;

import fling.*;
import fling.BNF;
import fling.adapters.JavaMediator;

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
      derive(Figure).to(Orientation, Symbol.oneOrMore(Figure), seal). //
      derive(Orientation).to(row).or(column). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "SubFigure", Σ.class);
}
