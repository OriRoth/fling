package org.spartan.fajita.revision.export;

import java.lang.Object;
import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.symbols.Terminal;

@SuppressWarnings("all") public class FajitaTesting {
  public static MalExample_1<Example_1<Example_1<Example_1_rec_331, MalExample_1_rec_3c0>, MalExample_1<Example_1_rec_331, MalExample_1_rec_3c0>>, MalExample_1<Example_1<Example_1_rec_331, MalExample_1_rec_3c0>, MalExample_1<Example_1_rec_331, MalExample_1_rec_3c0>>> malexample(
      Object... arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.malexample, arg0);
    return $$$;
  }
  public static Example_1<Example_1<Example_1<Example_1_rec_331, MalExample_1_rec_3c0>, MalExample_1<Example_1_rec_331, MalExample_1_rec_3c0>>, MalExample_1<Example_1<Example_1_rec_331, MalExample_1_rec_3c0>, MalExample_1<Example_1_rec_331, MalExample_1_rec_3c0>>> example(
      Object... arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.example, arg0);
    return $$$;
  }
  public static Invocation_1 call(Terminal arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.call, arg0);
    return $$$;
  }

  public interface Example_1<example, malexample> extends ASTNode {
    example example(Object... arg0);
    malexample malexample(Object... arg0);
    FajitaTestingAST.Test $();
  }

  public interface MalExample_1<example, malexample> extends ASTNode {
    example example(Object... arg0);
    malexample malexample(Object... arg0);
    FajitaTestingAST.Test $();
  }

  public interface Invocation_1 {
    Invocation_2 with(Object... arg0);
  }

  public interface Invocation_2 extends ASTNode {
    FajitaTestingAST.Test $();
  }

  public interface MalExample_1_rec_3c0 {
    Example_1_rec_331 example(Object... arg0);
    MalExample_1_rec_3c0 malexample(Object... arg0);
    FajitaTestingAST.Test $();
  }

  public interface Example_1_rec_331 {
    Example_1_rec_331 example(Object... arg0);
    MalExample_1_rec_3c0 malexample(Object... arg0);
    FajitaTestingAST.Test $();
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder
      implements Example_1, MalExample_1, Invocation_1, Invocation_2, MalExample_1_rec_3c0, Example_1_rec_331 {
    $$$() {
      super(new org.spartan.fajita.revision.examples.FajitaTesting().bnf().ebnf(), "org.spartan.fajita.revision.export");
    }
    public $$$ example(Object... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.example, arg0);
      return this;
    }
    public $$$ malexample(Object... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.malexample, arg0);
      return this;
    }
    public $$$ with(Object... arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.FajitaTesting.Term.with, arg0);
      return this;
    }
    public FajitaTestingAST.Test $() {
      return ast("FajitaTestingAST");
    }
  }
}
