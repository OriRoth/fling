package org.spartan.fajita.revision.examples.usage;

import static org.spartan.fajita.revision.junk.Datalog.*;
import static org.spartan.fajita.revision.junk.FactExpression.*;

import org.spartan.fajita.revision.junk.DatalogAST.Program;

public class Ancestor {
  public static Program program() {
    return fact(that("parent").by("john", "bob")) //
        .fact(that("parent").by("bob", "donald")) //
        .rule(that("ancestor").by("A", "B")) //
        /**/.is("parent").by("A", "B") //
        .rule(that("ancestor").by("A", "B")) //
        /**/.is("parent").by("A", "C") //
        /**/.and("ancestor").by("C", "B") //
        .query("ancestor").by("john", "donald") //
        .$();
  }
  public static void main(String[] args) {
    program();
  }
}
