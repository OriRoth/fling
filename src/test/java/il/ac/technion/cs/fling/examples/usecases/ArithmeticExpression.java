package il.ac.technion.cs.fling.examples.usecases;

import static il.ac.technion.cs.fling.examples.generated.ArithmeticExpression.α.v;

public class ArithmeticExpression {
  public static void compilationTest() {
    v("X"). // define variable X
        times().v("X").plus().v("Y"). // X * X + Y
        times().begin(). // X* X + Y(
        n(12.3).plus().v("X").end(); // X* X + Y(12.3+X)
  }
}
