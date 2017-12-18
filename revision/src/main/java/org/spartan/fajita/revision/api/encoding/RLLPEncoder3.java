package org.spartan.fajita.revision.api.encoding;

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

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.JSM3;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ParameterType;

public class RLLPEncoder3 {
  public static final String $_TYPE_NAME = "$";
  public final String topClassName;
  public final String topClass;
  final BNF bnf;
  final BNFAnalyzer analyzer;
  final String packagePath;
  final Namer namer;
  final List<String> apiTypes;
  final List<String> staticMethods;
  final List<String> innerTypes;

  public RLLPEncoder3(Fajita fajita) {
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    bnf = fajita.bnf();
    analyzer = new BNFAnalyzer(bnf);
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    innerTypes = new ArrayList<>();
    computeAPITypes();
    computeStaticMethods();
    computeInnerTypes();
    StringBuilder $ = new StringBuilder();
    $.append("package ").append(packagePath).append(";public class ").append(topClassName).append("{");
    for (String s : staticMethods)
      $.append(s);
    for (String s : apiTypes)
      $.append(s);
    for (String s : innerTypes)
      $.append(s);
    $.append("}");
    topClass = $.toString();
  }
  private void computeStaticMethods() {
    // TODO Auto-generated method stub
  }
  private void computeAPITypes() {
    new APITypesComputer().compute();
  }
  private void computeInnerTypes() {
    // TODO Auto-generated method stub
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

  class APITypesComputer {
    private final Map<Symbol, Set<Set<Verb>>> seenTypes = new HashMap<>();

    public void compute() {
      compute$Type();
      for (NonTerminal nt : bnf.nonTerminals)
        if (!SpecialSymbols.augmentedStartSymbol.equals(nt))
          compute(new JSM3(bnf, analyzer, nt), null, new HashSet<>());
    }
    private String compute(JSM3 jsm, Verb origin, Set<Verb> parentLegalJumps) {
      if (jsm == UNKNOWN)
        return namer.name(origin);
      if (jsm.isEmpty()) {
        return parentLegalJumps.contains(origin) ? namer.name(origin) : $_TYPE_NAME;
      }
      Symbol top = jsm.peek();
      Set<Verb> legalJumps = bnf.verbs.stream().filter(v -> jsm.jump(v) != JAMMED).collect(toSet());
      String $n = namer.name(top, legalJumps);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $n;
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
        $.append(computeMethod(jsm, top, v, legalJumps));
      apiTypes.add($.append("}").toString());
      return $n;
    }
    private String computeMethod(JSM3 jsm, Symbol top, Verb v, Set<Verb> legalJumps) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, legalJumps)
          : computeMethod(jsm, top.asVerb(), v, legalJumps);
    }
    private String computeMethod(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps) {
      StringBuilder $ = new StringBuilder("public ");
      List<Symbol> c = analyzer.llClosure(top, v);
      String typeName;
      JSM3 next;
      if (c == null) {
        if (!legalJumps.contains(v))
          return "";
        typeName = compute(next = jsm.jump(v), v, legalJumps);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, legalJumps);
      $.append(typeName);
      if (next != UNKNOWN)
        $.append(computeTemplates(next, legalJumps));
      return $.append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");").toString();
    }
    private String computeMethod(JSM3 jsm, Verb top, Verb v, Set<Verb> legalJumps) {
      return !top.equals(v) ? "" : compute(jsm.pop(), v, legalJumps);
    }
    private String computeTemplates(JSM3 next, Set<Verb> parentLegalJumps) {
      Set<Verb> nextLegalJumps = bnf.verbs.stream().filter(x -> next.jump(x) != JAMMED).collect(toSet());
      if (nextLegalJumps.isEmpty())
        return "";
      StringBuilder $ = new StringBuilder("<");
      List<String> templates = new ArrayList<>();
      JSM3 nextNext;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps) {
        String t = compute(nextNext = next.jump(nv), nv, parentLegalJumps);
        if (!nextNext.isEmpty() && nextTop.equals(nextNext.peek()))
          templates.add(namer.name(nv));
        else {
          // TODO Roth: check whether correct legal jumps
          t += computeTemplates(nextNext, parentLegalJumps);
          templates.add(t);
        }
      }
      String $$ = $.append(String.join(",", templates)).append(">").toString();
      return $$;
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (ParameterType t : type)
        $.add(t.toString()); // TODO Roth: probably wrong
      return String.join(",", $);
    }
    private void compute$Type() {
      StringBuilder $ = new StringBuilder("public interface ").append($_TYPE_NAME).append("{");
      // TODO Roth: fill in $ method
      apiTypes.add($.append("}").toString());
    }
  }
}
