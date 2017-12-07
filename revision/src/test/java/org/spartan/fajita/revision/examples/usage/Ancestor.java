package org.spartan.fajita.revision.examples.usage;

import static org.spartan.fajita.revision.junk.Body1.literal;
import static org.spartan.fajita.revision.junk.Datalog.fact;
import static org.spartan.fajita.revision.junk.Literal.name;

import org.spartan.fajita.revision.junk.DatalogAST.Program;

public class Ancestor {
  public static Program program() {
    return fact(name("parent").terms("john", "bob")) //
        .fact(name("parent").terms("bob", "donald")) //
        .head(name("ancestor").terms("A", "B")) //
        .body( //
            literal(name("parent").terms("A", "B")) //
        ) //
        .head(name("ancestor").terms("A", "B") //
        ).body( //
            literal(name("parent").terms("A", "C")) //
                .literal(name("ancestor").terms("C", "B")) //
        ) //
        .$();
  }
}
