package il.ac.technion.cs.fling.internal.grammar.rules;
/** Grammar sentential variable.
 *
 * @author Ori Roth */
public interface Variable extends Symbol {
  static Variable byName(final String name) {
    return new Variable() {
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
        if (this == o)
          return true;
        if (o == null)
          return false;
        if (o instanceof Variable v)
          return name().equals(v.name());
        return false;
      }
    };
  }
}
