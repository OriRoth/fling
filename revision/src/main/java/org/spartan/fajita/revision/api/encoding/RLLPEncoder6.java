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

public class RLLPEncoder6 {
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

  public RLLPEncoder6(Fajita fajita, NonTerminal start, String astTopClass) {
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
  public RLLPEncoder6(Fajita fajita, Symbol nested, String astTopClass) {
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
    private Map<JSMTypeComputer, MethodSkeleton> recNames = new LinkedHashMap<>();
    private int recCount = 0;
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
    public String name(Symbol s, List<Verb> legalJumps) {
      StringBuilder $ = new StringBuilder(s.isNonTerminal() ? s.asNonTerminal().name() : name(s.asVerb())).append("$");
      for (Verb v : verbsOrder)
        if (legalJumps.contains(v))
          $.append(name(v));
      return $.toString();
    }
    public MethodSkeleton name(JSMTypeComputer recursive) {
      if (!recNames.containsKey(recursive))
        recNames.put(recursive, new MethodSkeleton().append("REC$" + (++recCount) + "$" + recursive.originalTypeName));
      return recNames.get(recursive);
    }
  }

  class MembersComputer {
    // TODO Roth: check whether sufficient recognition
    private final Map<Symbol, Set<List<Verb>>> seenTypes = new LinkedHashMap<>();
    private final Set<String> apiTypeNames = new LinkedHashSet<>();
    private final Map<String, MethodSkeleton> apiTypeSkeletons = new LinkedHashMap<>();
    private final Map<JSMTypeComputer, MethodSkeleton> recNames = new LinkedHashMap<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      for (NonTerminal nt : bnf.nonTerminals)
        if (!SpecialSymbols.augmentedStartSymbol.equals(nt))
          compute(new JSM3(bnf, analyzer, nt), null, new ArrayList<>(), v -> namer.name(v), v -> namer.name(v));
      computeStaticMethods();
      computeRecTypes();
      computeErrorType();
      compute$$$Type();
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ${") //
          .append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private MethodSkeleton compute(JSM3 jsm, Verb origin, List<Verb> parentLegalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      if (jsm == UNKNOWN)
        return $.appendUnknown(origin);
      if (jsm.isEmpty())
        return parentLegalJumps.contains(origin) ? $.appendEmpty(origin) //
            : $.append(!bnf.isSubBNF ? "$" : "$$$");
      Symbol top = jsm.peek();
      List<Verb> legalJumps = jsm.legalJumps();
      String n = namer.name(top, legalJumps);
      $.append(n);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $;
      apiTypeNames.add(n);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(legalJumps);
      MethodSkeleton t = new MethodSkeleton().append("public interface ").appendName(n);
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
    private MethodSkeleton computeMethod(JSM3 jsm, Symbol top, Verb v, List<Verb> legalJumps,
        Function<Verb, String> unknownSolution, Function<Verb, String> emptySolution) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, legalJumps, unknownSolution, emptySolution) //
          : computeMethod(jsm, top.asVerb(), v, legalJumps, unknownSolution, emptySolution);
    }
    private MethodSkeleton computeMethod(JSM3 jsm, Verb top, Verb v, List<Verb> legalJumps, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      return !top.equals(v) ? $
          : $.append("public ").append(compute(jsm.pop(), v, legalJumps, unknownSolution, emptySolution)) //
              .append(" ").append(v.terminal.name()).append("(") //
              .append(parametersEncoding(v.type)).append(");");
    }
    private MethodSkeleton computeMethod(JSM3 jsm, NonTerminal top, Verb v, List<Verb> legalJumps,
        Function<Verb, String> unknownSolution, Function<Verb, String> emptySolution) {
      MethodSkeleton $ = computeType(computeType(jsm, top, v, legalJumps, unknownSolution, emptySolution, null));
      return $.isEmpty() ? $
          : new MethodSkeleton().append("public ").append($) //
              .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");");
    }
    public JSMTypeComputer computeType(JSM3 jsm, NonTerminal top, Verb v, List<Verb> legalJumps,
        Function<Verb, String> unknownSolution, Function<Verb, String> emptySolution, JSMTypeComputer parent) {
      List<Symbol> c = analyzer.llClosure(top, v);
      MethodSkeleton typeName;
      JSM3 next;
      if (c == null) {
        if (!legalJumps.contains(v))
          return null;
        typeName = compute(next = jsm.jump(v), v, legalJumps, unknownSolution, unknownSolution);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, legalJumps, unknownSolution, unknownSolution);
      JSMTypeComputer tc = new JSMTypeComputer(typeName, jsm, next, parent);
      if (next != UNKNOWN)
        computeTemplates(tc, next, x -> legalJumps.contains(x) ? unknownSolution.apply(x) : "ParseError",
            x -> legalJumps.contains(x) ? emptySolution.apply(x) : "ParseError");
      return tc;
    }
    private void computeTemplates(JSMTypeComputer tc, JSM3 next, Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution) {
      List<Verb> nextLegalJumps = next.legalJumps();
      if (nextLegalJumps.isEmpty())
        return;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps) {
        if (nextTop.isVerb())
          tc.templates.add(new JSMTypeComputer(new MethodSkeleton().append(unknownSolution.apply(nv)), next, next.pop(), tc));
        else
          tc.templates.add(computeType(next, nextTop.asNonTerminal(), nv, nextLegalJumps, unknownSolution, emptySolution, tc));
      }
    }
    private MethodSkeleton computeType(JSMTypeComputer tc) {
      MethodSkeleton $ = new MethodSkeleton();
      if (tc == null)
        return $;
      for (JSMTypeComputer c : tc.templates)
        computeType(c);
      if (tc.isRecursive()) {
        tc.originalTypeName = tc.typeName.asSimpleName();
        tc.setTypeName(tc, namer.name(tc));
        tc.recFlag = false;
        if (!recNames.containsKey(tc))
          recNames.put(tc, namer.name(tc));
      }
      $.append(tc.typeName);
      if (tc.templates.isEmpty())
        return $;
      List<MethodSkeleton> ts = new ArrayList<>();
      for (JSMTypeComputer c : tc.templates)
        ts.add(computeType(c));
      $.append("<");
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
    private void computeRecTypes() {
      for (JSMTypeComputer t : recNames.keySet())
        computeRecType(t);
    }
    private void computeRecType(JSMTypeComputer t) {
      String n = t.typeName.asSimpleName();
      List<Verb> nextLegalJumps = t.nextJSM.legalJumps();
      // NOTE verbs of {@link JSM3#legalJumps()} should be deterministically ordered
      Function<Verb, String> solution = v -> !nextLegalJumps.contains(v) ? "ParseError" : namer.name(v), recursiveSolution = //
          v -> !nextLegalJumps.contains(v) ? "ParseError"
              : computeType(t.templates.get(nextLegalJumps.indexOf(v))).toString(solution, solution);
      apiTypes.add(apiTypeSkeletons.get(t.originalTypeName).toString(recursiveSolution, recursiveSolution, n));
      apiTypeNames.add(n);
    }
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      JSM3 jsm = new JSM3(bnf, analyzer, startSymbol);
      Symbol top = jsm.peek();
      assert top.isNonTerminal();
      List<Verb> legalJumps = jsm.legalJumps();
      Function<Verb, String> solution = !bnf.isSubBNF ? x -> "$" : x -> "$$$";
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(computeType(jsm, top.asNonTerminal(), v, legalJumps, solution, solution, null)).toString(solution,
              solution)) //
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
      Text, Empty, Unknown, Name
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
    MethodSkeleton appendName(String n) {
      bones.add(n);
      types.add(BoneType.Name);
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
          case Name:
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
    @SuppressWarnings("incomplete-switch") String toString(Function<Verb, String> unknownSolution,
        Function<Verb, String> emptySolution, String nameReplacement) {
      StringBuilder $ = new StringBuilder();
      for (int i = 0; i < bones.size(); ++i)
        switch (types.get(i)) {
          case Text:
            $.append(bones.get(i));
            break;
          case Name:
            $.append(nameReplacement);
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
    public String asSimpleName() {
      if (bones.size() != 1 && BoneType.Text.equals(types.get(0)))
        throw new NotImplementedException();
      return toString(null, null);
    }
    @Override public String toString() {
      throw new NotImplementedException();
    }
  }

  class JSMTypeComputer {
    MethodSkeleton typeName;
    final JSM3 jsm;
    JSM3 nextJSM;
    private final JSMTypeComputer parent;
    final List<JSMTypeComputer> templates;
    boolean recFlag;
    String originalTypeName;

    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, JSM3 nextJSM) {
      this(typeName, jsm, nextJSM, null);
    }
    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, JSM3 nextJSM, JSMTypeComputer parent) {
      this.typeName = typeName;
      this.jsm = jsm;
      this.nextJSM = nextJSM;
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
    public void setTypeName(JSMTypeComputer origin, MethodSkeleton newTypeName) {
      if (jsm.equals(origin.jsm))
        typeName = newTypeName;
      templates.stream().forEach(t -> t.setTypeName(origin, newTypeName));
    }
    @Override public int hashCode() {
      return jsm.hashCode() + templates.hashCode();
    }
    @Override public boolean equals(Object obj) {
      if (!(obj instanceof JSMTypeComputer))
        return false;
      JSMTypeComputer other = (JSMTypeComputer) obj;
      return jsm.equals(other.jsm) && templates.equals(other.templates);
    }
  }
}
