package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM11.JAMMED;
import static org.spartan.fajita.revision.parser.rll.JSM11.UNKNOWN;

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
import org.spartan.fajita.revision.parser.rll.JSM11;
import org.spartan.fajita.revision.parser.rll.RLLPConcrete3;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

public class RLLPEncoder11 {
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

  public RLLPEncoder11(Fajita fajita, NonTerminal start, String astTopClass) {
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
  public RLLPEncoder11(Fajita fajita, Symbol nested, String astTopClass) {
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
    public String name(Symbol s, Set<Verb> allLegalJumps) {
      if (s.isVerb())
        return s.asVerb().terminal.name() + "_";
      StringBuilder $ = new StringBuilder(s.isNonTerminal() ? s.asNonTerminal().name() : name(s.asVerb())).append("_");
      for (Verb v : bnf.verbs)
        if (allLegalJumps.contains(v))
          $.append(name(v));
      return $.toString();
    }
    public String name(TypeEncoding t) {
      if (recNames.containsKey(t))
        return recNames.get(t);
      recNames.put(t, "R" + (++recCount) + "_" + name(t.jsm.peek(), t.jsm.peekLegalJumps()));
      return recNames.get(t);
    }
  }

  class MembersComputer {
    private final Map<Symbol, Set<Set<Verb>>> seenTypes = new LinkedHashMap<>();
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
      Set<Verb> blj = new LinkedHashSet<>();
      blj.add(SpecialSymbols.$);
      JSM11 jsm = RLLPConcrete3.next(new JSM11(bnf, analyzer, startSymbol, blj), v);
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
    private TypeEncoding computeType(JSM11 jsm, Verb origin, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      return computeType(jsm, origin, unknownSolution, emptySolution, null);
    }
    private TypeEncoding computeType(JSM11 jsm, Verb origin, Function<Verb, String> unknownSolution, Supplier<String> emptySolution,
        TypeEncoding parent) {
      assert jsm != JAMMED;
      if (jsm == UNKNOWN)
        return new TypeEncoding(jsm, origin, unknownSolution.apply(origin), parent);
      if (jsm.isEmpty())
        return new TypeEncoding(jsm, origin, emptySolution.get(), parent);
      Symbol top = jsm.peek();
      Set<Verb> allLegalJumps = jsm.allLegalJumps();
      // NOTE contains an optimization for verb as stack top
      if (top.isVerb())
        allLegalJumps.clear();
      String $ = namer.name(top, allLegalJumps);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(allLegalJumps))
        return computeTemplates(new TypeEncoding(jsm, origin, $, parent), jsm, unknownSolution, emptySolution);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(allLegalJumps);
      StringBuilder t = new StringBuilder("public interface ").append($);
      StringBuilder template = new StringBuilder("<E");
      List<String> templates = new ArrayList<>();
      for (Verb v : allLegalJumps)
        if (!SpecialSymbols.$.equals(v))
          templates.add(namer.name(v));
      if (!templates.isEmpty()) {
        template.append(",");
        template.append(String.join(",", templates));
      }
      t.append(template.append(">{"));
      // NOTE further filtering is done in {@link RLLPEncoder#computeMethod}
      // NOTE contains an optimization for verb as stack top
      for (Verb v : top.isVerb() ? Arrays.asList(top.asVerb()) : bnf.verbs)
        // NOTE $ method is built below
        if (!SpecialSymbols.$.equals(v))
          t.append(computeMethod(jsm.trim(), v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && jsm.peekLegalJumps().contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add(t.append("}").toString());
      apiTypeNames.add($);
      return computeTemplates(new TypeEncoding(jsm, origin, $, parent), jsm, unknownSolution, emptySolution);
    }
    private String computeMethod(JSM11 jsm, Verb v, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      if (jsm == JAMMED)
        return "";
      if (jsm == UNKNOWN)
        return "public " + namer.name(v) + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      if (jsm.isEmpty())
        return "public E " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      Symbol top = jsm.peek();
      JSM11 next = RLLPConcrete3.next(jsm, v);
      if (next == JAMMED)
        return "";
      // NOTE contains an optimization for verb as stack top
      return top.isVerb() ? //
          !top.asVerb().equals(v) ? //
              "" //
              : "public E " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");" //
          : "public " + solveType(computeType(next, v, unknownSolution, emptySolution), unknownSolution, emptySolution) //
              + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
    }
    private TypeEncoding computeTemplates(TypeEncoding $, JSM11 jsm, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      if ($.isRecursive())
        return $;
      assert jsm != UNKNOWN && !jsm.isEmpty();
      $.templates.add(computeType(jsm.pop(), SpecialSymbols.empty, unknownSolution, emptySolution, $));
      // NOTE contains an optimization for verb as stack top
      if (jsm.peek().isNonTerminal())
        for (Verb v : jsm.allLegalJumps())
          if (!SpecialSymbols.$.equals(v))
            $.templates.add(computeType(jsm.jumpFirstOption(v), v, unknownSolution, emptySolution, $));
      return $;
    }
    private String solveType(TypeEncoding t, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      JSM11 jsm = t.jsm;
      assert jsm != JAMMED;
      if (jsm == UNKNOWN || jsm.isEmpty())
        return t.typeName;
      assert t.templates.size() > 0;
      if (t.isRecursive())
        return solveRecursiveType(t, unknownSolution, emptySolution);
      StringBuilder $ = new StringBuilder(namer.name(jsm.peek(), jsm.allLegalJumps())) //
          .append("<");
      List<String> templates = new ArrayList<>();
      for (TypeEncoding template : t.templates)
        templates.add(solveType(template, unknownSolution, emptySolution));
      return $.append(String.join(",", templates)).append(">").toString();
    }
    private String solveRecursiveType(TypeEncoding t, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      String n = namer.name(t);
      LinkedHashSet<Verb> ri = t.root().recursionInterface();
      StringBuilder $ = new StringBuilder(n);
      if (!ri.isEmpty()) {
        $.append("<");
        List<String> templates = new ArrayList<>();
        for (Verb v : ri)
          if (!SpecialSymbols.$.equals(v))
            if (SpecialSymbols.empty.equals(v))
              templates.add(emptySolution.get());
            else
              templates.add(unknownSolution.apply(v));
        $.append(String.join(",", templates)).append(">");
      }
      if (!seenRecs.contains(t)) {
        seenRecs.add(t);
        computeRecursiveType(t, ri, unknownSolution, emptySolution);
      }
      return $.toString();
    }
    private void computeRecursiveType(TypeEncoding te, LinkedHashSet<Verb> ri, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      JSM11 jsm = te.recursionAncestor.jsm;
      assert jsm != JAMMED && jsm != UNKNOWN && !jsm.isEmpty();
      Symbol top = jsm.peek();
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
      // NOTE further filtering is done in {@link RLLPEncoder#computeMethod}
      // NOTE contains an optimization for verb as stack top
      for (Verb v : top.isVerb() ? Arrays.asList(top.asVerb()) : bnf.verbs)
        if (!SpecialSymbols.$.equals(v))
          // NOTE $ method is built below
          // NOTE no trimming here
          t.append(computeMethod(jsm, v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && jsm.peekLegalJumps().contains(SpecialSymbols.$))
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
    final JSM11 jsm;
    final Verb origin;
    String typeName;
    final List<TypeEncoding> templates;
    final TypeEncoding parent;
    TypeEncoding recursionAncestor;
    private boolean recFlag;

    public TypeEncoding(JSM11 jsm, Verb origin, String typeName, TypeEncoding parent) {
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
      return jsm.equals(other.jsm);
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
    LinkedHashSet<Verb> recursionInterface() {
      LinkedHashSet<Verb> $ = new LinkedHashSet<>();
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
      // TODO Roth: optimize
      if (isRecursive())
        return recursionInterface().equals(other.recursionInterface());
      assert templates.size() == other.templates.size();
      for (int i = 0; i < templates.size(); ++i)
        if (!templates.get(i).equals(other.templates.get(i)))
          return false;
      return true;
    }
  }
}
