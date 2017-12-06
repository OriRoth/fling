package org.spartan.fajita.revision.ast.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.isInheritanceRule;
import static org.spartan.fajita.revision.ast.encoding.ASTUtil.normalize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.parser.ell.EBNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ClassType;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.util.DAG;

public class JamoosClassesRenderer {
  EBNF ebnf;
  public String topClassName;
  public final String packagePath;
  DAG.Tree<NonTerminal> inheritance = new DAG.Tree<>();
  DAG<NonTerminal> reversedInheritance;
  Set<NonTerminal> abstractNonTerminals = new HashSet<>();
  private List<String> innerClasses = new LinkedList<>();
  public String topClass;
  public Map<NonTerminal, Map<NonTerminal, List<Symbol>>> concreteClassesMapping = new HashMap<>();
  public Map<String, Integer> innerClassesUsedNames = new HashMap<>();
  public Map<String, Map<String, Integer>> innerClassesFieldUsedNames = new HashMap<>();
  public Map<String, LinkedHashMap<String, String>> innerClassesFieldTypes = new HashMap<>();
  private Map<NonTerminal, Set<List<Symbol>>> n;
  private EBNFAnalyzer analyzer;

  public JamoosClassesRenderer(EBNF ebnf, String packagePath) {
    this.ebnf = ebnf;
    this.packagePath = packagePath;
    // NOTE should correspond to the producer in Fajita
    parseTopClass(Fajita.producer());
  }
  public static String topClassName(EBNF ebnf) {
    return ebnf.name + "AST";
  }
  public static String topClassName(BNF bnf) {
    return bnf.name + "AST";
  }
  private void parseTopClass(Function<NonTerminal, NonTerminal> producer) {
    StringBuilder $ = new StringBuilder();
    $.append("package " + packagePath + ";");
    $.append("public class " + (topClassName = topClassName(ebnf)) + "{");
    parseInnerClasses(producer);
    for (String i : innerClasses)
      $.append(i);
    topClass = $.append("}").toString();
  }
  private void parseInnerClasses(Function<NonTerminal, NonTerminal> producer) {
    n = normalize(ebnf, inheritance, producer);
    for (Entry<NonTerminal, Set<List<Symbol>>> r : n.entrySet()) {
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      if (!isInheritanceRule(rhs)) {
        innerClassesFieldTypes.putIfAbsent(lhs.name(), new LinkedHashMap<>());
        for (List<Symbol> clause : r.getValue())
          for (Symbol s : clause)
            parseSymbol(lhs.name(), s);
        // if (!inheritance.get(lhs).isEmpty()) {
        // NonTerminal parent = inheritance.get(lhs).iterator().next();
        // concreteClassesMapping.putIfAbsent(parent, new HashMap<>());
        // concreteClassesMapping.get(parent).put(key, value);
        // }
      } else
        abstractNonTerminals.add(lhs);
    }
    for (Entry<NonTerminal, Set<List<Symbol>>> r : n.entrySet()) {
      StringBuilder $ = new StringBuilder();
      NonTerminal lhs = r.getKey();
      Set<List<Symbol>> rhs = r.getValue();
      // Declaration
      $.append("public static class ") //
          .append(lhs.name()) //
          .append((!inheritance.containsKey(lhs) ? "" : " extends " + inheritance.get(lhs).iterator().next())) //
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
          $.append(";") //
              .append("}");
      }
      innerClasses.add($.append("}").toString());
    }
  }
  private void parseSymbol(String lhs, Symbol s) {
    for (String t : parseType(s))
      innerClassesFieldTypes.get(lhs).put(generateFieldName(lhs, s), t);
  }
  @SuppressWarnings("unused") private List<String> parseTypes(String lhs, List<Symbol> ss) {
    return ss.stream().map(x -> parseType(x)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    });
  }
  private List<String> parseType(Symbol s) {
    List<String> $ = new LinkedList<>();
    // if (s instanceof Optional) {
    // Optional o = (Optional) s;
    // for (String x : parseTypes(lhs, o.symbols))
    // $.add("java.util.Optional<" + x + ">");
    // } else if (s instanceof Either)
    // $.add(generateEither((Either) s));
    // else if (s instanceof OneOrMore) {
    // OneOrMore o = (OneOrMore) s;
    // for (String x : parseTypes(lhs, o.symbols))
    // $.add(x + "[]");
    // for (String x : parseTypes(lhs, o.separators))
    // $.add(x + "[]");
    // } else if (s instanceof NoneOrMore || s instanceof NoneOrMore.Separator
    // || s instanceof NoneOrMore.IfNone) {
    // NoneOrMore n = s instanceof NoneOrMore ? (NoneOrMore) s
    // : s instanceof NoneOrMore.Separator ? ((NoneOrMore.Separator) s).parent()
    // : ((NoneOrMore.IfNone) s).parent();
    // if (n.ifNone.isEmpty()) {
    // for (String x : parseTypes(lhs, n.symbols))
    // $.add(x + "[]");
    // for (String x : parseTypes(lhs, n.separators))
    // $.add(x + "[]");
    // } else
    // $.add(generateEither((NoneOrMore) s));
    // } else if (s instanceof EVerb) {
    // EVerb e = (EVerb) s;
    // $.addAll(parseType(lhs, e.ent));
    // } else //
    if (s.isExtendible())
      $.addAll(s.asExtendible().parseTypes(this::parseType));
    else if (s.isVerb()) {
      Verb v = (Verb) s;
      for (ParameterType t : v.type)
        if (t instanceof ClassType)
          $.add(((ClassType) t).clazz.getTypeName());
        else if (t instanceof NestedType)
          $.addAll(parseType(((NestedType) t).nested));
        else
          $.add(t.toString());
    } else if (s instanceof NonTerminal)
      $.add(((NonTerminal) s).name(packagePath, topClassName));
    else
      $.add("Void");
    return $;
  }
  // // NOTE this method (maybe others too) assume "either" accepts simple
  // symbols
  // private String generateEither(Either e) {
  // StringBuilder $ = new StringBuilder();
  // String name = generateClassName("Either");
  // $.append("static class ").append(name).append("{");
  // List<String> enumContent = an.empty.list();
  // $.append("public Object $;").append("public Tag tag;");
  // for (Symbol x : e.symbols) {
  // String verbType, typeName, capitalName;
  // $.append("boolean is").append(capitalName =
  // capital(x.name())).append("(){return Tag.").append(capitalName)
  // .append(".equals(tag);}");
  // $.append(typeName = x.isVerb() ? ("".equals(verbType = ((Verb)
  // x).type.toString()) ? "Void" : verbType) : "Void")
  // .append(" get").append(capitalName).append("(){return (") //
  // .append(typeName).append(")$;}");
  // enumContent.add(capitalName);
  // }
  // $.append("public enum Tag{");
  // for (String x : enumContent)
  // $.append(x).append(",");
  // $.append("}}");
  // innerClasses.add($.toString());
  // return name;
  // }
  // private String generateEither(NoneOrMore n) {
  // StringBuilder $ = new StringBuilder();
  // String name = generateClassName("Either");
  // $.append("static class ").append(name).append("{public boolean exist;");
  // for (String type : parseTypes(name, n.symbols)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // for (String type : parseTypes(name, n.separators)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // for (String type : parseTypes(name, n.ifNone)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // $.append("boolean isList(){return exist;}boolean isNone(){return
  // !exist;}}");
  // innerClasses.add($.toString());
  // return name;
  // }
  private String generateFieldName(String lhs, String name) {
    if (!innerClassesFieldUsedNames.containsKey(lhs))
      innerClassesFieldUsedNames.put(lhs, new HashMap<>());
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
  private String generateFieldName(String lhs, Symbol s) {
    return generateFieldName(lhs, s.head().name().toLowerCase());
  }
  @SuppressWarnings("unused") private String generateClassName(String name) {
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
    return abstractNonTerminals.contains(nt);
  }
  public NonTerminal solveAbstractNonTerminal(NonTerminal nt, Terminal t) {
    if (reversedInheritance == null)
      reversedInheritance = inheritance.reverse();
    if (analyzer == null)
      analyzer = new EBNFAnalyzer(n, ebnf.startSymbols);
    for (NonTerminal child : reversedInheritance.get(nt))
      if (t == null && analyzer.isNullable(child) || t != null && analyzer.firstSetOf(child).contains(t))
        return abstractNonTerminals.contains(child) ? solveAbstractNonTerminal(child, t) : child;
    return null;
  }
}
