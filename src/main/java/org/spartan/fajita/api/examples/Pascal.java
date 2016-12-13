package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Pascal.NT.*;
import static org.spartan.fajita.api.examples.Pascal.Term.*;

import java.io.IOException;

import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Pascal {
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
    BNF bnf = BNFBuilder.buildBNF(Term.class, NT.class) //
        .setApiName("Pascal") //
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
  // public static void legal(augS_0_8a x) {
  // x.program().id().semi().begin().end().$();
  // x.program().id().pair().semi().begin().end().$();
  // x.program().id().semi().label().semi().begin().end().$();
  // x.program().id().semi().constant().semi().begin().end().$();
  // x.program().id().semi().label().semi().constant().semi().begin().end().$();
  // x.program().id().pair().semi().label().semi().semi().constant().semi().begin().end().$();
  // x.program().id().pair().semi().label().semi().semi().semi().semi().semi().semi().begin().end().$();
  // }
  // public static void illegal(augS_0_8a x) {
  // x.program().program();
  // x.program().id().id();
  // x.program().id().semi().label().constant();
  // x.program().id().semi().constant().semi().$();
  // }
}
