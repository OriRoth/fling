package roth.ori.fling.examples;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.option;
import static roth.ori.fling.examples.FlingTesting.NT.*;
import static roth.ori.fling.examples.FlingTesting.Term.*;

import java.io.IOException;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

public class FlingTesting extends Grammar {
  public enum Term implements Terminal {
    example, malexample, call, with, toConclude, then
  }

  public enum NT implements NonTerminal {
    Test, ExampleKind, Example, MalExample, ExampleBody, ExampleBodyNext
  }

  @Override public FlingBNF bnf() {
    return Fling.build(getClass(), Term.class, NT.class, "FlingTesting", Main.packagePath, Main.projectPath) //
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
    new FlingTesting().generateGrammarFiles();
  }
}
