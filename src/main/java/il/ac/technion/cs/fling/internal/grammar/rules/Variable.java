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
        if (!(o instanceof Variable))
          return false;
        final Variable other = (Variable) o;
        return name().equals(other.name());
      }
    };
  }
}
