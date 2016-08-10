package org.spartan.fajita.api.junk;

class Simple {
  /**
   * <pre>
   * return new BNFBuilder(Term.class, NT.class) //
   *     .start(S) //
   *     .derive(S).to(A) //
   *     .derive(A).to(B).or(a) //
   *     .derive(B).to(b) //
   *     .go();
   * </pre>
   */
  public abstract static class S_0_1321776379 {
    public abstract A_1_1534058999 a();
    public abstract B_1_664251737 b();
  }

  public abstract static class S_1_1321776379 {
    public abstract int $();
  }

  public abstract static class A_0_1534058999 {
    public abstract A_1_1534058999 a();
  }

  public abstract static class A_1_1534058999 {
    public abstract int $();
  }

  public abstract static class A_1_1888627496 {
    public abstract int $();
  }

  public abstract static class A_0_1888627496 {
    public abstract B_1_664251737 b();
  }

  public abstract static class B_0_664251737 {
    public abstract B_1_664251737 b();
  }

  public abstract static class B_1_664251737 {
    public abstract int $();
  }

  public static void main(String[] args) {
    S_0_1321776379 x = null;
    x.a().$();
    x.b().$();
  }
}
