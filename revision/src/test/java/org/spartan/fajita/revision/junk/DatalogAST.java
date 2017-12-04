package org.spartan.fajita.revision.junk;

public class DatalogAST {
  public static class RULE {
  }

  public static class LITERAL {
    java.lang.String name;
    java.lang.String[] terms;
  }

  public static class BODY {
    org.spartan.fajita.revision.junk.DatalogAST.LITERAL[] body;
  }

  public static class S {
    org.spartan.fajita.revision.junk.DatalogAST.RULE[] s1;
  }

  public static class RULE2 extends RULE {
    org.spartan.fajita.revision.junk.DatalogAST.LITERAL head;
    org.spartan.fajita.revision.junk.DatalogAST.BODY body;
  }

  public static class RULE1 extends RULE {
    org.spartan.fajita.revision.junk.DatalogAST.LITERAL fact;
  }
}