package fling.examples.programs;

import static fling.examples.generated.ExtendedBalancedParentheses.c;

public class ExtendedBalancedParentheses {
  public static void compilationTest() {
    c().ↄ().$();
    // c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().c().ↄ().Ↄ().c().ↄ().$();
    c().c().c().ↄ().Ↄ().c();
    c().c().c().ↄ().Ↄ().c().Ↄ().$();
  }
}
