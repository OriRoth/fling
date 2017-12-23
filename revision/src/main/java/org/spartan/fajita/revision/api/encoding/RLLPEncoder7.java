package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM3.UNKNOWN;
import static org.spartan.fajita.revision.parser.rll.JSM3.JAMMED;

import java.util.ArrayList;
import java.util.Collections;
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
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RLLPEncoder7 {
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

  public RLLPEncoder7(Fajita fajita, NonTerminal start, String astTopClass) {
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
  public RLLPEncoder7(Fajita fajita, Symbol nested, String astTopClass) {
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
    private final Map<JSMTypeComputer, String> seenRecs = new LinkedHashMap<>();
    private int recCount;
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
    public MethodSkeleton name(JSMTypeComputer recursiveRoot) {
      if (!seenRecs.containsKey(recursiveRoot))
        seenRecs.put(recursiveRoot, "REC$" + (++recCount) + "$" + recursiveRoot.typeName.asSimpleName());
      return new MethodSkeleton().append(seenRecs.get(recursiveRoot));
    }
  }

  class MembersComputer {
    // TODO Roth: check whether sufficient recognition
    private final Map<Symbol, Set<List<Verb>>> seenTypes = new LinkedHashMap<>();
    private final Set<String> apiTypeNames = new LinkedHashSet<>();
    private final Map<String, MethodSkeleton> apiTypeSkeletons = new LinkedHashMap<>();
    private final Set<JSMTypeComputer> seenRecs = new LinkedHashSet<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
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
    private MethodSkeleton compute(JSM3 jsm, Verb origin, List<Verb> baseLegalJumps, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      if (jsm == UNKNOWN)
        return $.appendUnknown(origin);
      if (jsm.isEmpty())
        return $.appendEmpty();
      Symbol top = jsm.peek();
      List<Verb> legalJumps = jsm.legalJumps(baseLegalJumps);
      String n = namer.name(top, legalJumps);
      $.append(n);
      if (seenTypes.containsKey(top) && seenTypes.get(top).contains(legalJumps))
        return $;
      apiTypeNames.add(n);
      seenTypes.putIfAbsent(top, new LinkedHashSet<>());
      seenTypes.get(top).add(legalJumps);
      MethodSkeleton t = new MethodSkeleton().append("public interface ").appendName(n);
      t.append("<E");
      if (!legalJumps.isEmpty()) {
        t.append(",");
        List<String> templates = new ArrayList<>();
        for (Verb v : legalJumps)
          templates.add(namer.name(v));
        t.append(String.join(",", templates));
      }
      t.append(">{");
      for (Verb v : bnf.verbs)
        t.append(computeMethod(jsm, top, v, legalJumps, unknownSolution, emptySolution));
      // TODO Roth: verify correctness
      if (!bnf.isSubBNF && top.isNonTerminal() && analyzer.followSetOf(top.asNonTerminal()).contains(SpecialSymbols.$)
          && legalJumps.contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypeSkeletons.put(n, t.append("}"));
      apiTypes.add(t.toString(unknownSolution, emptySolution));
      return $;
    }
    private MethodSkeleton computeMethod(JSM3 jsm, Symbol top, Verb v, List<Verb> baseLegalJumps,
        Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      return top.isNonTerminal() ? computeMethod(jsm, top.asNonTerminal(), v, baseLegalJumps, unknownSolution, emptySolution) //
          : computeMethod(jsm, top.asVerb(), v, baseLegalJumps, unknownSolution, emptySolution);
    }
    private MethodSkeleton computeMethod(JSM3 jsm, Verb top, Verb v, List<Verb> baseLegalJumps,
        Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      MethodSkeleton $ = new MethodSkeleton();
      return !top.equals(v) ? $
          : $.append("public ").append(compute(jsm.pop(), v, baseLegalJumps, unknownSolution, emptySolution)) //
              .append(" ").append(v.terminal.name()).append("(") //
              .append(parametersEncoding(v.type)).append(");");
    }
    private MethodSkeleton computeMethod(JSM3 jsm, NonTerminal top, Verb v, List<Verb> baseLegalJumps,
        Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      MethodSkeleton $ = computeType(computeType(jsm, top, v, baseLegalJumps, unknownSolution, emptySolution, null),
          unknownSolution);
      return $.isEmpty() ? $
          : new MethodSkeleton().append("public ").append($) //
              .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");");
    }
    public JSMTypeComputer computeType(JSM3 jsm, Symbol top, Verb v, List<Verb> baseLegalJumps,
        Function<Verb, String> unknownSolution, Supplier<String> emptySolution, JSMTypeComputer parent) {
      if (jsm.isEmpty())
        return new JSMTypeComputer(new MethodSkeleton().appendEmpty(), jsm, baseLegalJumps, null);
      List<Symbol> c = top.isVerb() ? !top.asVerb().equals(v) ? null : new ArrayList<>()
          : analyzer.llClosure(top.asNonTerminal(), v);
      MethodSkeleton typeName;
      JSM3 next;
      if (c == null) {
        if (!baseLegalJumps.contains(v))
          return null;
        typeName = compute(next = jsm.jump(v), v, baseLegalJumps, unknownSolution, emptySolution);
      } else
        typeName = compute(next = jsm.pop().pushAll(c), v, baseLegalJumps, unknownSolution, emptySolution);
      JSMTypeComputer tc = new JSMTypeComputer(typeName, jsm, jsm.legalJumps(baseLegalJumps), next, parent);
      if (next != UNKNOWN && !tc.isRecursive())
        computeTemplates(tc, next, baseLegalJumps, unknownSolution, emptySolution);
      return tc;
    }
    private void computeTemplates(JSMTypeComputer tc, JSM3 next, List<Verb> baseLegalJumps, Function<Verb, String> unknownSolution,
        Supplier<String> emptySolution) {
      List<Verb> nextLegalJumps = next.legalJumps(baseLegalJumps);
      if (nextLegalJumps.isEmpty())
        return;
      Symbol nextTop = next.peek();
      for (Verb nv : nextLegalJumps)
        tc.templates.add(computeType(next, nextTop, nv, nextLegalJumps, unknownSolution, emptySolution, tc));
    }
    private MethodSkeleton computeType(JSMTypeComputer tc, Function<Verb, String> unknownSolution) {
      MethodSkeleton $ = new MethodSkeleton();
      if (tc == null)
        return $;
      for (JSMTypeComputer c : tc.templates)
        computeType(c, unknownSolution);
      if (tc.isRecursive()) {
        JSMTypeComputer root = tc.root();
        tc.typeName = namer.name(root);
        seenRecs.add(root);
        tc.templates.clear();
        assert root.legalJumps.equals(tc.legalJumps);
        // NOTE can be improved to remove obsolete generics
        for (Verb v : root.legalJumps)
          tc.templates
              .add(new JSMTypeComputer(new MethodSkeleton().append(unknownSolution.apply(v)), UNKNOWN, new ArrayList<>(), JAMMED));
      }
      $.append(tc.typeName);
      if (!tc.hasTemplates)
        return $;
      List<MethodSkeleton> ts = new ArrayList<>();
      for (JSMTypeComputer c : tc.templates)
        ts.add(computeType(c, unknownSolution));
      $.append("<").appendEmpty();
      if (!ts.isEmpty()) {
        $.append(",");
        for (int i = 0; i < ts.size(); ++i) {
          $.append(ts.get(i));
          if (i != ts.size() - 1)
            $.append(",");
        }
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
      for (JSMTypeComputer t : seenRecs)
        computeRecs(t);
    }
    private boolean computeRecs(JSMTypeComputer current) {
      if (current.isRecursive()) {
        String n = current.typeName.asSimpleName();
        apiTypes.add(apiTypeSkeletons.get(current.root().typeName.asSimpleName()).toString(x -> namer.name(x), () -> "E", n));
        apiTypeNames.add(n);
        return true;
      }
      for (JSMTypeComputer c : current.templates)
        if (computeRecs(c))
          return true;
      return false;
    }
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      JSM3 base = new JSM3(bnf, analyzer, startSymbol);
      Symbol baseTop = base.peek();
      List<Symbol> c = baseTop.isVerb() ? !baseTop.asVerb().equals(v) ? null : new ArrayList<>()
          : analyzer.llClosure(baseTop.asNonTerminal(), v);
      if (c == null)
        return;
      JSM3 jsm = base.pop().pushAll(c);
      // TODO Roth: verify legalJumps
      List<Verb> legalJumps = jsm.legalJumps(new ArrayList<>(bnf.verbs));
      Symbol top = jsm.isEmpty() ? null : jsm.peek();
      computeType(jsm, top, v, legalJumps, x -> namer.name(x), () -> "E", null);
      Function<Verb, String> unknownSolution = x -> {
        throw new RuntimeException("Unreachable");
      };
      Supplier<String> emptySolution = !bnf.isSubBNF ? () -> "$" : () -> "$$$";
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(computeType(jsm, top, v, legalJumps, unknownSolution, emptySolution, null),
              !bnf.isSubBNF ? x -> "$" : x -> "$$$").toString(unknownSolution, emptySolution)) //
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
    MethodSkeleton appendEmpty() {
      bones.add(null);
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
    @SuppressWarnings("incomplete-switch") String toString(Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      StringBuilder $ = new StringBuilder();
      for (int i = 0; i < bones.size(); ++i)
        switch (types.get(i)) {
          case Text:
          case Name:
            $.append(bones.get(i));
            break;
          case Empty:
            $.append(emptySolution.get());
            break;
          case Unknown:
            $.append(unknownSolution.apply((Verb) bones.get(i)));
            break;
        }
      return $.toString();
    }
    @SuppressWarnings("incomplete-switch") String toString(Function<Verb, String> unknownSolution, Supplier<String> emptySolution,
        String nameReplacement) {
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
            $.append(emptySolution.get());
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
    final boolean hasTemplates;
    MethodSkeleton typeName;
    final JSM3 jsm;
    final List<Verb> legalJumps;
    JSM3 nextJSM;
    private final JSMTypeComputer parent;
    final List<JSMTypeComputer> templates;

    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, List<Verb> legalJumps, JSM3 nextJSM) {
      this(typeName, jsm, legalJumps, nextJSM, null, false);
    }
    public JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, List<Verb> legalJumps, JSM3 nextJSM, JSMTypeComputer parent) {
      this(typeName, jsm, legalJumps, nextJSM, parent, !nextJSM.isEmpty());
    }
    private JSMTypeComputer(MethodSkeleton typeName, JSM3 jsm, List<Verb> legalJumps, JSM3 nextJSM, JSMTypeComputer parent,
        boolean hasTemplates) {
      this.typeName = typeName;
      this.jsm = jsm;
      this.legalJumps = legalJumps;
      this.nextJSM = nextJSM;
      this.parent = parent;
      this.templates = new ArrayList<>();
      this.hasTemplates = hasTemplates;
    }
    public boolean isRecursive() {
      for (JSMTypeComputer current = parent; current != null; current = current.parent)
        if (jsm.peek().equals(current.jsm.peek()) && legalJumps.equals(current.legalJumps))
          return true;
      return false;
    }
    JSMTypeComputer getRecursiveAncestor() {
      for (JSMTypeComputer current = parent; current != null; current = current.parent)
        if (jsm.peek().equals(current.jsm.peek()) && legalJumps.equals(current.legalJumps))
          return current;
      return null;
    }
    public boolean hasRecursion() {
      return isRecursive() || templates.stream().anyMatch(JSMTypeComputer::hasRecursion);
    }
    public String getRecursionOrigin() {
      assert !jsm.isEmpty();
      return namer.name(jsm.peek(), legalJumps);
    }
    public JSMTypeComputer root() {
      JSMTypeComputer $ = this;
      while ($.parent != null)
        $ = $.parent;
      return $;
    }
    // NOTE above algorithm requires native implementations of
    // {@link Object#hashCode} and {@link Object#equals}
  }
}
