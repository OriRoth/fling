package il.ac.technion.cs.fling.examples.automata;
import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Q.*;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Γ.*;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Σ.*;
import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.internal.compiler.api.dom.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar._.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
public enum AnBn {
  ;
  enum Q implements Named {
    q0, q1, q2
  }
  enum Σ implements Terminal {
    a, b
  }
  enum Γ implements Named {
    E, X
  }
  private static final DPDA<Named, Token, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go());
  public static final String JavaFluentAPI = new JavaGenerator("il.ac.technion.cs.fling.examples.generated", "AnBn") //
      .go(new ReliableAPICompiler(dpda).go());
  public static final String CppFluentAPI = new CPPGenerator(
      new il.ac.technion.cs.fling.internal.compiler.Linker("AnBn")) //
          .go(new ReliableAPICompiler(dpda).go());
}
