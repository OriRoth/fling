package il.ac.technion.cs.fling.examples.automata;

import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Q.q0;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Q.q1;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Γ.g0;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Γ.g1;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Σ.a;
import static il.ac.technion.cs.fling.examples.automata.LongFall.Σ.b;

import il.ac.technion.cs.fling.DPDA;
import il.ac.technion.cs.fling.adapters.CPPGenerator;
import il.ac.technion.cs.fling.adapters.JavaGenerator;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.namers.NaiveNamer;

public class LongFall {
  enum Q implements Named {
    q0, q1
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    g0, g1
  }

  public static final DPDA<Named, Token, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(g0, g1) //
      .δ(q0, a, g1, q0, g1, g1) //
      .δ(q0, b, g1, q1) //
      .δ(q1, ε(), g1, q1) //
      .δ(q1, ε(), g0, q0, g0) //
      .go());
  public static final String JavaFluentAPI = new JavaGenerator("il.ac.technion.cs.fling.examples.generated", "LongFall",
      "$", new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "LongFall")) //
          .renderCompilationUnit(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CPPGenerator(new NaiveNamer("LongFall")) //
      .renderCompilationUnit(new ReliableAPICompiler(dpda).compileFluentAPI());
}
