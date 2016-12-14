package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.BadJump.NT.*;
import static org.spartan.fajita.api.examples.BadJump.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

class BadJump {
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }

  static class ERROR {
    //
  }

  public abstract static class S_2_73467bb2 {
    public abstract int $();
  }

  public abstract static class A_2_1efe7ee3<bad_jump> {
    public abstract bad_jump bad_jump();
  }

  public abstract static class B_1_2d98e0de<bad_jump, exit> {
    public abstract bad_jump bad_jump();
    public abstract exit exit();
  }

  public abstract static class S_1_36438d7e {
    public abstract B_1_2d98e0de<S_3_36438d7e, ERROR> a();
  }

  public abstract static class S_3_36438d7e {
    public abstract int $();
  }

  public abstract static class augS_0_1e04e768 {
    public abstract S_1_36438d7e some();
    public abstract B_1_2d98e0de<S_2_73467bb2, A_2_1efe7ee3<S_2_73467bb2>> a();
  }

  @SuppressWarnings({ "hiding", "unused" }) static void test(augS_0_1e04e768 x) {
    B_1_2d98e0de<S_2_73467bb2, A_2_1efe7ee3<S_2_73467bb2>> a = x.a();
    x.a().bad_jump().$();
    /* Since bad_jump is in Follow(B) and we have jump information to verb
     * bad_jump (because bad_jump is by chance in Follow(A)) the RLLp recognizes
     * it can perform a jump operation with verb bad_jump, this is wrong! */
  }

  static enum Term implements Terminal {
    a, bad_jump, exit, some
  }

  static enum NT implements NonTerminal {
    S, A, B;
  }

  public static BNF buildBNF() {
    BNF bnf = new Fajita(Term.class, NT.class) //
        .start(S) //
        .derive(S).to(A).and(bad_jump) //
        .derive(A).to(B).and(exit)//
        .derive(B).to(a) //
        /*                                                    */
        /* adding bad_jump to Follow(B) */
        /*                                                    */
        .derive(S).to(some).and(B).and(bad_jump) //
        /*                                                    */
        /*                                                    */
        .go();
    return bnf;
  }
}
