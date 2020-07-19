package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.SubFigure.row;
import java.util.Arrays;
import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;
import il.ac.technion.cs.fling.examples.generated.SubFigureAST.*;
/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 *
 * @author Yossi Gil
 * @since April 2019 */
enum SubFigure {
  ;
  // @formatter:off
  public static void main(final String[] args) {
    final var fig =
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
  private static String toString(final Figure fig) {
    final int h = getHeight(fig), w = getWidth(fig);
    final var table = new char[h * 5][w * 6];
    for (final char[] chars : table)
      Arrays.fill(chars, ' ');
    fillTable(fig, table, 0, 0, h, w);
    final var $ = new StringBuilder();
    for (final char[] row : table) {
      for (final char c : row)
        $.append(c);
      $.append('\n');
    }
    return $.toString().trim();
  }
  private static void fillTable(final Figure fig, final char[][] table, final int i, final int j, final int h,
      final int w) {
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
    for (var jj = j; jj < j + w; ++jj)
      table[i][jj] = '*';
    for (var jj = j; jj < j + w; ++jj)
      table[i + h - 1][jj] = '*';
    for (var ii = i; ii < i + h; ++ii)
      table[ii][j] = '*';
    for (var ii = i; ii < i + h; ++ii)
      table[ii][j + w - 1] = '*';
    final var n1 = fig.load.isEmpty() ? ' ' : fig.load.charAt(0);
    final var n2 = fig.load.length() < 1 ? ' ' : fig.load.charAt(1);
    table[i + h / 2][j + w / 2 - 1] = n1;
    table[i + h / 2][j + w / 2] = n2;
  }
  private static void fillTable(final Figure2 composite, final char[][] table, final int i, final int j, final int h,
      final int w) {
    if (isRow(composite.orientation)) {
      final int totalWidth = composite.figure.stream().map(SubFigure::getWidth).reduce(0, Integer::sum);
      var k = 0;
      for (var l = 0; l < composite.figure.size(); ++l) {
        final var fig = composite.figure.get(l);
        var figW = w / totalWidth * getWidth(fig);
        if (l == composite.figure.size() - 1)
          figW = w - k;
        fillTable(fig, table, i, j + k, h, figW);
        k += figW;
      }
    } else {
      final int totalHeight = composite.figure.stream().map(SubFigure::getHeight).reduce(0, Integer::sum);
      var k = 0;
      for (var l = 0; l < composite.figure.size(); ++l) {
        final var fig = composite.figure.get(l);
        var figH = h / totalHeight * getHeight(fig);
        if (l == composite.figure.size() - 1)
          figH = h - k;
        fillTable(fig, table, i + k, j, figH, w);
        k += figH;
      }
    }
  }
  private static int getHeight(final Figure fig) {
    if (fig instanceof Figure1)
      return 1;
    final var composite = (Figure2) fig;
    return isRow(composite.orientation) ? //
        composite.figure.stream() //
            .map(SubFigure::getHeight) //
            .max(Integer::compareTo).get() //
        : composite.figure.stream() //
            .map(SubFigure::getHeight) //
            .reduce(Integer::sum).get();
  }
  private static int getWidth(final Figure fig) {
    if (fig instanceof Figure1)
      return 1;
    final var composite = (Figure2) fig;
    //
    //
    return isRow(composite.orientation) ? composite.figure.stream() //
        .map(SubFigure::getWidth) //
        .reduce(Integer::sum).get()
        : composite.figure.stream() //
            .map(SubFigure::getWidth) //
            .max(Integer::compareTo).get();
  }
  private static boolean isRow(final Orientation orientation) {
    return orientation instanceof Orientation1;
  }
}
