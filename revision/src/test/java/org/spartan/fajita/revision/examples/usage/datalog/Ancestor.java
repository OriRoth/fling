package org.spartan.fajita.revision.examples.usage.datalog;

import static org.spartan.fajita.revision.junk.Datalog.*;

import org.spartan.fajita.revision.junk.DatalogAST.Program;

public class Ancestor {
  public static Program program() {
    return fact("parent").by("john", "bob") //
        .fact("parent").by("bob", "donald") //
        .rule("ancestor").by("A", "B") //
        /**/.is("parent").by("A", "B") //
        .rule("ancestor").by("A", "B") //
        /**/.is("parent").by("A", "C") //
        /**/.is("ancestor").by("C", "B") //
        .query("ancestor").by("john", "X") //
        .$();
  }
  public static void main(String[] args) {
    RunDatalogProgram.run(program());
  }
}
