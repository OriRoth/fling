package org.spartan.fajita.revision.export.testing;

public class FajitaTestingAST implements org.spartan.fajita.revision.export.AST {
  public static class ExampleBodyNext {
  }

  public static class ExampleKind {
  }

  public static class ExampleBody {
    public java.util.Optional<org.spartan.fajita.revision.symbols.NonTerminal> examplebody1;
    public org.spartan.fajita.revision.symbols.Terminal call;
    public java.lang.Object[] with;
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext examplebodynext;

    public ExampleBody(java.util.Optional<org.spartan.fajita.revision.symbols.NonTerminal> examplebody1,
        org.spartan.fajita.revision.symbols.Terminal call, java.lang.Object[] with,
        org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext examplebodynext) {
      this.examplebody1 = examplebody1;
      this.call = call;
      this.with = with;
      this.examplebodynext = examplebodynext;
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

  public static class ExampleBodyNext1 extends org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext {
    public ExampleBodyNext1() {
    }
  }

  public static class ExampleBodyNext2 extends org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext {
    public org.spartan.fajita.revision.symbols.Terminal then;
    public java.lang.Object[] with;
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext examplebodynext;

    public ExampleBodyNext2(org.spartan.fajita.revision.symbols.Terminal then, java.lang.Object[] with,
        org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBodyNext examplebodynext) {
      this.then = then;
      this.with = with;
      this.examplebodynext = examplebodynext;
    }
  }

  public static class MalExample extends org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleKind {
    public org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody malexample;

    public MalExample(org.spartan.fajita.revision.export.testing.FajitaTestingAST.ExampleBody malexample) {
      this.malexample = malexample;
    }
  }
}