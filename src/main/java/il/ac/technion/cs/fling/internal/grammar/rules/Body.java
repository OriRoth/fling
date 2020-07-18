package il.ac.technion.cs.fling.internal.grammar.rules;
import java.util.Collection;
/** A body of {@link ERule}
 *
 * @author Yossi Gil
 * @since 2020-06-08 */
public class Body extends Word<Component> {
  public Body(final Component... cs) {
    super(cs);
  }
  public Body(final Collection<Component> cs) {
    super(cs);
  }
}
