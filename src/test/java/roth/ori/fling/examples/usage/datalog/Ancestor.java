package roth.ori.fling.examples.usage.datalog;

import static roth.ori.fling.junk.Datalog.*;
import static roth.ori.fling.examples.Datalog.*;

import roth.ori.fling.junk.DatalogAST.Program;

public class Ancestor {
  public static Program program() {
    return fact("parent").of("john", "bob") //
        .fact("parent").of("bob", "donald") //
        .infer("ancestor").of(v("A"), v("B")) //
        /**/.when("parent").of(v("A"), v("B")) //
        .infer("ancestor").of(v("A"), v("B")) //
        /**/.when("parent").of(v("A"), v("C")) //
        /**/.and("ancestor").of(v("C"), v("B")) //
        .query("ancestor").of(l("john"), v("X")) //
        .$();
  }
  public static void main(String[] args) {
    RunDatalogProgram.run(program());
  }
}
