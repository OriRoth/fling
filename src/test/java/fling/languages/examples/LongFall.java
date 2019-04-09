package fling.languages.examples;

import static fling.generated.LongFall.a;
import static fling.generated.LongFall.b;
import static fling.languages.LongFall.CppFluentAPI;

public class LongFall {
  public static void compilationTest() {
    a().a().a().a().a().a().a().$();
    a().a().a().a().a().a().a().b().$();
    b().$();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
