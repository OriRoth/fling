package fling.examples.languages;

import static fling.Symbol.noneOrMore;
import static fling.examples.languages.HTMLTable.V.*;
import static fling.examples.languages.HTMLTable.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;

import fling.*;
import fling.BNF;
import fling.adapters.JavaMediator;

public class HTMLTable {
  public enum Σ implements Terminal {
    html, table, tr, th, td, end, ¢
  }

  public enum V implements Variable {
    HTML, Table, Header, Line, Tr, Th, Td, Cell
  }

  public static final BNF bnf = bnf(). //
      start(HTML). //
      derive(HTML).to(html.with(String.class), Table). //
      derive(Table).to(table.many(String.class), Header, noneOrMore(Line), end). //
      derive(Header).to(Tr, noneOrMore(Th), end). //
      derive(Line).to(Tr, noneOrMore(Td), end). //
      derive(Tr).to(tr.many(String.class)). //
      derive(Th).to(th.many(String.class), ¢.with(String.class), end). //
      derive(Td).to(td.many(String.class), Cell, end). //
      derive(Cell).to(¢.with(String.class)).or(Table). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "HTMLTable", Σ.class);
}
