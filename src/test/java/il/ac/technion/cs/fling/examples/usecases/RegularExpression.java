package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.RegularExpression.re;
import static il.ac.technion.cs.fling.examples.generated.RegularExpression.RE.*;
import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;
/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 *
 * @author Yossi Gil
 * @since April 2019 */
enum RegularExpression {
  ;

  public static void compilationTest() {
    re().noneOrMore(exactly("a").and().option(exactly("b"))).or().oneOrMore(anyDigit()).$();
  }
}
