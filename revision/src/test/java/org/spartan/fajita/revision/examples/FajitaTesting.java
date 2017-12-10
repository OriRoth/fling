package org.spartan.fajita.revision.examples;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.Example;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.ExampleKind;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.Invocation;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.MalExample;
import static org.spartan.fajita.revision.examples.FajitaTesting.NT.Test;
import static org.spartan.fajita.revision.examples.FajitaTesting.Term.call;
import static org.spartan.fajita.revision.examples.FajitaTesting.Term.example;
import static org.spartan.fajita.revision.examples.FajitaTesting.Term.malexample;
import static org.spartan.fajita.revision.examples.FajitaTesting.Term.with;

import java.io.IOException;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class FajitaTesting extends Grammar {
  public enum Term implements Terminal {
    example, malexample, call, with
  }

  public enum NT implements NonTerminal {
    Test, ExampleKind, Example, MalExample, Invocation
  }

  @Override public FajitaBNF bnf() {
    return Fajita.build(getClass(), Term.class, NT.class, "FajitaTesting", Main.packagePath, Main.projectPath) //
        .start(Test, Invocation) //
        .derive(Test).to(oneOrMore(ExampleKind)) //
        .specialize(ExampleKind).into(Example, MalExample) //
        .derive(Example).to(attribute(example, new VarArgs(Object.class))) //
        .derive(MalExample).to(attribute(malexample, new VarArgs(Object.class))) //
        .derive(Invocation).to(attribute(call, Terminal.class), attribute(with, new VarArgs(Object.class)));
  }
  public static void main(String[] args) throws IOException {
    new FajitaTesting().generateGrammarFiles();
  }
}
