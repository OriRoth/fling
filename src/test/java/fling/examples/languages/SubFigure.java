package fling.examples.languages;

import static fling.examples.languages.SubFigure.V.Figure;
import static fling.examples.languages.SubFigure.V.Orientation;
import static fling.examples.languages.SubFigure.Σ.column;
import static fling.examples.languages.SubFigure.Σ.load;
import static fling.examples.languages.SubFigure.Σ.row;
import static fling.examples.languages.SubFigure.Σ.seal;
import static fling.grammars.BNF.bnf;
import static fling.internal.grammar.sententials.Notation.oneOrMore;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Variable;

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
      "fling.examples.generated", "SubFigure", Σ.class);
}
