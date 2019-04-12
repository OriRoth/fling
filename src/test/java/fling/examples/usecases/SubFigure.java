package fling.examples.usecases;
import static fling.examples.generated.SubFigure.row;

import fling.examples.ExamplesMainRunMeFirst;
import fling.examples.generated.SubFigureAST.*;
/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
public class SubFigure {
  // @formatter:off
  public static void main(final String[] args) {
    final Figure fig =
      row().
        column().
          load("p1").
          load("p2").
          load("p3").
        seal().
        column().
          load("p4").
          row().
            load("p5").
            load("p6").
          seal().
        seal().
      seal().$();
    System.out.println(toString(fig));
  }
  // @formatter:on
  public static String toString(final Figure fig) {
    final int h = getHeight(fig), w = getWidth(fig);
    final char[][] table = new char[h * 5][w * 6];
    for (int i = 0; i < table.length; ++i)
      for (int j = 0; j < table[i].length; ++j)
        table[i][j] = ' ';
    fillTable(fig, table, 0, 0, h, w);
    final StringBuilder $ = new StringBuilder();
    for (final char[] row : table) {
      for (final char c : row)
        $.append(c);
      $.append('\n');
    }
    return $.toString().trim();
  }
  private static void fillTable(final Figure fig, final char[][] table, final int i, final int j, final int h, final int w) {
    if (fig instanceof Figure1)
      fillTable((Figure1) fig, table, i, j, h, w);
    else
      fillTable((Figure2) fig, table, i, j, h, w);
  }
  private static void fillTable(final Figure1 fig, final char[][] table, int i, int j, int h, int w) {
    i *= 5;
    h *= 5;
    j *= 6;
    w *= 6;
    for (int jj = j; jj < j + w; ++jj)
      table[i][jj] = '*';
    for (int jj = j; jj < j + w; ++jj)
      table[i + h - 1][jj] = '*';
    for (int ii = i; ii < i + h; ++ii)
      table[ii][j] = '*';
    for (int ii = i; ii < i + h; ++ii)
      table[ii][j + w - 1] = '*';
    final char n1 = fig.load.isEmpty() ? ' ' : fig.load.charAt(0);
    final char n2 = fig.load.length() < 1 ? ' ' : fig.load.charAt(1);
    table[i + h / 2][j + w / 2 - 1] = n1;
    table[i + h / 2][j + w / 2] = n2;
  }
  @SuppressWarnings("null") private static void fillTable(final Figure2 composite, final char[][] table, final int i, final int j,
      final int h, final int w) {
    if (isRow(composite.orientation)) {
      final int totalWidth = composite.figure.stream().map(SubFigure::getWidth).reduce(0, Integer::sum);
      int k = 0;
      for (int l = 0; l < composite.figure.size(); ++l) {
        final Figure fig = composite.figure.get(l);
        int figW = w / totalWidth * getWidth(fig);
        if (l == composite.figure.size() - 1)
          figW = w - k;
        fillTable(fig, table, i, j + k, h, figW);
        k += figW;
      }
    } else {
      final int totalHeight = composite.figure.stream().map(SubFigure::getHeight).reduce(0, Integer::sum);
      int k = 0;
      for (int l = 0; l < composite.figure.size(); ++l) {
        final Figure fig = composite.figure.get(l);
        int figH = h / totalHeight * getHeight(fig);
        if (l == composite.figure.size() - 1)
          figH = h - k;
        fillTable(fig, table, i + k, j, figH, w);
        k += figH;
      }
    }
  }
  @SuppressWarnings("null") public static int getHeight(final Figure fig) {
    if (fig instanceof Figure1)
      return 1;
    final Figure2 composite = (Figure2) fig;
    return isRow(composite.orientation) ? //
        composite.figure.stream() //
            .map(SubFigure::getHeight) //
            .max(Integer::compareTo).get() //
        : composite.figure.stream() //
            .map(SubFigure::getHeight) //
            .reduce(Integer::sum).get();
  }
  @SuppressWarnings("null") public static int getWidth(final Figure fig) {
    if (fig instanceof Figure1)
      return 1;
    final Figure2 composite = (Figure2) fig;
    return !isRow(composite.orientation) ? //
        composite.figure.stream() //
            .map(SubFigure::getWidth) //
            .max(Integer::compareTo).get() //
        : composite.figure.stream() //
            .map(SubFigure::getWidth) //
            .reduce(Integer::sum).get();
  }
  public static boolean isRow(final Orientation orientation) {
    return orientation instanceof Orientation1;
  }
}
