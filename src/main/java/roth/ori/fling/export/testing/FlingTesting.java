package roth.ori.fling.export.testing;

import static roth.ori.fling.api.Fling.attribute;
import static roth.ori.fling.api.Fling.noneOrMore;
import static roth.ori.fling.api.Fling.oneOrMore;
import static roth.ori.fling.api.Fling.option;
import static roth.ori.fling.export.testing.FlingTesting.NT.Example;
import static roth.ori.fling.export.testing.FlingTesting.NT.ExampleBody;
import static roth.ori.fling.export.testing.FlingTesting.NT.ExampleBodyNext;
import static roth.ori.fling.export.testing.FlingTesting.NT.ExampleKind;
import static roth.ori.fling.export.testing.FlingTesting.NT.MalExample;
import static roth.ori.fling.export.testing.FlingTesting.NT.Test;
import static roth.ori.fling.export.testing.FlingTesting.Term.call;
import static roth.ori.fling.export.testing.FlingTesting.Term.example;
import static roth.ori.fling.export.testing.FlingTesting.Term.malexample;
import static roth.ori.fling.export.testing.FlingTesting.Term.then;
import static roth.ori.fling.export.testing.FlingTesting.Term.toConclude;
import static roth.ori.fling.export.testing.FlingTesting.Term.with;

import roth.ori.fling.api.Fling;
import roth.ori.fling.api.Fling.FlingBNF;
import roth.ori.fling.api.Main;
import roth.ori.fling.export.ASTNode;
import roth.ori.fling.export.FluentAPIRecorder;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.types.VarArgs;

@SuppressWarnings("all") public class FlingTesting extends Grammar {
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
  public static Example_1<Example_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>, MalExample_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>> example(
      ASTNode arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(Term.example, arg0);
    return $$$;
  }
  public static MalExample_1<Example_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>, MalExample_1<Example_1<Example_1_rec_3a4, MalExample_1_rec_167>, MalExample_1<Example_1_rec_3a4, MalExample_1_rec_167>>> malexample(
      ASTNode arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(Term.malexample, arg0);
    return $$$;
  }

  public interface Example_1<example, malexample> extends ASTNode {
    example example(ASTNode arg0);
    malexample malexample(ASTNode arg0);
    FlingTestingAST.Test $();
  }

  public interface MalExample_1<example, malexample> extends ASTNode {
    example example(ASTNode arg0);
    malexample malexample(ASTNode arg0);
    FlingTestingAST.Test $();
  }

  public interface ExampleBody1_1<call> {
    call call(Terminal arg0);
  }

  public interface ExampleBody_2 {
    ExampleBody_3 with(Object... arg0);
  }

  public interface ExampleBody_3 {
    ExampleBodyNext_1<ExampleBodyNext_1<ExampleBodyNext_1_rec_8f>> then(Terminal arg0);
  }

  public interface ExampleBodyNext_1<then> {
    ExampleBodyNext_2<then> with(Object... arg0);
  }

  public interface ExampleBodyNext_2<then> {
    then then(Terminal arg0);
  }

  public interface MalExample_1_rec_167 {
    Example_1_rec_3a4 example(ASTNode arg0);
    MalExample_1_rec_167 malexample(ASTNode arg0);
    FlingTestingAST.Test $();
  }

  public interface Example_1_rec_3a4 {
    Example_1_rec_3a4 example(ASTNode arg0);
    MalExample_1_rec_167 malexample(ASTNode arg0);
    FlingTestingAST.Test $();
  }

  public interface ExampleBodyNext_1_rec_8f {
    ExampleBodyNext_2<ExampleBodyNext_1_rec_8f> with(Object... arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements Example_1, MalExample_1, ExampleBody1_1, ExampleBody_2,
      ExampleBody_3, ExampleBodyNext_1, ExampleBodyNext_2, MalExample_1_rec_167, Example_1_rec_3a4, ExampleBodyNext_1_rec_8f {
    $$$() {
      super(new FlingTesting().bnf().ebnf(), "roth.ori.fling.export.testing");
    }
    public $$$ example(ASTNode arg0) {
      recordTerminal(Term.example, arg0);
      return this;
    }
    public $$$ malexample(ASTNode arg0) {
      recordTerminal(Term.malexample, arg0);
      return this;
    }
    public $$$ call(Terminal arg0) {
      recordTerminal(Term.call, arg0);
      return this;
    }
    public $$$ with(Object... arg0) {
      recordTerminal(Term.with, arg0);
      return this;
    }
    public $$$ then(Terminal arg0) {
      recordTerminal(Term.then, arg0);
      return this;
    }
    public FlingTestingAST.Test $() {
      return ast("FlingTestingAST");
    }
  }
}
