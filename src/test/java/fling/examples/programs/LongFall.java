package fling.examples.programs;

import static fling.examples.automata.LongFall.CppFluentAPI;
import static fling.examples.generated.LongFall.a;
import static fling.examples.generated.LongFall.b;

public class LongFall {
  public static void compilationTest() {
    a().a().a().a().a().a().a().$();
    a().a().a().a().a().a().a().b().$();
    b().$();
  }
  public static void main(final String[] args) {
    System.out.println(CppFluentAPI);
  }
}
