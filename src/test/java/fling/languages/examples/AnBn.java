package fling.languages.examples;

import static fling.generated.AnBn.a;
import static fling.languages.AnBn.CppFluentAPI;

public class AnBn {
  public static void compilationTest() {
    a().a().a().b().b().b().$();
    // a().a().a().b().b().a();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
