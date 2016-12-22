package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.Pascal.NT.*;
import static org.spartan.fajita.api.examples.Pascal.Term.*;
import static org.spartan.fajita.api.junk.Pascal.program;

import java.io.IOException;

import org.spartan.fajita.api.Fajita;
import org.spartan.fajita.api.Main;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Pascal {
  private static final String apiName = "Pascal";

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

  public static String buildApi() {
    return Fajita.buildBNF(Term.class, NT.class) //
        .setApiName(apiName) //
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
        .go(Main.packagePath);
  }
  public static void main(String[] args) throws IOException {
    Main.apiGenerator(apiName, buildApi());
  }
  public static void legal() {
    program().id().semi().begin().end().$();
    program().id().pair().semi().begin().end().$();
    program().id().semi().label().semi().begin().end().$();
    program().id().semi().constant().semi().begin().end().$();
    program().id().semi().label().semi().constant().semi().begin().end().$();
    program().id().pair().semi().label().semi().semi().constant().semi().begin().end().$();
    program().id().pair().semi().label().semi().semi().semi().semi().semi().semi().begin().end().$();
  }
  public static void illegal() {
    // program().program();
    // program().id().id();
    // program().id().semi().label().constant();
    // program().id().semi().constant().semi().$();
  }
}
