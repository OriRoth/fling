package org.spartan.fajita.revision.motivation;

@SuppressWarnings("all") public class AB { // A⁺B⁺
  public static class Q0 {
    public Q1 a() { return new Q1(); }
  }

  public static class Q1 {
    public Q1 a() { return new Q1(); }
    public Q2 b() { return new Q2(); }
  }

  public static class Q2 {
    public Q3 b() { return new Q3(); }
  }

  public static class Q3 {
    public Q3 b() { return new Q3(); }
    public void done() { /**/ }
  }

  public static void main(String[] args) {
    Q0 $ = new Q0();
  }
}
