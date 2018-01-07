package org.spartan.fajita.revision.examples.usage;

import static org.spartan.fajita.revision.junk.EBNF.derive;
import static org.spartan.fajita.revision.junk.Literal.attribute;
import static org.spartan.fajita.revision.junk.Literal.either;
import static org.spartan.fajita.revision.junk.Literal.oneOrMore;
import static org.spartan.fajita.revision.junk.Literal.option;

import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.types.VarArgs;

public class EBNF {
  public static void testing() {
    derive("EBNF").to(oneOrMore("Rule")) //
        .derive("Rule").to(attribute("derive", NonTerminal.class).and("To").and(option("ClauseTail"))) //
        .derive("To").to(attribute("to", "Literal")).or(attribute("to", String.class)) //
        .derive("ClauseTail").to(oneOrMore(either("and", "or").and("Literal"))) //
        .derive("Literal").to(attribute("oneOrMore", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("oneOrMore", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("noneOrMore", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("noneOrMore", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("option", "Literal").and(option("ClauseTail"))) //
        /**/.or(attribute("option", String.class).and(option("ClauseTail"))) //
        /**/.or(attribute("attribute", Terminal.class, new VarArgs(Object.class)).and(option("ClauseTail"))) //
        .$();
  }
}
