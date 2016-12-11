package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.SmallPascal.NT.*;
import static org.spartan.fajita.api.examples.SmallPascal.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SmallPascal {
  static enum Term implements Terminal {
    program, begin, end, label, //
    constant, id, procedure, semi, //
    pair;
  }

  static enum NT implements NonTerminal {
    Program, Labels, Label, MoreLabels
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(Program) //
        .derive(Program).to(program).and(Labels).and(end)//
        .derive(Labels).to(label).and(Label).and(MoreLabels) //
        /*           */.orNone() //
        .derive(Label).to(semi) //
        .derive(MoreLabels).to(Label).and(MoreLabels) //
        /*           */.orNone() //
        .go();
    return bnf;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
//  public static void legal(augS_0_2dc x) {
//    x.program().label().semi().end().$();
//    x.program().label().semi().semi().end().$();
//    x.program().label().semi().semi().semi().end().$();
//    x.program().label().semi().semi().semi().semi().end().$();
//    x.program().label().semi().semi().semi().semi().semi().end().$();
//    x.program().label().semi().semi().semi().semi().semi().semi().end().$();
//    x.program().label().semi().semi().semi().semi().semi().semi().semi().end().$();
//  }
}
