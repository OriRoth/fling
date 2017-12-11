package org.spartan.fajita.revision.export.testing;

public class FajitaTestingAST implements org.spartan.fajita.revision.export.AST {
  public static class ExampleKind {
  }

  public static class ExampleBody {
    public java.util.Optional<org.spartan.fajita.revision.symbols.NonTerminal> examplebody1;
    public org.spartan.fajita.revision.symbols.Terminal call;
    public java.lang.Object[] with;
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext[] examplebody2;

    public ExampleBody(java.util.Optional<org.spartan.fajita.revision.symbols.NonTerminal> examplebody1,
        org.spartan.fajita.revision.symbols.Terminal call, java.lang.Object[] with,
        org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext[] examplebody2) {
      this.examplebody1 = examplebody1;
      this.call = call;
      this.with = with;
      this.examplebody2 = examplebody2;
    }
  }

  public static class ExampleBodyNext {
    public org.spartan.fajita.revision.symbols.Terminal then;
    public java.lang.Object[] with;

    public ExampleBodyNext(org.spartan.fajita.revision.symbols.Terminal then, java.lang.Object[] with) {
      this.then = then;
      this.with = with;
    }
  }

  public static class Test {
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind[] test1;

    public Test(org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind[] test1) {
      this.test1 = test1;
    }
  }

  public static class Example extends org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind {
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody example;

    public Example(org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody example) {
      this.example = example;
    }
  }

  public static class MalExample extends org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind {
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody malexample;

    public MalExample(org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody malexample) {
      this.malexample = malexample;
    }
  }
}