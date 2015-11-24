package org.spartan.fajita.api.examples.toiletteSeat;

import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.defecate;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.female;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.lower;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.male;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.raise;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteTerminals.urinate;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Action;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Down_Visitor;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Down_Visitors;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Lowering_Visitor;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Raising_Visitor;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Up_Visitor;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Up_Visitors;
import static org.spartan.fajita.api.examples.toiletteSeat.ToiletteSeat.ToiletteVariables.Visitors;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Type;

public class ToiletteSeat {
  public static void expressionBuilder() {
    //
  }

  static enum ToiletteTerminals implements Terminal {
    male, female,
    urinate, defecate,
    lower, raise;
    @Override public Type type() {
      return Type.VOID;
    }
  }

  static enum ToiletteVariables implements NonTerminal {
    Visitors, Down_Visitors, Up_Visitors,
    Up_Visitor, Down_Visitor,
    Lowering_Visitor, Raising_Visitor,
    Action
  }

  public static BNF buildBNF() {
    BNF bnf = new BNFBuilder(ToiletteTerminals.class,ToiletteVariables.class)
    .startConfig() //
    .setApiNameTo("Toilette Seat") //
    .setStartSymbols(Visitors) //
    .endConfig() //
    .derive(Visitors) //
      .to(Down_Visitors) //
    .derive(Down_Visitors) //
      .to(Down_Visitor).and(Down_Visitors) //
      .or().to(Raising_Visitor).and(Up_Visitors) //
      .or().to(SpecialSymbols.epsilon) //
    .derive(Up_Visitors) //
      .to(Up_Visitor).and(Up_Visitors) //
      .or().to(Lowering_Visitor).and(Down_Visitors) //
      .or().to(SpecialSymbols.epsilon) //
    .derive(Up_Visitor)//
      .to(male).and(urinate) //
    .derive(Down_Visitor) //
      .to(female).and(Action) //
      .or().to(male).and(defecate) //
    .derive(Raising_Visitor)//
      .to(male).and(raise).and(urinate) //
    .derive(Lowering_Visitor) //
      .to(female).and(lower).and(Action) //
      .or().to(male).and(lower).and(defecate) //
    .derive(Action) //
      .to(urinate) //
      .or().to(defecate) //
    .finish(); 
    return bnf;
  }
}