package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.noneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.*;
import static org.spartan.fajita.revision.examples.FajitaTesting.Term.*;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class FajitaTesting extends Grammar {
  public enum Term implements Terminal {
    example, malexample, call, with, toConclude, then
  }

  public enum NT implements NonTerminal {
    Test, ExampleKind, Example, MalExample, ExampleBody, ExampleBodyNext
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "FajitaTesting", Main.packagePath, Main.projectPath) //
        .start(Test) //
        .derive(Test).to(oneOrMore(ExampleKind)) //
        .specialize(ExampleKind).into(Example, MalExample) //
        .derive(Example).to(attribute(example, ExampleBody)) //
        .derive(MalExample).to(attribute(malexample, ExampleBody)) //
        .derive(ExampleBody).to(option(attribute(toConclude, NonTerminal.class)), //
            attribute(call, Terminal.class), //
            attribute(with, new VarArgs(Object.class)), //
            noneOrMore(ExampleBodyNext)) //
        .derive(ExampleBodyNext).to(attribute(then, Terminal.class), //
            attribute(with, new VarArgs(Object.class)));
  }
  public static void main(String[] args) throws IOException {
    new FajitaTesting().generateGrammarFiles();
  }
}
