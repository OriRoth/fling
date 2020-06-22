package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Cell;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.HTML;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Header;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Row;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Table;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Td;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Th;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ.Tr;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.end;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.html;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.table;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.td;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.th;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.tr;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.¢;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.HTMLTable.Γ;
import il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class HTMLTable implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    html, table, tr, th, td, end, ¢
  }
  public enum Γ implements Variable {
    HTML, Table, Header, Row, Tr, Th, Td, Cell
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf(). //
        start(HTML). //
        derive(HTML).to(html.with(String.class), Table). //
        derive(Table).to(table.many(String.class), Header, Quantifiers.noneOrMore(Row), end). //
        derive(Header).to(Tr, Quantifiers.noneOrMore(Th), end). //
        derive(Row).to(Tr, Quantifiers.noneOrMore(Td), end). //
        derive(Tr).to(tr.many(String.class)). //
        derive(Th).to(th.many(String.class), ¢.with(String.class), end). //
        derive(Td).to(td.many(String.class), Cell, end). //
        derive(Cell).to(¢.with(String.class)).or(Table). //
        build();
  }
}
