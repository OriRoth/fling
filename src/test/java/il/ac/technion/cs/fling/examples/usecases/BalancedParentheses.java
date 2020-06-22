package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.BalancedParentheses.c;
import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;
/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 *
 * @author Yossi Gil
 * @since April 2019 */
public class BalancedParentheses {
  public static void compilationTest() {
    c().ↄ().$();
    // c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().ↄ().ↄ().c().ↄ().$();
    c().c().ↄ().ↄ().c();
    // ↄ();
  }
}
