package org.spartan.fajita.api.examples;

import static org.spartan.fajita.api.examples.ToiletteSeat.ToiletteTerminals.*;
import static org.spartan.fajita.api.examples.ToiletteSeat.ToiletteVariables.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class ToiletteSeat {
  public static void expressionBuilder() {
    //
  }

  static enum ToiletteTerminals implements Terminal {
    male, female, urinate, defecate, lower, raise;
  }

  static enum ToiletteVariables implements NonTerminal {
    Visitors, Down_Visitors, Up_Visitors, Up_Visitor, Down_Visitor, Lowering_Visitor, Raising_Visitor, Action
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(ToiletteTerminals.class, ToiletteVariables.class).start(Visitors) //
        .derive(Visitors) //
        /*                  */.to(Down_Visitors) //
        .derive(Down_Visitors)//
        /*                  */.to(Down_Visitor).and(Down_Visitors) //
        /*                  */.or(Raising_Visitor).and(Up_Visitors) //
        /*                  */.orNone() //
        .derive(Up_Visitors) //
        .to(Up_Visitor).and(Up_Visitors) //
        /*                  */.or(Lowering_Visitor).and(Down_Visitors) //
        /*                  */.orNone() //
        .derive(Up_Visitor)//
        /*                  */.to(male).and(urinate) //
        .derive(Down_Visitor) //
        /*                  */.to(female).and(Action) //
        /*                  */.or(male).and(defecate) //
        .derive(Raising_Visitor)//
        /*                  */.to(male).and(raise).and(urinate) //
        .derive(Lowering_Visitor) //
        /*                  */.to(female).and(lower).and(Action) //
        /*                  */.or(male).and(lower).and(defecate) //
        .derive(Action) //
        /*                  */.to(urinate) //
        /*                  */.or(defecate) //
        .go();
    return bnf;
  }
}
