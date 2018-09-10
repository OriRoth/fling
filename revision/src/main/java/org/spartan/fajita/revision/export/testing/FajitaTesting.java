package org.spartan.fajita.revision.export.testing;

import static org.spartan.fajita.revision.api.Fajita.attribute;
import static org.spartan.fajita.revision.api.Fajita.noneOrMore;
import static org.spartan.fajita.revision.api.Fajita.oneOrMore;
import static org.spartan.fajita.revision.api.Fajita.option;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.Example;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.ExampleBody;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.ExampleBodyNext;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.ExampleKind;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.MalExample;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.NT.Test;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.call;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.example;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.malexample;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.then;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.toConclude;
import static org.spartan.fajita.revision.export.testing.FajitaTesting.Term.with;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.api.Fajita.FajitaBNF;
import org.spartan.fajita.revision.api.Main;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

@SuppressWarnings("all") public class FajitaTesting extends Grammar {
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
    FajitaTestingAST.Test $();
  }

  public interface MalExample_1<example, malexample> extends ASTNode {
    example example(ASTNode arg0);
    malexample malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
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
    FajitaTestingAST.Test $();
  }

  public interface Example_1_rec_3a4 {
    Example_1_rec_3a4 example(ASTNode arg0);
    MalExample_1_rec_167 malexample(ASTNode arg0);
    FajitaTestingAST.Test $();
  }

  public interface ExampleBodyNext_1_rec_8f {
    ExampleBodyNext_2<ExampleBodyNext_1_rec_8f> with(Object... arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements Example_1, MalExample_1, ExampleBody1_1, ExampleBody_2,
      ExampleBody_3, ExampleBodyNext_1, ExampleBodyNext_2, MalExample_1_rec_167, Example_1_rec_3a4, ExampleBodyNext_1_rec_8f {
    $$$() {
      super(new FajitaTesting().bnf().ebnf(), "org.spartan.fajita.revision.export.testing");
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
    public FajitaTestingAST.Test $() {
      return ast("FajitaTestingAST");
    }
  }
}
