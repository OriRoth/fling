package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.util.Greek.chi;
import static org.spartan.fajita.revision.util.Greek.gamma;
import static org.spartan.fajita.revision.util.Greek.koppa;
import static org.spartan.fajita.revision.util.Greek.omega;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.parser.rlr.LRP;
import org.spartan.fajita.revision.parser.rlr.LRP.Action;
import org.spartan.fajita.revision.parser.rlr.LRP.Item;
import org.spartan.fajita.revision.parser.rlr.RLRA;
import org.spartan.fajita.revision.parser.rlr.RLRA.J;
import org.spartan.fajita.revision.symbols.Constants;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.util.TriFunction;

public class RLRAEncoder {
  public final String topClassName;
  public final String topClass;
  final NonTerminal startSymbol;
  final BNF bnf;
  final Set<Verb> verbs;
  final RLRA empty;
  final LRP lrp;
  public final String packagePath;
  final String topClassPath;
  final Namer namer;
  final List<String> apiTypes;
  final List<String> staticMethods;
  final Class<? extends Grammar> provider;
  final String templates;

  public RLRAEncoder(Fajita fajita, NonTerminal start) {
    topClassName = fajita.apiName;
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = start;
    provider = fajita.provider;
    bnf = fajita.bnf();
    verbs = new LinkedHashSet<>();
    verbs.addAll(bnf.verbs);
    verbs.add(Constants.$);
    empty = new RLRA(verbs.stream().map(x -> x.asTerminal()).collect(Collectors.toSet()), bnf.nonTerminals, bnf.rules(),
        bnf.startSymbols);
    lrp = empty.lrp;
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    templates = computeTemplates();
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
  public RLRAEncoder(Fajita fajita, Symbol nested) {
    assert nested.isNonTerminal() || nested.isExtendible();
    topClassName = nested.name();
    packagePath = fajita.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = nested.head().asNonTerminal();
    provider = fajita.provider;
    bnf = fajita.bnf().getSubBNF(startSymbol);
    verbs = new LinkedHashSet<>();
    verbs.addAll(bnf.verbs);
    verbs.add(Constants.$);
    empty = new RLRA(verbs.stream().map(x -> x.asTerminal()).collect(Collectors.toSet()), bnf.nonTerminals, bnf.rules(),
        bnf.startSymbols);
    lrp = empty.lrp;
    namer = new Namer();
    apiTypes = new ArrayList<>();
    staticMethods = new ArrayList<>();
    templates = computeTemplates();
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
    private final Map<Set<Item>, String> stateNames = new LinkedHashMap<>();
    private int stateCount = 0;

    public String name(Verb v) {
      if (Constants.$.equals(v))
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
    public String name(Set<Item> q) {
      if (stateNames.containsKey(q))
        return stateNames.get(q);
      String $ = koppa + stateCount++;
      stateNames.put(q, $);
      return $;
    }
    public String name(J j) {
      assert j != null && !j.isAccept && !j.isUnknown();
      return gamma + String.join("", j.toPush.stream().map(q -> name(q)).collect(toList()));
    }
    public String name(NonTerminal nt, Verb v, int d) {
      return nt.name() + "_" + name(v) + "_" + d;
    }
  }

  class MembersComputer {
    private final Set<String> apiTypeNames = new LinkedHashSet<>();

    public void compute() {
      computeMiscTypes();
      computeStaticMethods();
      compute$$$Type();
    }
    private void computeStaticMethods() {
      for (Verb v : lrp.first(startSymbol).stream().map(t -> t.asVerb()).collect(toList()))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      RLRA rlra = empty.shift(lrp.q0);
      Action a = lrp.action(lrp.q0, v);
      computeType(a, rlra, v, (nt, vv, d) -> namer.name(nt, vv, d.intValue()));
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(a, rlra, v, (nt, vv, d) -> chi)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){return null;}") //
          .toString());
    }
    private String computeType(Action a, RLRA rlra, Verb originV, TriFunction<NonTerminal, Verb, Integer, String> unknownSolution) {
      if (a.isReject())
        return chi;
      if (a.isAccept())
        return omega;
      String name = null;
      if (a.isShift()) {
        Set<Item> q = a.state;
        name = namer.name(q);
        rlra = rlra.shift(q);
      } else if (a.isReduce()) {
        DerivationRule r = a.rule;
        J j = rlra.reduce(originV, r);
        if (j == null)
          return chi;
        if (j.isAccept)
          return omega;
        if (j.isUnknown())
          return namer.name(r.lhs, originV, j.unknownDepth);
        rlra = rlra.jump(r);
        name = j.toPush.isEmpty() ? namer.name(rlra.peek()) : namer.name(j);
      }
      if (apiTypes.contains(name))
        return name + computeTemplates(rlra, unknownSolution);
      StringBuilder $ = new StringBuilder("public interface ").append(name).append(templates).append("{");
      for (Verb v : verbs) {
        Action aa = lrp.action(rlra.peek(), v);
        $.append("public ").append(computeType(aa, rlra, v, unknownSolution)).append("(") //
            .append(parameterNamesEncoding(v.type)).append(");");
      }
      apiTypes.add($.toString());
      apiTypeNames.add(name);
      return name + computeTemplates(rlra, unknownSolution);
    }
    private String computeTemplates(RLRA rlra, TriFunction<NonTerminal, Verb, Integer, String> unknownSolution) {
      assert JAMMED != jsm && UNKNOWN != jsm;
      StringBuilder $ = new StringBuilder("<");
      List<String> templates = new ArrayList<>();
      if (jsm.isEmpty()) {
        $.append(emptySolution.get());
        for (Verb v : jsm.baseLegalJumps())
          if (!Constants.$.equals(v))
            templates.add(unknownSolution.apply(v));
        if (!templates.isEmpty())
          $.append(",").append(String.join(",", templates));
        return $.append(">").toString();
      }
      // NOTE can send null as origin verb as sent J cannot be JUNKNOWN
      $.append(computeType(J.of(jsm), null, unknownSolution, emptySolution));
      for (Verb v : jsm.trim().baseLegalJumps())
        if (!Constants.$.equals(v))
          templates.add(computeType(jsm.jjumpFirstAvailable(v), v, unknownSolution, emptySolution));
      if (!templates.isEmpty())
        $.append(",").append(String.join(",", templates));
      return $.append(">").toString();
    }
    private String computeMethod(JSM jsm, Verb v, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      if (jsm == JAMMED)
        return "";
      if (jsm == UNKNOWN)
        return "public " + namer.name(v) + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      if (jsm.isEmpty())
        return "public ϱ " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      Symbol top = jsm.peek();
      J jnext = RLLPConcrete.jnext(jsm, v);
      if (JJAMMED == jnext)
        return "";
      return top.isVerb() && !top.asVerb().equals(v) ? "" //
          : "public " + computeType(jnext, v, unknownSolution, emptySolution) //
              + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
    }
    private void computeMiscTypes() {
      apiTypes.add("public interface ${" + omega + " $();}");
      apiTypeNames.add("$");
      apiTypes.add("public interface " + omega + "{void " + omega + "();}");
      apiTypeNames.add(omega);
      apiTypes.add("private interface " + chi + "{}");
      apiTypeNames.add(chi);
    }
    private void compute$$$Type() {
      // List<String> superInterfaces = new ArrayList<>(apiTypeNames);
      // superInterfaces.add(ASTNode.class.getCanonicalName());
      // StringBuilder $ = new StringBuilder("private static class $$$ extends ") //
      // .append(FluentAPIRecorder.class.getCanonicalName()).append(" implements ") //
      // .append(String.join(",", superInterfaces)).append("{").append(String.join("",
      // //
      // bnf.verbs.stream().filter(v -> v != Constants.$) //
      // .map(v -> "public $$$ " + v.terminal.name() + "(" //
      // + parametersEncoding(v.type) + "){recordTerminal(" //
      // + v.terminal.getClass().getCanonicalName() //
      // + "." + v.terminal.name() + (v.type.length == 0 ? "" : ",") //
      // + parameterNamesEncoding(v.type) + ");return this;}")
      // .collect(toList())));
      // if (!bnf.isSubBNF)
      // $.append("public ").append(packagePath + "." + astTopClass + "." +
      // startSymbol.name()) //
      // .append(" $(){return ast(" + packagePath + "." + astTopClass +
      // ".class.getSimpleName());}");
      // $.append("$$$(){super(new " + provider.getCanonicalName() +
      // "().bnf().ebnf()");
      // if (bnf.isSubBNF)
      // $.append(".makeSubBNF(").append(startSymbol.getClass().getCanonicalName() +
      // "." + startSymbol.name()).append(")");
      // $.append(",\"" + packagePath + "\");}");
      // apiTypes.add($.append("}").toString());
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

  private String computeTemplates() {
    List<String> ts = new ArrayList<>();
    for (NonTerminal nt : bnf.nonTerminals)
      for (Verb v : verbs)
        for (int i = 0; i < bnf.f; ++i)
          ts.add(namer.name(nt, v, i));
    return "<" + String.join(",", ts) + ">";
  }
}
