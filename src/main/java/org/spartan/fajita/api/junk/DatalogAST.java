package org.spartan.fajita.api.junk;

public class DatalogAST {
  public static class RULE {
  }

  public static class LITERAL {
    java.lang.String name;
    java.lang.String[] terms;
  }

  public static class BODY {
    LITERAL[] body;
  }

  public static class S {
    RULE[] s1;
  }

  public static class RULE$2 extends RULE {
    LITERAL head;
    BODY body;
  }

  public static class RULE$1 extends RULE {
    LITERAL fact;
  }
}
