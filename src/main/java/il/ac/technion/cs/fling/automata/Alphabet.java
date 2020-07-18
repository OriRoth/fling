package il.ac.technion.cs.fling.automata;
/** Alphabet utilities.
 *
 * @author Ori Roth */
public enum Alphabet {
  ;

  /** The empty word, ε.
   *
   * @param <T> Terminal type
   * @return ε value */
  public static <T> T ε() {
    return null;
  }
}
