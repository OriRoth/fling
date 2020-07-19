package il.ac.technion.cs.fling.internal.grammar.rules;
/** Named entity.
 *
 * @author Ori Roth */
public interface Named {
  /** @return name */
  String name();
  static Named by(final String name) {
    return new Named() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(final Object o) {
        if (o == this)
          return true;
        if (!(o instanceof Named))
          return false;
        return name.equals(((Named) o).name());
      }
    };
  }
}
