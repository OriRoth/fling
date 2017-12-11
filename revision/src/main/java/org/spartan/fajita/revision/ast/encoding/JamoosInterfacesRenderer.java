package org.spartan.fajita.revision.ast.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.isInheritanceRule;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.util.DAG;

public class JamoosInterfacesRenderer extends JamoosClassesRenderer {
  public JamoosInterfacesRenderer(EBNF ebnf, String packagePath) {
    super(ebnf, packagePath, new DAG<>());
  }
  @Override protected void parseInnerClasses() {
    for (Entry<NonTerminal, Set<List<Symbol>>> r : n.entrySet()) {
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      innerClasses.add(parseInnerInterface(lhs, rhs));
      innerClasses.add(parseInnerClass(lhs, rhs));
    }
  }
  private String parseInnerInterface(NonTerminal lhs, Set<List<Symbol>> rhs) {
    StringBuilder $ = new StringBuilder();
    if (!isInheritanceRule(rhs))
      return $.toString();
    $.append("public static interface ") //
        .append(lhs.name()) //
        .append((!inheritance.containsKey(lhs) ? ""
            : " extends " + String.join(",",
                inheritance.get(lhs).stream().map(x -> packagePath + "." + topClassName + ".I" + x.name()).collect(toList())))) //
        .append("{");
    return $.append("}").toString();
  }
  private String parseInnerClass(NonTerminal lhs, Set<List<Symbol>> rhs) {
    StringBuilder $ = new StringBuilder();
    if (isInheritanceRule(rhs))
      return $.toString();
    $.append("public static class ") //
        .append(lhs.name()) //
        .append((!inheritance.containsKey(lhs) ? ""
            : " implements "
                + String.join(",", inheritance.get(lhs).stream().map(x -> x.name(packagePath, topClassName)).collect(toList())))) //
        .append("{");
    List<String> fields = innerClassesFieldTypes.get(lhs.name()).entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
        .collect(toList());
    $.append(String.join(";", fields.stream().map(x -> "public " + x).collect(toList())));
    if (!fields.isEmpty())
      $.append(";");
    $.append("public " + lhs.name() + "(") //
        .append(String.join(",", fields)) //
        .append("){") //
        .append(String.join(";", innerClassesFieldTypes.get(lhs.name()).entrySet().stream()
            .map(e -> "this." + e.getKey() + "=" + e.getKey()).collect(toList())));
    if (!fields.isEmpty())
      $.append(";");
    $.append("}");
    return $.append("}").toString();
  }
}
