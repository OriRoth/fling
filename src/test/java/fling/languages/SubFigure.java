package fling.languages;

import static fling.grammar.BNF.bnf;
import static fling.grammar.sententials.Notation.oneOrMore;
import static fling.languages.SubFigure.V.Figure;
import static fling.languages.SubFigure.V.Orientation;
import static fling.languages.SubFigure.Σ.column;
import static fling.languages.SubFigure.Σ.load;
import static fling.languages.SubFigure.Σ.row;
import static fling.languages.SubFigure.Σ.seal;

import fling.adapters.JavaMediator;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class SubFigure {
  public enum Σ implements Terminal {
    load, row, column, seal
  }

  public enum V implements Variable {
    Figure, Orientation
  }

  public static final BNF bnf = bnf(V.class). //
      start(Figure). //
      derive(Figure).to(load.with(String.class)). //
      derive(Figure).to(Orientation, oneOrMore(Figure), seal). //
      derive(Orientation).to(row). //
      derive(Orientation).to(column). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "SubFigure", Σ.class);
}
