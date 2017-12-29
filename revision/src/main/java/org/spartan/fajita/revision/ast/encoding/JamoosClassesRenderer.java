package org.spartan.fajita.revision.ast.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.isInheritanceRule;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.normalize;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.export.AST;
import org.spartan.fajita.revision.parser.ell.EBNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ClassType;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.util.DAG;
import org.spartan.fajita.revision.util.DAG.MoreThanOneParent;

public class JamoosClassesRenderer {
  EBNF ebnf;
  public String topClassName;
  protected final String packagePath;
  protected final DAG<NonTerminal> inheritance;
  protected DAG<NonTerminal> reversedInheritance;
  protected Set<NonTerminal> abstractNonTerminals = new LinkedHashSet<>();
  protected List<String> innerClasses = new LinkedList<>();
  public String topClass;
  protected Map<NonTerminal, Map<NonTerminal, List<Symbol>>> concreteClassesMapping = new LinkedHashMap<>();
  protected Map<String, Integer> innerClassesUsedNames = new LinkedHashMap<>();
  protected Map<String, Map<String, Integer>> innerClassesFieldUsedNames = new LinkedHashMap<>();
  protected Map<String, LinkedHashMap<String, String>> innerClassesFieldTypes = new LinkedHashMap<>();
  protected Map<NonTerminal, Set<List<Symbol>>> n;
  protected EBNFAnalyzer analyzer;
  private JamoosClassesRenderer actual = this;

