package il.ac.technion.cs.fling.examples.usecases;

import static il.ac.technion.cs.fling.examples.automata.LongFall.CppFluentAPI;
import static il.ac.technion.cs.fling.examples.generated.LongFall.a;
import static il.ac.technion.cs.fling.examples.generated.LongFall.b;

import il.ac.technion.cs.fling.examples.LoopOverLanguageDefinitions;

/** This class demonstrates the use of automatically generated fluent API.
 * Needless to say, it cannot be compiled before this fluent API was generated.
 * To generate the respective fluent APIs, run
 * {@link LoopOverLanguageDefinitions}.
 *
 * @author Yossi Gil
 * @since April 2019 */
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
