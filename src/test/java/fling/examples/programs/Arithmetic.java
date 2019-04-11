package fling.examples.programs;

import static fling.examples.generated.Arithmetic.v;

public class Arithmetic {
  public static void compilationTest() {
    v("X"). // define variable X
        times().v("X").plus().v("Y"). // X * X + Y
        times().begin(). // X* X + Y(
        n(12.3).plus().v("X").end(); // X* X + Y(12.3+X)
  }
}
