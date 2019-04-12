package fling.examples.programs;

import static fling.examples.generated.Arithmetic.v;
/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
public class Arithmetic {
  public static void compilationTest() {
    v("X"). // define variable X
        times().v("X").plus().v("Y"). // X * X + Y
        times().begin(). // X* X + Y(
        n(12.3).plus().v("X").end(); // X* X + Y(12.3+X)
  }
}
