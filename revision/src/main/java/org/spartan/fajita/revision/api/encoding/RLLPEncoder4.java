package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM3.UNKNOWN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.JSM3;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ParameterType;

public class RLLPEncoder4 {
  public final String topClassName;
  public final String topClass;
  final NonTerminal startSymbol;
  final String astTopClass;
  final BNF bnf;
  final BNFAnalyzer analyzer;
  final String packagePath;
  final String topClassPath;
  final Namer namer;
  final List<String> apiTypes;
  final List<String> staticMethods;
  final Class<? extends Grammar> provider;

  public RLLPEncoder4(Fajita fajita, NonTerminal start, String astTopClass) {
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
  public RLLPEncoder4(Fajita fajita, Symbol nested, String astTopClass) {
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
    private final List<Verb> verbsOrder = new ArrayList<>(bnf.verbs);
    {
      Collections.sort(verbsOrder);
    }

    public String name(Verb v) {
      if (verbNames.containsKey(v))
        return verbNames.get(v);
      int x;
      terminalCounts.put(v.terminal,
          Integer.valueOf(x = terminalCounts.getOrDefault(v.terminal, Integer.valueOf(0)).intValue() + 1));
      String $ = v.terminal.name() + x;
      verbNames.put(v, $);
      return $;
    }
    public String name(Symbol s, Set<Verb> legalJumps) {
      StringBuilder $ = new StringBuilder(s.isNonTerminal() ? s.asNonTerminal().name() : name(s.asVerb())).append("$");
      for (Verb v : verbsOrder)
        if (legalJumps.contains(v))
          $.append(name(v));
      return $.toString();
    }
  }

  class MembersComputer {
    // TODO Roth: check whether sufficient recognition
    private final Map<Symbol, Set<Set<Verb>>> seenTypes = new LinkedHashMap<>();
    private final Set<String> apiTypeNames = new LinkedHashSet<>();
    private final Set<Verb> terminusVerbs = new HashSet<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      for (NonTerminal nt : bnf.nonTerminals)
        if (!SpecialSymbols.augmentedStartSymbol.equals(nt))
          compute(new JSM3(bnf, analyzer, nt), null, new LinkedHashSet<>(), v -> namer.name(v), v -> namer.name(v));
      compute$$$Type();
      computeStaticMethods();
      computeErrorType();
    }
    private String compute(JSM3 jsm, Verb origin, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      if (jsm == UNKNOWN)
        return unknownSolution.apply(origin);
      if (jsm.isEmpty()) {
        if (parentLegalJumps.contains(origin))
          return emptySolution.apply(origin);
        if (!bnf.isSubBNF)
          return "$";
        terminusVerbs.add(origin);
        return startSymbol.name();
      }
      Symbol top = jsm.peek();
      Set<Verb> legalJumps = jsm.legalJumps();
      String $n = namer.name(top, legalJumps);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $n;
      apiTypeNames.add($n);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(legalJumps);
      StringBuilder $ = new StringBuilder("public interface ").append($n);
      if (!legalJumps.isEmpty()) {
        List<String> templates = new ArrayList<>();
        for (Verb v : legalJumps)
          templates.add(namer.name(v));
        $.append("<").append(String.join(",", templates)).append(">");
      }
      $.append("{");
      for (Verb v : bnf.verbs)
        $.append(computeMethod(jsm, top, v, legalJumps, unknownSolution, emptySolution));
      if (top.isNonTerminal() && analyzer.followSetOf(top.asNonTerminal()).contains(SpecialSymbols.$))
        $.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add($.append("}").toString());
      return $n;
    }
    private String computeMethod(JSM3 jsm, Symbol top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, legalJumps, unknownSolution, emptySolution)
          : computeMethod(jsm, top.asVerb(), v, legalJumps, unknownSolution, emptySolution);
    }
    private String computeMethod(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      String $ = computeType(jsm, top, v, legalJumps, unknownSolution, emptySolution);
      return "".equals($) ? ""
          : new StringBuilder("public ").append($) //
              .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");").toString();
    }
    private String computeMethod(JSM3 jsm, Verb top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      return !top.equals(v) ? ""
          : new StringBuilder("public ") //
              .append(compute(jsm.pop(), v, legalJumps, unknownSolution, emptySolution)) //
              .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");").toString();
    }
    private void computeTemplates(JSMTypeComputer tc, JSM3 next, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      Set<Verb> nextLegalJumps = next.legalJumps();
      if (nextLegalJumps.isEmpty())
        return;
      JSM3 nextNext;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps) {
        String t = compute(nextNext = next.jump(nv), nv, parentLegalJumps, unknownSolution, emptySolution);
        if (!nextNext.isEmpty() && nextTop.equals(nextNext.peek()))
          tc.templates.add(new JSMTypeComputer(unknownSolution.apply(nv), next, tc));
        else {
          JSMTypeComputer ntc = new JSMTypeComputer(t, next, tc);
          tc.templates.add(ntc);
          if (!ntc.isRecursive())
            computeTemplates(ntc, nextNext, parentLegalJumps, unknownSolution, emptySolution);
        }
      }
    }
    public String computeType(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      List<Symbol> c = analyzer.llClosure(top, v);
      String typeName;
      JSM3 next;
      if (c == null) {
        if (!legalJumps.contains(v))
          return "";
        typeName = compute(next = jsm.jump(v), v, legalJumps, unknownSolution, unknownSolution);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, legalJumps, unknownSolution, unknownSolution);
      JSMTypeComputer tc = new JSMTypeComputer(typeName, jsm);
      if (next != UNKNOWN)
        computeTemplates(tc, next, legalJumps, x -> legalJumps.contains(x) ? unknownSolution.apply(x) : "ParseError",
            x -> legalJumps.contains(x) ? emptySolution.apply(x) : "ParseError");
      return computeType(tc);
    }
    private String computeType(JSMTypeComputer tc) {
      assert !tc.isRecursive(); // TODO Roth: deal with it
      StringBuilder $ = new StringBuilder();
      $.append(tc.typeName);
      if (!tc.templates.isEmpty()) {
        $.append("<");
        List<String> ts = new ArrayList<>();
        for (JSMTypeComputer c : tc.templates)
          ts.add(computeType(c));
        $.append(String.join(",", ts)).append(">");
      }
      return $.toString();
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add(type[i].toString() + " arg" + (i + 1));
      return String.join(",", $);
    }
    private String parameterNamesEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add("arg" + (i + 1));
      return String.join(",", $);
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ").append("$").append("{") //
          .append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private void compute$$$Type() {
      StringBuilder $ = new StringBuilder("private static class $$$ extends ") //
          .append(FluentAPIRecorder.class.getCanonicalName()).append(" implements ") //
          .append(String.join(",", apiTypeNames)).append("{").append(String.join("", //
              bnf.verbs.stream().filter(v -> v != SpecialSymbols.$) //
                  .map(v -> "public " + (!terminusVerbs.contains(v) ? "$$$" : startSymbol.name()) //
                      + " " + v.terminal.name() + "(" //
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
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      JSM3 jsm = new JSM3(bnf, analyzer, startSymbol);
      Symbol top = jsm.peek();
      assert top.isNonTerminal();
      Set<Verb> legalJumps = jsm.legalJumps();
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(jsm, top.asNonTerminal(), v, legalJumps, x -> "ParseError",
              x -> !bnf.isSubBNF ? "$" : startSymbol.name())) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
    }
    private void computeErrorType() {
      apiTypes.add("private interface ParseError{}");
    }
  }

  class JSMTypeComputer {
    public final String typeName;
    private final JSM3 jsm;
    private final JSMTypeComputer parent;
    public final List<JSMTypeComputer> templates;
    public boolean recFlag;

    public JSMTypeComputer(String typeName, JSM3 jsm) {
      this(typeName, jsm, null);
    }
    public JSMTypeComputer(String typeName, JSM3 jsm, JSMTypeComputer parent) {
      this.typeName = typeName;
      this.jsm = jsm;
      this.parent = parent;
      this.templates = new ArrayList<>();
    }
    public boolean isRecursive() {
      for (JSMTypeComputer current = parent; current != null; current = current.parent)
        // TODO Roth: comparison by jsm only?
        if (jsm.equals(current.jsm)) {
          current.recFlag = true;
          return true;
        }
      return false;
    }
  }
}
