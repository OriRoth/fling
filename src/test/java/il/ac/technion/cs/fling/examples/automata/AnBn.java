package il.ac.technion.cs.fling.examples.automata;

import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Q.q0;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Q.q1;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Q.q2;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Γ.E;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Γ.X;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Σ.a;
import static il.ac.technion.cs.fling.examples.automata.AnBn.Σ.b;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class AnBn {
  enum Q implements Named {
    q0, q1, q2
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    E, X
  }

  public static final DPDA<Named, Token, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go());
  public static final String JavaFluentAPI = new JavaGenerator("il.ac.technion.cs.fling.examples.generated", "AnBn",
      "$", new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "AnBn")) //
          .renderCompilationUnit(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CPPGenerator(new NaiveNamer("AnBn")) //
      .renderCompilationUnit(new ReliableAPICompiler(dpda).compileFluentAPI());
}
