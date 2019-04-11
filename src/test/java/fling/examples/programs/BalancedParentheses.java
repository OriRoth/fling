package fling.examples.programs;

import static fling.examples.generated.BalancedParentheses.c;

public class BalancedParentheses {
  public static void compilationTest() {
    c().ↄ().$();
    // c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().ↄ().ↄ().c().ↄ().$();
    c().c().ↄ().ↄ().c();
    // ↄ();
  }
}
