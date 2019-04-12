package fling.examples.programs;

import static fling.examples.generated.RegularExpression.re;
import static fling.examples.generated.RegularExpression.RE.*;

public class RegularExpression {
  public static void compilationTest() {
    re().noneOrMore(exactly("a").and().option(exactly("b"))).or().oneOrMore(anyDigit()).$();
  }
}
