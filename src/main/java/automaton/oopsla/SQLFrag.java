package automaton.oopsla;

import static automaton.oopsla.SQLFrag.SQLNonterminals.Operator;
import static automaton.oopsla.SQLFrag.SQLNonterminals.Quant;
import static automaton.oopsla.SQLFrag.SQLNonterminals.Query;
import static automaton.oopsla.SQLFrag.SQLNonterminals.Where;
import static automaton.oopsla.SQLFrag.SQLTerminals.all;
import static automaton.oopsla.SQLFrag.SQLTerminals.column;
import static automaton.oopsla.SQLFrag.SQLTerminals.columns;
import static automaton.oopsla.SQLFrag.SQLTerminals.equals;
import static automaton.oopsla.SQLFrag.SQLTerminals.from;
import static automaton.oopsla.SQLFrag.SQLTerminals.greaterThan;
import static automaton.oopsla.SQLFrag.SQLTerminals.lowerThan;
import static automaton.oopsla.SQLFrag.SQLTerminals.select;
import static automaton.oopsla.SQLFrag.SQLTerminals.where;

import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class SQLFrag {
  
  class Table{} 
  class Column {}
  class Expr {}
  
  
  static 
    // begin{enums}
  enum SQLTerminals implements Terminal{
    select,from,all,columns,
    where,column,equals,greaterThan,lowerThan;
  }

    // end{enums}
  static
    // begin{enums}
  enum SQLNonterminals implements NonTerminal{
    Query,Quant,Where,Operator;
  }
    // end{enums}

  void defineBNF(){
    // begin{BNF}
    new BNFBuilder(SQLTerminals.class, SQLNonterminals.class)
      .start(Query)
      .derive(Query).to(select)
                    .and(Quant).and(from,Table.class).and(Where)
      .derive(Quant).to(all)
                    .or(columns,Column[].class)
      .derive(Where).to(where)
                    .and(column,Column.class).and(Operator)
                    .orNone()
      .derive(Operator).to(equals,Expr.class)
                       .or(greaterThan,Expr.class)
                       .or(lowerThan,Expr.class)
      .finish();
    // end{BNF}
  }
  // begin{usage}
  void usage_example(Column c,Table t,Expr e){
    new Query()
      .select().all().from(t);
    new Query()
      .select().all().from(t)
      .where().column(c).equals(e);
  }
  // end{usage}
}
