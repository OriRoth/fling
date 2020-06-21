package il.ac.technion.cs.fling.examples.automata;
import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Q.q0;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Q.q1;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Γ.A;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Γ.B;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Γ.E;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Σ.a;
import static il.ac.technion.cs.fling.examples.automata.AeqB.Σ.b;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.adapters.CSharpGenerator;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.adapters.SMLGenerator;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.PolynomialAPICompiler;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveLinker;
/** AeqB = {w in {a, b}* | #a in w = #b in w}.
 *
 * @author Ori Roth */
public class AeqB {
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
  public static final NaiveLinker namer = new NaiveLinker("il.ac.technion.cs.fling.examples.generated", "AeqB");
  public static final String JavaFluentAPI = new JavaGenerator(
      new NaiveLinker("il.ac.technion.cs.fling.examples.generated", "AeqB"),
      "il.ac.technion.cs.fling.examples.generated", "AeqB") //
          .go(new ReliableAPICompiler(dpda).makeModel());
  /** C++ fluent API supporting method chains of the form
   * <code>a()->b().a().b()...$();</code> */
  public static final String CppFluentAPI = new CPPGenerator(new NaiveLinker("AeqB")) //
      .go(new ReliableAPICompiler(dpda).makeModel());
  /** SML fluent API */
  public static final String SMLFluentAPI = new SMLGenerator(new NaiveLinker("AeqB"), "zzz") //
      .go(new ReliableAPICompiler(dpda).makeModel());
  private final NaiveLinker namer2 = new NaiveLinker("AeqB");
  @Test public void test1() {
    final Model m = new ReliableAPICompiler(dpda).makeModel();
    new JavaGenerator(namer, "il.ac.technion.cs.fling.examples.generated", "AeqB").go(m);
    new CPPGenerator(namer2).go(m);
    new CSharpGenerator(namer2).go(m);
    new SMLGenerator(namer2, "zzz").go(m);
  }
  @Test public void test2() {
    final Model m = new PolynomialAPICompiler(dpda).makeModel();
    new JavaGenerator(namer, "il.ac.technion.cs.fling.examples.generated", "AeqB").go(m);
    new CPPGenerator(namer2).go(m);
    new SMLGenerator(namer2, "zzz").go(m);
    new CSharpGenerator(namer2).go(m);
  }
  @Test public void test3() {
    final Model m = new PolynomialAPICompiler(dpda).makeModel();
    new CPPGenerator(namer2).go(m);
  }
  /** Print C++ program to standard output. */
  public static void main(final String[] args) {
    System.out.println(CppFluentAPI);
    // System.out.println(SMLFluentAPI);
  }
}
