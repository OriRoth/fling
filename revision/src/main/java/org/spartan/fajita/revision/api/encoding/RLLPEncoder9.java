package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM3.JAMMED;
import static org.spartan.fajita.revision.parser.rll.JSM3.UNKNOWN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.JSM3;
import org.spartan.fajita.revision.parser.rll.RLLPConcrete3;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

public class RLLPEncoder9 {
  public final String topClassName;
  public final String topClass;
  final NonTerminal startSymbol;
  final String astTopClass;
  final BNF bnf;
  final BNFAnalyzer analyzer;
  public final String packagePath;
  final String topClassPath;
  final Namer namer;
  final List<String> apiTypes;
  final List<String> staticMethods;
  final Class<? extends Grammar> provider;

  public RLLPEncoder9(Fajita fajita, NonTerminal start, String astTopClass) {
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = start;
    provider = fajita.provider;
    bnf = fajita.bnf();
    this.astTopClass = astTopClass;
    analyzer = new BNFAnalyzer(bnf);
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    computeMembers();
    StringBuilder $ = new StringBuilder();
    $.append("package ").append(packagePath).append(";@").append(SuppressWarnings.class.getCanonicalName()) //
        .append("(\"all\")").append(" public class ").append(topClassName).append("{");
    for (String s : staticMethods)
      $.append(s);
    for (String s : apiTypes)
      $.append(s);
    $.append("}");
    topClass = $.toString();
  }
  // TODO Roth: code duplication in constructors
  public RLLPEncoder9(Fajita fajita, Symbol nested, String astTopClass) {
    assert nested.isNonTerminal() || nested.isExtendible();
    topClassName = nested.name();
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = nested.head().asNonTerminal();
    provider = fajita.provider;
    bnf = fajita.bnf().getSubBNF(startSymbol);
    this.astTopClass = astTopClass;
    analyzer = new BNFAnalyzer(bnf);
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    computeMembers();
    StringBuilder $ = new StringBuilder();
    $.append("package ").append(packagePath).append(";@").append(SuppressWarnings.class.getCanonicalName()) //
        .append("(\"all\")").append(" public class ").append(topClassName).append("{");
    for (String s : staticMethods)
      $.append(s);
    for (String s : apiTypes)
      $.append(s);
    $.append("}");
    topClass = $.toString();
  }
  private void computeMembers() {
    new MembersComputer().compute();
  }

  class Namer {
    private final Map<Verb, String> verbNames = new LinkedHashMap<>();
    private final Map<Terminal, Integer> terminalCounts = new LinkedHashMap<>();
    private final Map<TypeEncoding, String> recNames = new LinkedHashMap<>();
    private int recCount;

    public String name(Verb v) {
      if (SpecialSymbols.$.equals(v))
        return "$";
      if (verbNames.containsKey(v))
        return verbNames.get(v);
      int x;
      terminalCounts.put(v.terminal,
          Integer.valueOf(x = terminalCounts.getOrDefault(v.terminal, Integer.valueOf(0)).intValue() + 1));
      String $ = v.terminal.name() + x;
      verbNames.put(v, $);
      return $;
    }
    public String name(Symbol s, List<Verb> legalJumps) {
      StringBuilder $ = new StringBuilder(s.isNonTerminal() ? s.asNonTerminal().name() : name(s.asVerb())).append("_");
      for (Verb v : bnf.verbs)
        if (legalJumps.contains(v))
          $.append(name(v));
      return $.toString();
    }
    public String name(TypeEncoding t) {
      if (recNames.containsKey(t))
        return recNames.get(t);
      recNames.put(t, "R" + (++recCount) + "_" + name(t.jsm.peek(), t.jsm.legalJumps()));
      return recNames.get(t);
    }
  }

