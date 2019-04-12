package fling.examples.usecases;

import static fling.examples.automata.LongFall.CppFluentAPI;
import static fling.examples.generated.LongFall.*;

import fling.examples.ExamplesMainRunMeFirst;
/**
 * This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run {@link ExamplesMainRunMeFirst}.
 * 
 * @author Yossi Gil
 * @since April 2019
 */
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
