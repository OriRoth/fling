package roth.ori.fling.examples.usage.datalog;

import static roth.ori.fling.junk.Datalog.*;

import roth.ori.fling.junk.DatalogAST.Program;

public class Ancestor {
  public static Program program() {
    return fact("parent").of("john", "bob") //
        .fact("parent").of("bob", "donald") //
        .infer("ancestor").of("A", "B") //
        /**/.when("parent").of("A", "B") //
        .infer("ancestor").of("A", "B") //
        /**/.when("parent").of("A", "C") //
        /**/.and("ancestor").of("C", "B") //
        .query("ancestor").of("john", "X") //
        .$();
  }
  public static void main(String[] args) {
    RunDatalogProgram.run(program());
  }
}
