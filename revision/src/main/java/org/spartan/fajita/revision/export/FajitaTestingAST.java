package org.spartan.fajita.revision.export;

public class FajitaTestingAST implements org.spartan.fajita.revision.export.AST {
  public static class Test {
    public org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind[] test1;

    public Test(org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind[] test1) {
      this.test1 = test1;
    }
  }

  public static class Invocation {
    public org.spartan.fajita.revision.symbols.Terminal call;
    public java.lang.Object[] with;

    public Invocation(org.spartan.fajita.revision.symbols.Terminal call, java.lang.Object[] with) {
      this.call = call;
      this.with = with;
    }
  }

  public static class ExampleKind {
  }

  public static class Example extends org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind {
    public java.lang.Object[] example;

    public Example(java.lang.Object[] example) {
      this.example = example;
    }
  }

  public static class MalExample extends org.spartan.fajita.revision.export.FajitaTestingAST.ExampleKind {
    public java.lang.Object[] malexample;

    public MalExample(java.lang.Object[] malexample) {
      this.malexample = malexample;
    }
  }
}