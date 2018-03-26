package org.spartan.fajita.revision.motivation;

@SuppressWarnings("all") public class ABBA {
  public static class Q0 {
    public Q1 a() {
      return new Q1();
    }
    public Q2 b() {
      return new Q2();
    }
  }

  public static class Q1 {
    public Q0 b() {
      return new Q0();
    }
  }

  public static class Q2 {
    public Q0 a() {
      return new Q0();
    }
  }
  
  public static void main(String[] args) {
    Q0 $ = new Q0();
  }
}