  public JamoosClassesRenderer(EBNF ebnf, String packagePath) {
    this(ebnf, packagePath, new DAG.Tree<>());
  }
  protected JamoosClassesRenderer(EBNF ebnf, String packagePath, DAG<NonTerminal> inheritance) {
    this.inheritance = inheritance;
    this.ebnf = ebnf;
    this.packagePath = packagePath;
    this.topClassName = topClassName(ebnf);
    // NOTE should correspond to the producer in Fajita
    parseTopClass(ebnf.afterSolution());
  }
  public static String topClassName(EBNF ebnf) {
    return ebnf.name + "AST";
  }
  public static String topClassName(BNF bnf) {
    return bnf.name + "AST";
  }
  protected void parseTopClass(Function<NonTerminal, NonTerminal> producer) {
    StringBuilder $ = new StringBuilder();
    $.append("package " + packagePath + ";");
    $.append("public class " + topClassName + " implements " + AST.class.getTypeName() + "{");
    try {
      n = normalize(ebnf, inheritance, producer);
    } catch (@SuppressWarnings("unused") MoreThanOneParent e) {
      actual = new JamoosInterfacesRenderer(ebnf, packagePath);
      topClass = actual.topClass;
      return;
    }
    for (Entry<NonTerminal, Set<List<Symbol>>> r : n.entrySet()) {
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      if (!isInheritanceRule(rhs)) {
        innerClassesFieldTypes.putIfAbsent(lhs.name(), new LinkedHashMap<>());
        for (List<Symbol> clause : r.getValue())
          for (Symbol s : clause)
            parseSymbol(lhs.name(), s);
      } else
        abstractNonTerminals.add(lhs);
    }
    parseInnerClasses();
    for (String i : innerClasses)
      $.append(i);
    topClass = $.append("}").toString();
  }
  protected void parseInnerClasses() {
    for (Entry<NonTerminal, Set<List<Symbol>>> r : n.entrySet()) {
      StringBuilder $ = new StringBuilder();
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      // Declaration
      $.append("public static class ") //
          .append(lhs.name()) //
          .append((!inheritance.containsKey(lhs) ? ""
              : " extends " + packagePath + "." + topClassName + "." + inheritance.get(lhs).iterator().next())) //
          .append("{");
      if (!isInheritanceRule(rhs)) {
        // Fields
        List<String> fields = innerClassesFieldTypes.get(lhs.name()).entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
            .collect(toList());
        $.append(String.join(";", fields.stream().map(x -> "public " + x).collect(toList())));
        if (!fields.isEmpty())
          $.append(";");
        // Constructor
        $.append("public " + lhs.name() + "(") //
            .append(String.join(",", fields)) //
            .append("){") //
            .append(String.join(";", innerClassesFieldTypes.get(lhs.name()).entrySet().stream()
                .map(e -> "this." + e.getKey() + "=" + e.getKey()).collect(toList())));
        if (!fields.isEmpty())
          $.append(";");
        $.append("}");
      }
      innerClasses.add($.append("}").toString());
    }
  }
  protected void parseSymbol(String lhs, Symbol s) {
    for (String t : parseType(s))
      innerClassesFieldTypes.get(lhs).put(generateFieldName(lhs, s), t);
  }
  @SuppressWarnings("unused") protected List<String> parseTypes(String lhs, List<Symbol> ss) {
    return ss.stream().map(x -> parseType(x)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    });
  }
  protected List<String> parseType(Symbol s) {
    List<String> $ = new LinkedList<>();
    if (s.isExtendible())
      $.addAll(s.asExtendible().parseTypes(this::parseType, this::parseTypeForgiving));
    else if (s.isVerb()) {
      Verb v = s.asVerb();
      for (ParameterType t : v.type)
        if (t instanceof ClassType)
          $.add(((ClassType) t).clazz.getTypeName());
        else if (t instanceof NestedType)
          $.addAll(parseType(((NestedType) t).nested));
        else
          $.add(t.toString());
    } else if (s.isNonTerminal())
      $.add(s.asNonTerminal().name(packagePath, topClassName));
    else
      $.add("Void");
    return $;
  }
  protected List<String> parseTypeForgiving(Symbol s) {
    return s.isVerb() && s.asVerb().type.length == 0 ? Collections.singletonList("Void") : parseType(s);
  }
  protected String generateFieldName(String lhs, String name) {
    if (!innerClassesFieldUsedNames.containsKey(lhs))
      innerClassesFieldUsedNames.put(lhs, new LinkedHashMap<>());
    Map<String, Integer> names = innerClassesFieldUsedNames.get(lhs);
    if (!names.containsKey(name)) {
      names.put(name, Integer.valueOf(0));
      return name;
    }
    if (names.get(name).intValue() == 0) {
      String type = innerClassesFieldTypes.get(lhs).get(name);
      innerClassesFieldTypes.get(lhs).remove(name);
      innerClassesFieldTypes.get(lhs).put(name + "1", type);
      names.put(name, Integer.valueOf(1));
    }
    int nn;
    names.put(name, Integer.valueOf(nn = innerClassesFieldUsedNames.get(lhs).get(name).intValue() + 1));
    return name + nn;
  }
  protected String generateFieldName(String lhs, Symbol s) {
    return generateFieldName(lhs, s.head().name().toLowerCase());
  }
  protected String generateClassName(String name) {
    if (!innerClassesUsedNames.containsKey(name)) {
      innerClassesUsedNames.put(name, Integer.valueOf(1));
      return name + 1;
    }
    int nn;
    innerClassesUsedNames.put(name, Integer.valueOf(nn = innerClassesUsedNames.get(name).intValue() + 1));
    return name + nn;
  }
  public static JamoosClassesRenderer render(EBNF ebnf, String packagePath) {
    return new JamoosClassesRenderer(ebnf, packagePath);
  }
  public boolean isAbstractNonTerminal(NonTerminal nt) {
    return actual.abstractNonTerminals.contains(nt);
  }
  public NonTerminal solveAbstractNonTerminal(NonTerminal nt, Terminal t) {
    if (actual.reversedInheritance == null)
      actual.reversedInheritance = actual.inheritance.reverse();
    if (actual.analyzer == null)
      actual.analyzer = new EBNFAnalyzer(actual.n, actual.ebnf.startSymbols);
    for (NonTerminal child : actual.reversedInheritance.get(nt))
      if (t == null && actual.analyzer.isNullable(child) || t != null && actual.analyzer.firstSetOf(child).contains(t))
        return actual.abstractNonTerminals.contains(child) ? actual.solveAbstractNonTerminal(child, t) : child;
    return null;
  }
  public boolean isInterfaces() {
    return actual instanceof JamoosInterfacesRenderer;
  }
}
