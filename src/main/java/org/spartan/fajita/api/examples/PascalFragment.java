package org.spartan.fajita.api.examples;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import static org.spartan.fajita.api.examples.PascalFragment.NT.*;
import static org.spartan.fajita.api.examples.PascalFragment.Term.*;
 
public class PascalFragment {
  static enum Term implements Terminal {
    program, begin, end, label, //
    constant, id, procedure, semi, //
    pair;
  }

  static enum NT implements NonTerminal {
    Program, Parameters, Definitions, //
    Body, Labels, Constants, Label, //
    Constant, MoreLabels, MoreConstants, //
    Nested, Procedure;
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(Term.class, NT.class) //
        .start(Program) //
        .derive(Program).to(program).and(id).and(Parameters).and(semi).and(Definitions).and(Body) //
        .derive(Body).to(begin).and(end) //
        .derive(Definitions).to(Labels).and(Constants).and(Nested) //
        .derive(Labels).to(label).and(Label).and(MoreLabels) //
        /*           */.orNone() //
        .derive(Constants).to(constant).and(Constant).and(MoreConstants) //
        /*           */.orNone() //
        .derive(Label).to(semi) //
        .derive(Constant).to(semi)//
        .derive(MoreLabels).to(Label).and(MoreLabels) //
        /*           */.orNone() //
        .derive(MoreConstants).to(Constant).and(MoreConstants) //
        /*           */.orNone() //
        .derive(Nested).to(Procedure).and(Nested) //
        /*           */.orNone() //
        .derive(Procedure).to(procedure).and(id).and(Parameters).and(semi).and(Definitions).and(Body)//
        .derive(Parameters).to(pair) //
        /*           */.orNone() //
        .go();
    return bnf;
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(buildBNF());
  }
}