  class MembersComputer {
    private final Map<Symbol, Set<List<Verb>>> seenTypes = new LinkedHashMap<>();
    private final Set<String> apiTypeNames = new LinkedHashSet<>();
    private final Set<TypeEncoding> seenRecs = new LinkedHashSet<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      computeStaticMethods();
      compute$$$Type();
    }
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      JSM3 jsm = RLLPConcrete3.next(new JSM3(bnf, analyzer, startSymbol, new ArrayList<>()), v, false);
      jsm.makeTerminus();
      computeType(jsm, v, x -> namer.name(x), () -> "E");
      // NOTE should be applicable only for $ jumps
      Function<Verb, String> unknownSolution = !bnf.isSubBNF ? x -> {
        assert SpecialSymbols.$.equals(x);
        return "$";
      } : x -> {
        assert SpecialSymbols.$.equals(x);
        return "$$$";
      };
      Supplier<String> emptySolution = !bnf.isSubBNF ? () -> "$" : () -> "$$$";
      staticMethods.add(new StringBuilder("public static ") //
          .append(solveType(computeType(jsm, v, unknownSolution, emptySolution), unknownSolution, emptySolution)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
    }
    private TypeEncoding computeType(JSM3 jsm, Verb origin, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      return computeType(jsm, origin, unknownSolution, emptySolution, null);
    }
    private TypeEncoding computeType(JSM3 jsm, Verb origin, Function<Verb, String> unknownSolution, Supplier<String> emptySolution,
        TypeEncoding parent) {
      assert jsm != JAMMED;
      if (jsm == UNKNOWN)
        return new TypeEncoding(jsm, origin, unknownSolution.apply(origin), parent);
      if (jsm.isEmpty())
        return new TypeEncoding(jsm, origin, emptySolution.get(), parent);
      Symbol top = jsm.peek();
      List<Verb> legalJumps = jsm.legalJumps();
      String $ = namer.name(top, legalJumps);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return computeTemplates(new TypeEncoding(jsm, origin, $, parent), jsm, unknownSolution, emptySolution);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(legalJumps);
      StringBuilder t = new StringBuilder("public interface ").append($);
      StringBuilder template = new StringBuilder("<E");
      List<String> templates = new ArrayList<>();
      for (Verb v : legalJumps)
        if (!SpecialSymbols.$.equals(v))
          templates.add(namer.name(v));
      if (!templates.isEmpty()) {
        template.append(",");
        template.append(String.join(",", templates));
      }
      t.append(template.append(">{"));
      // TODO Roth: verify verbs
      for (Verb v : top.isVerb() ? Arrays.asList(top.asVerb()) : bnf.verbs)
        if (!SpecialSymbols.$.equals(v))
          // NOTE $ method is built below
          t.append(computeMethod(jsm.trim(), v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && legalJumps.contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add(t.append("}").toString());
      apiTypeNames.add($);
      return computeTemplates(new TypeEncoding(jsm, origin, $, parent), jsm, unknownSolution, emptySolution);
    }
    private String computeMethod(JSM3 jsm, Verb v, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      if (jsm == JAMMED)
        return "";
      if (jsm == UNKNOWN)
        return "public " + namer.name(v) + " " + v.terminal.name() + "();";
      if (jsm.isEmpty())
        return "public E " + v.terminal.name() + "();";
      Symbol top = jsm.peek();
      JSM3 next = RLLPConcrete3.next(jsm, v, false);
      if (next == JAMMED)
        return "";
      return top.isVerb() ? //
          !top.asVerb().equals(v) ? //
              "" //
              : "public E " + v.terminal.name() + "();" //
          : "public " + solveType(computeType(next, v, unknownSolution, emptySolution), unknownSolution, emptySolution) //
              + " " + v.terminal.name() + "();";
    }
    private TypeEncoding computeTemplates(TypeEncoding $, JSM3 jsm, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      if ($.isRecursive())
        return $;
      assert jsm != UNKNOWN && !jsm.isEmpty();
      $.templates.add(computeType(jsm.pop(), SpecialSymbols.empty, unknownSolution, emptySolution, $));
      for (Verb v : jsm.legalJumps())
        if (!SpecialSymbols.$.equals(v))
          $.templates.add(computeType(jsm.jumpReminder(v), v, unknownSolution, emptySolution, $));
      return $;
    }
    private String solveType(TypeEncoding t, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      JSM3 jsm = t.jsm;
      assert jsm != JAMMED;
      if (jsm == UNKNOWN || jsm.isEmpty())
        return t.typeName;
      assert t.templates.size() > 0;
      if (t.isRecursive())
        return solveRecursiveType(t, unknownSolution, emptySolution);
      StringBuilder $ = new StringBuilder(namer.name(jsm.peek(), jsm.legalJumps())) //
          .append("<");
      List<String> templates = new ArrayList<>();
      for (TypeEncoding template : t.templates)
        templates.add(solveType(template, unknownSolution, emptySolution));
      return $.append(String.join(",", templates)).append(">").toString();
    }
    private String solveRecursiveType(TypeEncoding t, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      String n = namer.name(t);
      List<Verb> ri = t.root().recursionInterface();
      StringBuilder $ = new StringBuilder(n);
      if (!ri.isEmpty()) {
        $.append("<");
        List<String> templates = new ArrayList<>();
        for (Verb v : ri)
          if (!SpecialSymbols.$.equals(v))
            if (SpecialSymbols.empty.equals(v))
              templates.add("E");
            else
              templates.add(namer.name(v));
        $.append(String.join(",", templates)).append(">");
      }
      if (!seenRecs.contains(t)) {
        seenRecs.add(t);
        computeRecursiveType(t, ri, unknownSolution, emptySolution);
      }
      return $.toString();
    }
    private void computeRecursiveType(TypeEncoding te, List<Verb> ri, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      JSM3 jsm = te.jsm;
      assert jsm != JAMMED && jsm != UNKNOWN && !jsm.isEmpty();
      Symbol top = jsm.peek();
      List<Verb> legalJumps = jsm.legalJumps();
      String $ = namer.name(te);
      StringBuilder t = new StringBuilder("public interface ").append($);
      if (!ri.isEmpty()) {
        t.append("<");
        List<String> templates = new ArrayList<>();
        for (Verb v : ri)
          if (!SpecialSymbols.$.equals(v))
            if (SpecialSymbols.empty.equals(v))
              templates.add("E");
            else
              templates.add(namer.name(v));
        t.append(String.join(",", templates)).append(">");
      }
      t.append("{");
      // TODO Roth: verify verbs
      for (Verb v : top.isVerb() ? Arrays.asList(top.asVerb()) : bnf.verbs)
        if (!SpecialSymbols.$.equals(v))
          // NOTE $ method is built below
          // NOTE no trimming here
          t.append(computeMethod(jsm, v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && legalJumps.contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add(t.append("}").toString());
      apiTypeNames.add($);
      computeTemplates(new TypeEncoding(jsm, null, $, null), jsm, unknownSolution, emptySolution);
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ${") //
          .append(packagePath.toLowerCase() + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private void compute$$$Type() {
      List<String> superInterfaces = new ArrayList<>(apiTypeNames);
      superInterfaces.add(ASTNode.class.getCanonicalName());
      StringBuilder $ = new StringBuilder("private static class $$$ extends ") //
          .append(FluentAPIRecorder.class.getCanonicalName()).append(" implements ") //
          .append(String.join(",", superInterfaces)).append("{").append(String.join("", //
              bnf.verbs.stream().filter(v -> v != SpecialSymbols.$) //
                  .map(v -> "public $$$ " + v.terminal.name() + "(" //
                      + parametersEncoding(v.type) + "){recordTerminal(" //
                      + v.terminal.getClass().getCanonicalName() //
                      + "." + v.terminal.name() + (v.type.length == 0 ? "" : ",") //
                      + parameterNamesEncoding(v.type) + ");return this;}")
                  .collect(toList())));
      if (!bnf.isSubBNF)
        $.append("public ").append(packagePath + "." + astTopClass + "." + startSymbol.name()) //
            .append(" $(){return ast(" + packagePath + "." + astTopClass + ".class.getSimpleName());}");
      $.append("$$$(){super(new " + provider.getCanonicalName() + "().bnf().ebnf()");
      if (bnf.isSubBNF)
        $.append(".makeSubBNF(").append(startSymbol.getClass().getCanonicalName() + "." + startSymbol.name()).append(")");
      $.append(",\"" + packagePath + "\");}");
      apiTypes.add($.append("}").toString());
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add((type[i] instanceof NestedType ? ASTNode.class.getCanonicalName() : type[i].toParameterString()) + " arg" + (i + 1));
      return String.join(",", $);
    }
    private String parameterNamesEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add("arg" + (i + 1));
      return String.join(",", $);
    }
  }

  class TypeEncoding {
    final JSM3 jsm;
    final Verb origin;
    String typeName;
    final List<TypeEncoding> templates;
    final TypeEncoding parent;
    TypeEncoding recursionAncestor;
    private boolean recFlag;

    public TypeEncoding(JSM3 jsm, Verb origin, String typeName, TypeEncoding parent) {
      this.jsm = jsm;
      this.origin = origin;
      this.typeName = typeName;
      this.templates = new ArrayList<>();
      this.parent = parent;
    }
    boolean isRecursive() {
      if (recFlag)
        return recursionAncestor != null;
      recFlag = true;
      if (jsm == UNKNOWN || jsm.isEmpty())
        return false;
      for (TypeEncoding current = parent; current != null; current = current.parent)
        if (match(current)) {
          recursionAncestor = current;
          return true;
        }
      return false;
    }
    boolean match(TypeEncoding other) {
      return jsm.peek().equals(other.jsm.peek()) && jsm.legalJumps().equals(other.jsm.legalJumps());
    }
    @SuppressWarnings({ "null", "unused" }) TypeEncoding root() {
      TypeEncoding next = parent, current = this;
      while (next != null) {
        if (next == null)
          return current;
        current = next;
        next = next.parent;
      }
      return current;
    }
    List<Verb> recursionInterface() {
      List<Verb> $ = new ArrayList<>();
      if (isRecursive())
        return $;
      if (jsm == UNKNOWN) {
        $.add(origin);
        return $;
      }
      if (jsm.isEmpty()) {
        $.add(SpecialSymbols.empty);
        return $;
      }
      for (TypeEncoding t : templates)
        $.addAll(t.recursionInterface());
      return $;
    }
    @Override public int hashCode() {
      return jsm == UNKNOWN ? 1 : jsm.isEmpty() ? 3 : jsm.peek().hashCode();
    }
    @Override public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!(obj instanceof TypeEncoding))
        return false;
      TypeEncoding other = (TypeEncoding) obj;
      if (!match(other) || isRecursive() ^ other.isRecursive())
        return false;
      assert templates.size() == other.templates.size();
      for (int i = 0; i < templates.size(); ++i)
        if (!templates.get(i).equals(other.templates.get(i)))
          return false;
      return true;
    }
  }
}
