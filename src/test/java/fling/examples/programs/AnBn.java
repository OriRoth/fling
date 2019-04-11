package fling.examples.programs;

import static fling.examples.automata.AnBn.CppFluentAPI;
import static fling.examples.generated.AnBn.a;

public class AnBn {
  public static void compilationTest() {
    a().a().a().b().b().b().$();
    // a().a().a().b().b().a();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
