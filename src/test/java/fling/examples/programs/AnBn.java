package fling.examples.programs;

import static fling.examples.automata.AnBn.CppFluentAPI;
import static fling.examples.generated.AnBn.a;

/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
public class AnBn {
  public static void compilationTest() {
    a().a().a().b().b().b().$();
    // a().a().a().b().b().a();
  }
  public static void main(final String[] args) {
    System.out.println(CppFluentAPI);
  }
}
