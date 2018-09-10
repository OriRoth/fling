package roth.ori.fling.examples;

import static roth.ori.fling.api.Fajita.attribute;
import static roth.ori.fling.api.Fajita.oneOrMore;
import static roth.ori.fling.api.Fajita.noneOrMore;
import static roth.ori.fling.api.Fajita.option;
import static roth.ori.fling.examples.FajitaTesting.NT.*;
import static roth.ori.fling.examples.FajitaTesting.Term.*;

import java.io.IOException;

import roth.ori.fling.api.Fajita;
import roth.ori.fling.api.Fajita.FajitaBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

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
