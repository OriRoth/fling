package org.spartan.fajita.revision.ast.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.isInheritanceRule;

import java.util.Collections;
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
      StringBuilder $i = new StringBuilder();
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      // Declaration
      $i.append("public static interface I") //
          .append(lhs.name()) //
          .append((!inheritance.containsKey(lhs) ? ""
              : " extends " + String.join(",",
                  inheritance.get(lhs).stream().map(x -> packagePath + "." + topClassName + ".I" + x.name()).collect(toList())))) //
          .append("{");
      if (!isInheritanceRule(rhs)) {
        // Fields
        List<String> fields = innerClassesFieldTypes.get(lhs.name()).entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
            .collect(toList());
        $i.append(String.join("", fields.stream().map(x -> "public " + x + "();").collect(toList())));
      }
      innerClasses.add($i.append("}").toString());
    }
  }
  @Override protected List<String> parseType(Symbol s) {
    return s.isNonTerminal() ? Collections.singletonList(s.asNonTerminal().iname(packagePath, topClassName))
        : super.parseType(s);
  }
}
