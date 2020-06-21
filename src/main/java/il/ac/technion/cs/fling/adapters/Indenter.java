package il.ac.technion.cs.fling.adapters;
import java.util.stream.Stream;
/** A fluent APi class for generating indented text; text is accumulated in an
 * internal {@link StringBuilder} and can be retrieved at any time.
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-18 */
public class Indenter {
  private int indent;
  private final StringBuilder builder = new StringBuilder();
  /** @return The current contents of the builder */
  public String contents() {
    return builder.toString();
  }
  /** Adds a line to the buffer, after indenting it
   * 
   * @param line a line to add
   * 
   * @return <b><code>this</code></b> */
  Indenter line(final String line) {
    builder.append(line.indent(indent));
    return this;
  }
  /** Adds any number of lines to the buffer, after indenting them
   * 
   * @param lines lines to add
   * 
   * @return <b><code>this</code></b> */
  Indenter lines(final String... lines) {
    for (final String line : lines)
      line(line);
    return this;
  }
  /** Adds any number of lines to the buffer, after indenting them
   * 
   * @param lines lines to add
   * 
   * @return <b><code>this</code></b> */
  Indenter lines(final Stream<String> lines) {
    lines.forEach(this::line);
    return this;
  }
  /** Format text, and add the formatted line to the buffer, after indenting it
   * 
   * @param format the formatting string
   * @param os     parameters to format
   * 
   * @return <b><code>this</code></b> */
  Indenter linef(final String format, final Object... os) {
    return line(String.format(format, os));
  }
  Indenter indent() {
    indent += 2;
    return this;
  }
  Indenter unindent() {
    assert indent > 0;
    indent -= 2;
    return this;
  }
  int level() {
    return indent / 2;
  }
  protected void ____() {
    line("\n");
  }
}
