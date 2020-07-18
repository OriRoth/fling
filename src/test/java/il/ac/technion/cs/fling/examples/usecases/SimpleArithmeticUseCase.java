package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.SimpleArithmetic.i;
import il.ac.technion.cs.fling.examples.generated.SimpleArithmeticAST.*;
@SuppressWarnings("unused") enum SimpleArithmeticUseCase {
  ;
  public static void main(final String[] args) {
    final E e = i(2).mult().begin().i(3).plus().i(4).end().$();
    System.out.println("2 * (3 + 4) = " + evaluate(e));
  }
  private static int evaluate(final E e) {
    return evaluate(e.t) + evaluate(e.e_);
  }
  private static int evaluate(final E_ e) {
    return e instanceof E_1 ? evaluate((E_1) e) : evaluate((E_2) e);
  }
  private static int evaluate(final E_1 e) {
    return evaluate(e.t) + evaluate(e.e_);
  }
  private static int evaluate(final E_2 e) {
    return 0;
  }
  private static int evaluate(final T t) {
    return evaluate(t.f) * evaluate(t.t_);
  }
  private static int evaluate(final T_ t) {
    return t instanceof T_1 ? evaluate((T_1) t) : evaluate((T_2) t);
  }
  private static int evaluate(final T_1 t) {
    return evaluate(t.f) * evaluate(t.t_);
  }
  private static int evaluate(final T_2 t) {
    return 1;
  }
  private static int evaluate(final F f) {
    return f instanceof F1 ? evaluate((F1) f) : evaluate((F2) f);
  }
  private static int evaluate(final F1 f) {
    return evaluate(f.e);
  }
  private static int evaluate(final F2 f) {
    return f.i;
  }
}
