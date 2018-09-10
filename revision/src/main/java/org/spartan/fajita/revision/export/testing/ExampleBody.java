package org.spartan.fajita.revision.export.testing;

import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

@SuppressWarnings("all") public class ExampleBody {
  public static ExampleBody_2 call(Terminal arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(FajitaTesting.Term.call, arg0);
    return $$$;
  }
  public static ExampleBody1_1<ExampleBody_2> toConclude(NonTerminal arg0) {
    $$$ $$$ = new $$$();
    $$$.recordTerminal(FajitaTesting.Term.toConclude, arg0);
    return $$$;
  }

  public interface ExampleBody_2 {
    ExampleBody_3 with(Object... arg0);
  }

  public interface ExampleBody_3 extends ASTNode {
    ExampleBodyNext_1<ExampleBodyNext_1<ExampleBodyNext_1_rec_8f>> then(Terminal arg0);
  }

  public interface ExampleBody1_1<call> {
    call call(Terminal arg0);
  }

  public interface ExampleBodyNext_1<then> {
    ExampleBodyNext_2<then> with(Object... arg0);
  }

  public interface ExampleBodyNext_2<then> extends ASTNode {
    then then(Terminal arg0);
  }

  public interface ExampleBodyNext_1_rec_8f {
    ExampleBodyNext_2<ExampleBodyNext_1_rec_8f> with(Object... arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder
      implements ExampleBody_2, ExampleBody_3, ExampleBody1_1, ExampleBodyNext_1, ExampleBodyNext_2, ExampleBodyNext_1_rec_8f {
    $$$() {
      super(new FajitaTesting().bnf().ebnf().makeSubBNF(
          FajitaTesting.NT.ExampleBody), "org.spartan.fajita.revision.export.testing");
    }
    public $$$ with(Object... arg0) {
      recordTerminal(FajitaTesting.Term.with, arg0);
      return this;
    }
    public $$$ then(Terminal arg0) {
      recordTerminal(FajitaTesting.Term.then, arg0);
      return this;
    }
    public $$$ call(Terminal arg0) {
      recordTerminal(FajitaTesting.Term.call, arg0);
      return this;
    }
  }
}
