package roth.ori.fling.examples.usage;

import static roth.ori.fling.junk.Exp2.*;

public class Exp2 {
  public static void main(String[] args) {
    System.out.println(a().a().b().c().b().c());
    System.out.println( //
    // a().a().a().a().a().a().a().a().a().a().a().a().a() //
    // .b().b().b().b().b().b().b().b().b().b().b().b().b() //
    // .c().c().c().c().c().c().c().c().c().c().c().c().c() //
    );
  }
}
