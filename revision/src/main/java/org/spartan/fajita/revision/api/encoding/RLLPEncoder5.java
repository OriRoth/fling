package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM3.UNKNOWN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.parser.rll.JSM3;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RLLPEncoder5 {
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

  public RLLPEncoder5(Fajita fajita, NonTerminal start, String astTopClass) {
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
  public RLLPEncoder5(Fajita fajita, Symbol nested, String astTopClass) {
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
    private final Map<String, MethodSkeleton> apiTypeSkeletons = new LinkedHashMap<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      for (NonTerminal nt : bnf.nonTerminals)
        if (!SpecialSymbols.augmentedStartSymbol.equals(nt))
          compute(new JSM3(bnf, analyzer, nt), null, new LinkedHashSet<>(), v -> namer.name(v), v -> namer.name(v));
      compute$$$Type();
      computeErrorType();
      computeStaticMethods();
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ${") //
          .append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private MethodSkeleton compute(JSM3 jsm, Verb origin, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      if (jsm == UNKNOWN)
        return $.appendUnknown(origin);
      if (jsm.isEmpty())
        return parentLegalJumps.contains(origin) ? $.appendEmpty(origin) //
            : $.append(!bnf.isSubBNF ? "$" : "$$$");
      Symbol top = jsm.peek();
      Set<Verb> legalJumps = jsm.legalJumps();
      String n = namer.name(top, legalJumps);
      $.append(n);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $;
      apiTypeNames.add(n);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(legalJumps);
      MethodSkeleton t = new MethodSkeleton().append("public interface ").append(n);
      if (!legalJumps.isEmpty()) {
        List<String> templates = new ArrayList<>();
        for (Verb v : legalJumps)
          templates.add(namer.name(v));
        t.append("<").append(String.join(",", templates)).append(">");
      }
      t.append("{");
      for (Verb v : bnf.verbs)
        t.append(computeMethod(jsm, top, v, legalJumps, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && top.isNonTerminal() && analyzer.followSetOf(top.asNonTerminal()).contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypeSkeletons.put(n, t.append("}"));
      apiTypes.add(t.toString(unknownSolution, emptySolution));
      return $;
    }
    private MethodSkeleton computeMethod(JSM3 jsm, Symbol top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, legalJumps, unknownSolution, emptySolution) //
          : computeMethod(jsm, top.asVerb(), v, legalJumps, unknownSolution, emptySolution);
    }
    private MethodSkeleton computeMethod(JSM3 jsm, Verb top, Verb v, Set<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      return !top.equals(v) ? $
          : $.append("public ").append(compute(jsm.pop(), v, legalJumps, unknownSolution, emptySolution)) //
              .append(" ").append(v.terminal.name()).append("(") //
              .append(parametersEncoding(v.type)).append(");");
    }
    private MethodSkeleton computeMethod(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps,
        Function<Verb, String> unknownSolution, Function<Verb, String> emptySolution) {
      MethodSkeleton $ = computeType(jsm, top, v, legalJumps, unknownSolution, emptySolution);
      return $.isEmpty() ? $
          : new MethodSkeleton().append("public ").append($) //
              .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");");
    }
    public MethodSkeleton computeType(JSM3 jsm, NonTerminal top, Verb v, Set<Verb> legalJumps,
        Function<Verb, String> unknownSolution, Function<Verb, String> emptySolution) {
      List<Symbol> c = analyzer.llClosure(top, v);
      MethodSkeleton typeName;
      JSM3 next;
      if (c == null) {
        if (!legalJumps.contains(v))
          return new MethodSkeleton();
        typeName = compute(next = jsm.jump(v), v, legalJumps, unknownSolution, unknownSolution);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, legalJumps, unknownSolution, unknownSolution);
      JSMTypeComputer tc = new JSMTypeComputer(typeName, jsm);
      if (next != UNKNOWN)
        computeTemplates(tc, next, legalJumps, x -> legalJumps.contains(x) ? unknownSolution.apply(x) : "ParseError",
            x -> legalJumps.contains(x) ? emptySolution.apply(x) : "ParseError");
      return computeType(tc);
    }
    private void computeTemplates(JSMTypeComputer tc, JSM3 next, Set<Verb> parentLegalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      Set<Verb> nextLegalJumps = next.legalJumps();
      if (nextLegalJumps.isEmpty())
        return;
      JSM3 nextNext;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps) {
        MethodSkeleton t = compute(nextNext = next.jump(nv), nv, parentLegalJumps, unknownSolution, emptySolution);
        if (!nextNext.isEmpty() && nextTop.equals(nextNext.peek()))
          tc.templates.add(new JSMTypeComputer(new MethodSkeleton().appendUnknown(nv), next, tc));
        else {
          JSMTypeComputer ntc = new JSMTypeComputer(t, next, tc);
          tc.templates.add(ntc);
          if (!ntc.checkRecursive())
            computeTemplates(ntc, nextNext, parentLegalJumps, unknownSolution, emptySolution);
        }
      }
    }
    private MethodSkeleton computeType(JSMTypeComputer tc) {
      assert !tc.isRecursive(); // TODO Roth: deal with it
      System.out.println(tc.isRecursive());
      MethodSkeleton $ = new MethodSkeleton();
      $.append(tc.typeName);
      if (tc.templates.isEmpty())
        return $;
      $.append("<");
      List<MethodSkeleton> ts = new ArrayList<>();
      for (JSMTypeComputer c : tc.templates)
        ts.add(computeType(c));
      for (int i = 0; i < ts.size(); ++i) {
        $.append(ts.get(i));
        if (i != ts.size() - 1)
          $.append(",");
      }
      $.append(">");
      return $;
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
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      JSM3 jsm = new JSM3(bnf, analyzer, startSymbol);
      Symbol top = jsm.peek();
      assert top.isNonTerminal();
      Set<Verb> legalJumps = jsm.legalJumps();
      Function<Verb, String> solution = !bnf.isSubBNF ? x -> "$" : x -> "$$$";
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(jsm, top.asNonTerminal(), v, legalJumps, solution, solution).toString(solution, solution)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
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
    private void computeErrorType() {
      apiTypes.add("private interface ParseError{}");
    }
  }

  static class MethodSkeleton {
    private enum BoneType {
      Text, Empty, Unknown
    }

    private List<Object> bones = new ArrayList<>();
    private List<BoneType> types = new ArrayList<>();

    MethodSkeleton append(String s) {
      bones.add(s);
      types.add(BoneType.Text);
      return this;
    }
    MethodSkeleton appendEmpty(Verb v) {
      bones.add(v);
      types.add(BoneType.Empty);
      return this;
    }
    MethodSkeleton appendUnknown(Verb v) {
      bones.add(v);
      types.add(BoneType.Unknown);
      return this;
    }
    boolean isEmpty() {
      return bones.isEmpty();
    }
    MethodSkeleton append(MethodSkeleton other) {
      bones.addAll(other.bones);
      types.addAll(other.types);
      return this;
    }
    @SuppressWarnings("incomplete-switch") String toString(Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      StringBuilder $ = new StringBuilder();
      for (int i = 0; i < bones.size(); ++i)
        switch (types.get(i)) {
          case Text:
            $.append(bones.get(i));
            break;
          case Empty:
            $.append(emptySolution.apply((Verb) bones.get(i)));
            break;
          case Unknown:
            $.append(unknownSolution.apply((Verb) bones.get(i)));
            break;
        }
      return $.toString();
    }
    @Override public String toString() {
      throw new NotImplementedException();
    }
  }

  class JSMTypeComputer {
    public final MethodSkeleton typeName;
    private final JSM3 jsm;
    private final JSMTypeComputer parent;
    public final List<JSMTypeComputer> templates;
    private boolean recFlag;

    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm) {
      this(typeName, jsm, null);
    }
    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, JSMTypeComputer parent) {
      this.typeName = typeName;
      this.jsm = jsm;
      this.parent = parent;
      this.templates = new ArrayList<>();
    }
    public boolean checkRecursive() {
      for (JSMTypeComputer current = parent; current != null; current = current.parent)
        // TODO Roth: comparison by jsm only?
        if (jsm.equals(current.jsm)) {
          current.recFlag = true;
          return true;
        }
      return false;
    }
    public boolean isRecursive() {
      return recFlag;
    }
    public boolean hasRecursion() {
      return recFlag || templates.stream().anyMatch(JSMTypeComputer::hasRecursion);
    }
  }
}
