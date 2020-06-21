package il.ac.technion.cs.fling.examples.automata;
import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Q.q0;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Q.q1;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Γ.A;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Γ.B;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Γ.E;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Σ.a;
import static il.ac.technion.cs.fling.examples.automata.AeqBTest.Σ.b;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolynomialAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveLinker;
/** AeqB = {w in {a, b}* | #a in w = #b in w}.
 *
 * @author Ori Roth */
public class AeqBTest {
  /** Set of DPDA states. */
  enum Q implements Named {
    q0, q1
  }
  /** Set of DPDA input letters. */
  enum Σ implements Terminal {
    a, b
  }
  /** Set of DPDA stack symbols. */
  enum Γ implements Named {
    E, A, B
  }
  /** DPDA accepting AeqB. */
  public static final DPDA<Named, Token, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(E) //
      .δ(q0, a, E, q1, E, A) //
      .δ(q0, b, E, q1, E, B) //
      .δ(q1, ε(), E, q0, E) //
      .δ(q1, a, A, q1, A, A) //
      .δ(q1, a, B, q1) //
      .δ(q1, b, A, q1) //
      .δ(q1, b, B, q1, B, B) //
      .go());
  @Test public void test1() {
    final Model m = new PolynomialAPICompiler(dpda).go();
    new CPPGenerator(namer).go(m);
    System.out.println(new CPPGenerator(namer).go(m));
  }
  @Test public void test2() {
    final Model m = new PolynomialAPICompiler(dpda).go();
    new CPPGenerator(namer).go(m);
    System.out.println(
        new JavaGenerator(namer, getClass().getPackageName() + ".generated", getClass().getSimpleName()).go(m));
  }
  private final NaiveLinker namer = new NaiveLinker("AeqB");
}
