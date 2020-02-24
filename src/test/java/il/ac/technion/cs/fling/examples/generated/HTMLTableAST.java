package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface HTMLTableAST {
  public class HTML {
    public final java.lang.String html;
    public final Table table;

    public HTML(java.lang.String html, Table table) {
      this.html = html;
      this.table = table;
    }
  }

  public class Table implements Cell {
    public final java.lang.String[] table;
    public final Header header;
    public final java.util.List<Row> row;

    public Table(java.lang.String[] table, Header header, java.util.List<Row> row) {
      this.table = table;
      this.header = header;
      this.row = row;
    }
  }

  public class Header {
    public final Tr tr;
    public final java.util.List<Th> th;

    public Header(Tr tr, java.util.List<Th> th) {
      this.tr = tr;
      this.th = th;
    }
  }

  public class Row {
    public final Tr tr;
    public final java.util.List<Td> td;

    public Row(Tr tr, java.util.List<Td> td) {
      this.tr = tr;
      this.td = td;
    }
  }

  public class Tr {
    public final java.lang.String[] tr;

    public Tr(java.lang.String[] tr) {
      this.tr = tr;
    }
  }

  public class Th {
    public final java.lang.String[] th;
    public final java.lang.String ¢;

    public Th(java.lang.String[] th, java.lang.String ¢) {
      this.th = th;
      this.¢ = ¢;
    }
  }

  public class Td {
    public final java.lang.String[] td;
    public final Cell cell;

    public Td(java.lang.String[] td, Cell cell) {
      this.td = td;
      this.cell = cell;
    }
  }

  interface Cell {}

  public class Cell1 implements Cell {
    public final java.lang.String ¢;

    public Cell1(java.lang.String ¢) {
      this.¢ = ¢;
    }
  }

  public static class Visitor {
    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.HTML hTML) {
      try {
        this.whileVisiting(hTML);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table) hTML.table);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table table) {
      try {
        this.whileVisiting(table);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header) table.header);
      table
          .row
          .stream()
          .forEach(_x_ -> visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row) _x_));
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header header) {
      try {
        this.whileVisiting(header);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr) header.tr);
      header
          .th
          .stream()
          .forEach(_x_ -> visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th) _x_));
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row row) {
      try {
        this.whileVisiting(row);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr) row.tr);
      row.td
          .stream()
          .forEach(_x_ -> visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td) _x_));
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr tr) {
      try {
        this.whileVisiting(tr);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th th) {
      try {
        this.whileVisiting(th);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td td) {
      try {
        this.whileVisiting(td);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell) td.cell);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell cell) {
      if (cell instanceof il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1)
        visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1) cell);
      else if (cell instanceof il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table)
        visit((il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table) cell);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1 cell1) {
      try {
        this.whileVisiting(cell1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.HTML hTML)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Table table)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Header header)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Row row)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Tr tr)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Th th)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Td td)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.HTMLTableAST.Cell1 cell1)
        throws java.lang.Exception {}
  }
}

