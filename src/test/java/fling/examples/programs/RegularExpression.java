package fling.examples.programs;

import static fling.examples.generated.RegularExpression.re;
import static fling.examples.generated.RegularExpression.RE.*;
/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
public class RegularExpression {
  public static void compilationTest() {
    re().noneOrMore(exactly("a").and().option(exactly("b"))).or().oneOrMore(anyDigit()).$();
  }
}
