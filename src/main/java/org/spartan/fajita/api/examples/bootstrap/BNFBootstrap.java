package org.spartan.fajita.api.examples.bootstrap;

import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.NT.*;
import static org.spartan.fajita.api.examples.bootstrap.BNFBootstrap.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNF.ClassEllipsis;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BNFBootstrap {
  public static void expressionBuilder() {
    // showASTs();
  }

  static enum Term implements Terminal {
    start, derives, with, //
    to, and, toNone, or, orNone, //
    go;
  }

  static enum NT implements NonTerminal {
    BNF, Header, Body, Footer, //
    Variables, Terminals, Start, //
    Rules, Rule, Conjunctions, //
    First_Conjunction, Extra_Conjunctions, //
    Extra_Conjunction, //
    Symbol_Sequence
  }

  public static void buildBNF() {
    BNF b = new BNFBuilder(Term.class, NT.class) //
        .start(BNF) //
        //
        .derive(BNF)/*               */.to(Header).and(Body).and(Footer) //
        .derive(Header)/*            */.to(Variables).and(Terminals) //
        /*                               */.or().to(Terminals).and(Variables) //
        .derive(Variables)/*         */.to(with, NonTerminal.class)//
        .derive(Terminals)/*         */.to(with, Terminal.class)//
        .derive(Body)/*              */.to(Start).and(Rules) //
        .derive(Rules)/*             */.to(Rule).and(Rules) //
        /*                               */.or().to(Rule) //
        .derive(Rule)/*              */.to(derives, NonTerminal.class).and(Conjunctions) //
        .derive(Conjunctions)/*      */.to(First_Conjunction).and(Extra_Conjunctions) //
        .derive(First_Conjunction)/* */.to(to, NonTerminal.class).and(Symbol_Sequence) //
        /*                                 */.or().to(to, Terminal.class, ClassEllipsis.class).and(Symbol_Sequence) //
        /*                                 */.or().to(toNone) //
        .derive(Extra_Conjunctions)/**/.to(Extra_Conjunction).and(Extra_Conjunctions) //
        /*                                 */.orNone() //
        .derive(Extra_Conjunction)/* */.to(or, NonTerminal.class).and(Symbol_Sequence) //
        /*                                 */.or().to(or, Terminal.class, ClassEllipsis.class).and(Symbol_Sequence)//
        /*                                 */.or().to(orNone)//
        .derive(Symbol_Sequence)/*   */.to(and, NonTerminal.class) //
        /*                    */.or().to(and, Terminal.class, ClassEllipsis.class) //
        /*                    */.orNone() //
        .derive(Footer).to(go)//
        .finish();
    System.out.println(b);
  }
}
