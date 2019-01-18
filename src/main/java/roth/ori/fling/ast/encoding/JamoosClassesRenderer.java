package roth.ori.fling.ast.encoding;

import static java.util.stream.Collectors.toList;
import static roth.ori.fling.ast.encoding.ASTUtil.isInheritanceRule;
import static roth.ori.fling.ast.encoding.ASTUtil.normalize;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import roth.ori.fling.bnf.BNF;
import roth.ori.fling.bnf.EBNF;
import roth.ori.fling.export.AST;
import roth.ori.fling.parser.ell.EBNFAnalyzer;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.types.ClassType;
import roth.ori.fling.symbols.types.NestedType;
import roth.ori.fling.symbols.types.ParameterType;
import roth.ori.fling.symbols.types.VarArgs;
import roth.ori.fling.util.DAG;
import roth.ori.fling.util.DAG.MoreThanOneParent;

public class JamoosClassesRenderer {
  EBNF ebnf;
  public String topClassName;
  protected final String packagePath;
  protected final DAG<Symbol> inheritance;
  protected DAG<Symbol> reversedInheritance;
  protected Set<Symbol> abstractsymbols = new LinkedHashSet<>();
  protected List<String> innerClasses = new LinkedList<>();
  public String topClass;
  protected Map<Symbol, Map<Symbol, List<GrammarElement>>> concreteClassesMapping = new LinkedHashMap<>();
  protected Map<String, Integer> innerClassesUsedNames = new LinkedHashMap<>();
  protected Map<String, Map<String, Integer>> innerClassesFieldUsedNames = new LinkedHashMap<>();
  protected Map<String, LinkedHashMap<String, String>> innerClassesFieldTypes = new LinkedHashMap<>();
  protected Map<Symbol, Set<List<GrammarElement>>> n;
  private JamoosClassesRenderer actual = this;

  public JamoosClassesRenderer(EBNF ebnf, String packagePath) {
    this(ebnf, packagePath, new DAG.Tree<>());
  }
  protected JamoosClassesRenderer(EBNF ebnf, String packagePath, DAG<Symbol> inheritance) {
    this.inheritance = inheritance;
    this.ebnf = ebnf;
    this.packagePath = packagePath;
    this.topClassName = topClassName(ebnf);
    // NOTE should correspond to the producer in Fling
    parseTopClass(ebnf.afterSolution());
  }
  public static String topClassName(EBNF ebnf) {
    return ebnf.name + "AST";
  }
  public static String topClassName(BNF bnf) {
    return bnf.name + "AST";
  }
  protected void parseTopClass(Function<Symbol, Symbol> producer) {
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
    for (Entry<Symbol, Set<List<GrammarElement>>> r : n.entrySet()) {
      Symbol lhs = r.getKey();
      Set<List<GrammarElement>> rhs = r.getValue();
      if (!isInheritanceRule(rhs)) {
        innerClassesFieldTypes.putIfAbsent(lhs.name(), new LinkedHashMap<>());
        for (List<GrammarElement> clause : r.getValue())
          for (GrammarElement s : clause)
            parseSymbol(lhs.name(), s);
      } else
        abstractsymbols.add(lhs);
    }
    parseInnerClasses();
    for (String i : innerClasses)
      $.append(i);
    topClass = $.append("}").toString();
  }
  protected void parseInnerClasses() {
    for (Entry<Symbol, Set<List<GrammarElement>>> r : n.entrySet()) {
      StringBuilder $ = new StringBuilder();
      Symbol lhs = r.getKey();
      Set<List<GrammarElement>> rhs = r.getValue();
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
  protected void parseSymbol(String lhs, GrammarElement s) {
    for (String t : parseType(s))
      innerClassesFieldTypes.get(lhs).put(generateFieldName(lhs, s), t);
  }
  @SuppressWarnings("unused") protected List<String> parseTypes(String lhs, List<GrammarElement> ss) {
    return ss.stream().map(x -> parseType(x)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    });
  }
  protected List<String> parseType(GrammarElement s) {
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
        else if (t instanceof VarArgs) {
          VarArgs va = (VarArgs) t;
          $.add(va.nt == null ? va.toString() : va.nt.name(packagePath, topClassName) + "[]");
        } else
          $.add(t.toString());
    } else if (s.isSymbol())
      $.add(s.asNonTerminal().name(packagePath, topClassName));
    else
      $.add("Void");
    return $;
  }
  protected List<String> parseTypeForgiving(GrammarElement s) {
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
      LinkedHashMap<String, String> fixedFields = new LinkedHashMap<>();
      for (Entry<String, String> field : innerClassesFieldTypes.get(lhs).entrySet())
        fixedFields.put(field.getKey() + (name.equals(field.getKey()) ? "1" : ""), field.getValue());
      innerClassesFieldTypes.get(lhs).clear();
      innerClassesFieldTypes.get(lhs).putAll(fixedFields);
      names.put(name, Integer.valueOf(1));
    }
    int nn;
    names.put(name, Integer.valueOf(nn = innerClassesFieldUsedNames.get(lhs).get(name).intValue() + 1));
    return name + nn;
  }
  protected String generateFieldName(String lhs, GrammarElement s) {
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
  public boolean isAbstractNonTerminal(Symbol nt) {
    return actual.abstractsymbols.contains(nt);
  }
  public Symbol solveAbstractNonTerminal(Symbol nt, Terminal t, EBNFAnalyzer analyzer) {
    if (actual.reversedInheritance == null)
      actual.reversedInheritance = actual.inheritance.reverse();
    for (Symbol child : actual.reversedInheritance.get(nt))
      if (t == null && analyzer.isNullable(child) || t != null && analyzer.firstSetOf(child).contains(t))
        return actual.abstractsymbols.contains(child) ? actual.solveAbstractNonTerminal(child, t, analyzer) : child;
    return null;
  }
  public boolean isInterfaces() {
    return actual instanceof JamoosInterfacesRenderer;
  }
}
