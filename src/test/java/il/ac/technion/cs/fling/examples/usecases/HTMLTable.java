package il.ac.technion.cs.fling.examples.usecases;

import static il.ac.technion.cs.fling.examples.generated.HTMLTable.html;
import static java.util.stream.Collectors.joining;

import il.ac.technion.cs.fling.examples.generated.HTMLTableAST.*;

public class HTMLTable {
  // @formatter:off
  public static void main(final String[] args) {
    final HTML page =
      html("My Table").
      table("style=\"width:100%\"").
        tr().
          th().¢("Issue Name").end().
          th().¢("Asignee").end().
          th().¢("Description").end().
        end().
        tr().
          td().¢("Clean Code").end().
          td().¢("Amy").end().
          td().¢("Formatting + refactoring").end().
        end().
        tr().
          td().¢("Document Classes").end().
          td().¢("Bruce").end().
          td().¢("Many classes lack documentation").end().
        end().
        tr().
          td().¢("Fix Bug").end().
          td().¢("Clara").end().
          td().
            table().
              tr().
                th().¢("#").end().
                th().¢("TODO").end().
              end().
              tr().
                td().¢("1").end().
                td().¢("Find bug").end().
              end().
              tr().
                td().¢("2").end().
                td().¢("Create test").end().
              end().
              tr().
                td().¢("3").end().
                td().¢("Terminate bug").end().
              end().
            end().
          end().
        end().
      end().$();
    System.out.println(toString(page));
  }
  // @formatter:on
  public static String toString(final HTML page) {
    return String.format("" //
        + "<!DOCTYPE html>\n" //
        + "<html>\n" //
        + "<body>\n" //
        + "<h2>%s</h2>\n" //
        + "%s\n" //
        + "</body>" //
        + "</html>\n", //
        page.html, toString(page.table, 0));
  }

  public static String toString(final Table table, final int depth) {
    return String.format("" //
        + "%s<table%s>\n" //
        + "%s\n" //
        + "%s\n" //
        + "%s</table>", //
        printTabs(depth), //
        printOptions(table.table), //
        toString(table.header, depth + 1), //
        table.row.stream().map(line -> toString(line, depth + 1)).collect(joining("\n")), //
        printTabs(depth));
  }

  public static String toString(final Header header, final int depth) {
    return String.format("" //
        + "%s<tr%s>\n" //
        + "%s\n" //
        + "%s</tr>", //
        printTabs(depth), //
        printOptions(header.tr.tr), //
        header.th.stream().map(th -> String.format("%s<th%s>%s</th>", //
            printTabs(depth + 1), //
            printOptions(th.th), //
            th.¢ //
        )).collect(joining("\n")), //
        printTabs(depth));
  }

  public static String toString(final Row r, final int depth) {
    return String.format("" //
        + "%s<tr%s>\n" //
        + "%s\n" //
        + "%s</tr>", //
        printTabs(depth), //
        printOptions(r.tr.tr), //
        r.td.stream().map(td -> String.format("%s<td%s>%s</td>", //
            printTabs(depth + 1), //
            printOptions(td.td), //
            td.cell instanceof Cell1 ? ((Cell1) td.cell).¢ : //
                "\n" + toString((Table) td.cell, depth + 2) + "\n" + printTabs(depth + 1) //
        )).collect(joining("\n")), //
        printTabs(depth));
  }

  private static String printOptions(String[] options) {
    return options.length == 0 ? "" : " " + String.join(" ", options);
  }

  private static String printTabs(int depth) {
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < depth; ++i)
      $.append('\t');
    return $.toString();
  }
}
