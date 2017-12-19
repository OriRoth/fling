package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.spartan.fajita.revision.parser.rll.JSM3.JAMMED;
import static org.spartan.fajita.revision.parser.rll.JSM3.UNKNOWN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.ast.encoding.JamoosClassesRenderer;
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

public class RLLPEncoder3 {
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

  public RLLPEncoder3(Fajita fajita, NonTerminal start) {
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = start;
    provider = fajita.provider;
    bnf = fajita.bnf();
    astTopClass = JamoosClassesRenderer.topClassName(bnf);
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
  public RLLPEncoder3(Fajita fajita, Symbol nested) {
    assert nested.isNonTerminal() || nested.isExtendible();
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = nested.head().asNonTerminal();
    provider = fajita.provider;
    bnf = fajita.bnf().getSubBNF(startSymbol);
    astTopClass = JamoosClassesRenderer.topClassName(bnf);
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
    private final Map<Verb, String> verbNames = new HashMap<>();
    private final Map<Terminal, Integer> terminalCounts = new HashMap<>();
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
    private final Map<Symbol, Set<Set<Verb>>> seenTypes = new HashMap<>();
    private final Set<String> apiTypeNames = new HashSet<>();

    public void compute() {
      compute$Type();
      for (NonTerminal nt : bnf.nonTerminals)
        if (!SpecialSymbols.augmentedStartSymbol.equals(nt))
          compute(new JSM3(bnf, analyzer, nt), null, new HashSet<>(), v -> namer.name(v));
      compute$$$Type();
      computeStaticMethods();
      computeErrorType();
    }
    private String compute(JSM3 jsm, Verb origin, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution) {
      if (jsm == UNKNOWN)
        return unknownSolution.apply(origin);
      if (jsm.isEmpty()) {
        return parentLegalJumps.contains(origin) ? namer.name(origin) : "$";
      }
      Symbol top = jsm.peek();
      Set<Verb> legalJumps = bnf.verbs.stream().filter(v -> jsm.jump(v) != JAMMED).collect(toSet());
      String $n = namer.name(top, legalJumps);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $n;
      apiTypeNames.add($n);
      seenTypes.putIfAbsent(top, new HashSet<>());
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
        $.append(computeMethod(jsm, top, v, legalJumps, unknownSolution));
      apiTypes.add($.append("}").toString());
      return $n;
    }
    private String computeMethod(JSM3 jsm, Symbol top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, legalJumps, unknownSolution)
          : computeMethod(jsm, top.asVerb(), v, legalJumps, unknownSolution);
    }
    private String computeMethod(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution) {
      StringBuilder $ = new StringBuilder("public ");
      List<Symbol> c = analyzer.llClosure(top, v);
      String typeName;
      JSM3 next;
      if (c == null) {
        if (!legalJumps.contains(v))
          return "";
        typeName = compute(next = jsm.jump(v), v, legalJumps, unknownSolution);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, legalJumps, unknownSolution);
      $.append(typeName);
      if (next != UNKNOWN)
        $.append(computeTemplates(next, legalJumps, unknownSolution));
      return $.append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");").toString();
    }
    private String computeMethod(JSM3 jsm, Verb top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution) {
      return !top.equals(v) ? "" : compute(jsm.pop(), v, legalJumps, unknownSolution);
    }
    private String computeTemplates(JSM3 next, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution) {
      Set<Verb> nextLegalJumps = bnf.verbs.stream().filter(x -> next.jump(x) != JAMMED).collect(toSet());
      if (nextLegalJumps.isEmpty())
        return "";
      StringBuilder $ = new StringBuilder("<");
      List<String> templates = new ArrayList<>();
      JSM3 nextNext;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps) {
        String t = compute(nextNext = next.jump(nv), nv, parentLegalJumps, unknownSolution);
        if (!nextNext.isEmpty() && nextTop.equals(nextNext.peek()))
          templates.add(unknownSolution.apply(nv));
        else {
          // TODO Roth: check whether correct legal jumps
          t += computeTemplates(nextNext, parentLegalJumps, unknownSolution);
          templates.add(t);
        }
      }
      String $$ = $.append(String.join(",", templates)).append(">").toString();
      return $$;
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        // TODO Roth: probably wrong
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
                  .map(v -> "public $$$ " + v.terminal.name() + "(" //
                      + parametersEncoding(v.type) + "){recordTerminal(" //
                      + v.terminal.getClass().getCanonicalName() //
                      + "." + v.terminal.name() + (v.type.length == 0 ? "" : ",") //
                      + parameterNamesEncoding(v.type) + ");return this;}")
                  .collect(toList()))) //
          .append("public ").append(packagePath + "." + astTopClass + "." + startSymbol.name()) //
          .append(" $(){return ast(" + packagePath + "." + astTopClass + ".class.getSimpleName());}") //
          .append("$$$(){super(new " + provider.getCanonicalName() + "().bnf().ebnf()");
      if (bnf.isSubBNF)
        $.append(".makeSubBNF(").append(startSymbol.getClass().getCanonicalName() + "." + startSymbol.name()).append("");
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
      Set<Verb> legalJumps = bnf.verbs.stream().filter(x -> jsm.jump(x) != JAMMED).collect(toSet());
      staticMethods.add(new StringBuilder("public static ") //
          .append(staticMethodTemplate(jsm, top, legalJumps, v, x -> "ParseError")) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
    }
    private String staticMethodTemplate(JSM3 jsm, Symbol top, Set<Verb> legalJumps, Verb origin,
        Function<Verb, String> unknownSolution) {
      JSM3 next;
      if (top.isVerb())
        next = jsm.pop();
      else {
        List<Symbol> c = analyzer.llClosure(top.asNonTerminal(), origin);
        if (c == null) {
          if (!legalJumps.contains(origin))
            assert false : "reachable?";
          next = jsm.jump(origin);
        } else
          next = jsm.pop().pushAll(c);
      }
      return namer.name(next.peek(), bnf.verbs.stream().filter(x -> next.jump(x) != JAMMED).collect(toSet())) //
          + computeTemplates(next, legalJumps, unknownSolution);
    }
    private void computeErrorType() {
      apiTypes.add("private interface ParseError{}");
    }
  }
}
