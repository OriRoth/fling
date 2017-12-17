package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.examples.Parenthesis.NT.C;
import static org.spartan.fajita.revision.examples.Parenthesis.NT.O;
import static org.spartan.fajita.revision.examples.Parenthesis.NT.S;
import static org.spartan.fajita.revision.examples.Parenthesis.Term.pop;
import static org.spartan.fajita.revision.examples.Parenthesis.Term.push;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

public class Parenthesis extends Grammar {
  public static enum Term implements Terminal {
    push, pop
  }

  public static enum NT implements NonTerminal {
    S, O, C
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(Parenthesis.class, Term.class, NT.class, "Parenthesis", Main.packagePath, Main.projectPath).start(S) //
        .derive(S).to(O, S, C, S).orNone() //
        .derive(O).to(push) //
        .derive(C).to(pop);
  }
  public static void main(String[] args) throws IOException {
    new Parenthesis().generateGrammarFiles();
  }
  public static void testing() {
    // push().pop();
    // push().push().pop().push().pop().pop();
  }
}
