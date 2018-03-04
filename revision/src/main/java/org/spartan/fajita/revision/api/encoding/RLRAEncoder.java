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
import java.util.stream.Collectors;

import org.spartan.fajita.revision.api.Fajita;
import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.bnf.DerivationRule;
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
      computeConstantTypes();
      computeStaticMethods();
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
          .append(computeType(a, rlra, v, //
              (nt, vv, d) -> Constants.$.equals(vv) && d.intValue() == 1 ? omega : chi)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){return null;}") //
          .toString());
    }
    private String computeType(Action a, RLRA dirty, Verb originV,
        TriFunction<NonTerminal, Verb, Integer, String> unknownSolution) {
      RLRA clean = null;
      RLRA templateReady = null;
      if (a.isReject())
        return chi;
      if (a.isAccept())
        return omega;
      String name = null;
      if (a.isShift()) {
        Set<Item> q = a.state;
        name = namer.name(q);
        dirty = dirty.shift(q);
        clean = dirty.trim();
        templateReady = dirty;
      } else if (a.isReduce()) {
        DerivationRule r = a.rule;
        J j = dirty.reduce(originV, r);
        if (j == null)
          return chi;
        if (j.isAccept)
          return omega;
        if (j.isUnknown())
          return unknownSolution.apply(r.lhs, originV, Integer.valueOf(j.unknownDepth));
        dirty = dirty.jump(r);
        List<Set<Item>> toPush = new ArrayList<>(j.toPush);
        toPush.add(0, dirty.peek());
        dirty = dirty.pop();
        j = J.record(j.toPop, toPush);
        name = namer.name(j);
        dirty = dirty.jump(j);
        clean = empty;
        for (Set<Item> q : toPush)
          clean = clean.shift(q);
        templateReady = dirty;
        for (int i = 0; i < toPush.size(); ++i)
          templateReady = templateReady.pop();
      }
      if (apiTypeNames.contains(name))
        return name + computeTemplates(templateReady, unknownSolution);
      apiTypeNames.add(name);
      StringBuilder $ = new StringBuilder("public interface ").append(name).append(templates).append("{");
      for (Verb v : verbs) {
        Action aa = lrp.action(dirty.peek(), v);
        $.append("public ").append(computeType(aa, clean, v, unknownSolution)).append(" ") //
            .append(v.terminal.name()).append("(").append(parametersEncoding(v.type)).append(");");
      }
      apiTypes.add($.append("}").toString());
      return name + computeTemplates(templateReady, unknownSolution);
    }
    private String computeTemplates(RLRA rlra, TriFunction<NonTerminal, Verb, Integer, String> unknownSolution) {
      StringBuilder $ = new StringBuilder("<");
      @SuppressWarnings("hiding") List<String> templates = new ArrayList<>();
      rlra = rlra.pop();
      for (int i = 1; i <= bnf.f; ++i) {
        for (NonTerminal nt : bnf.nonTerminals)
          if (!Constants.augmentedStartSymbol.equals(nt))
            for (Verb v : verbs) {
              if (rlra.size() == 0) {
                templates.add(unknownSolution.apply(nt, v, Integer.valueOf(i)));
                continue;
              }
              Action a = lrp.action(rlra.peek(), v);
              if (a.isReject() || a.isShift())
                templates.add(chi);
              else if (a.isAccept())
                templates.add(omega);
              else {
                assert a.isReduce();
                DerivationRule r = a.rule;
                if (!r.lhs.equals(nt))
                  templates.add(chi);
                else {
                  J j = rlra.reduce(v, r);
                  if (j == null)
                    templates.add(chi);
                  else if (j.isAccept)
                    templates.add(omega);
                  else if (j.isUnknown())
                    templates.add(unknownSolution.apply(nt, v, Integer.valueOf(j.unknownDepth)));
                  else
                    templates.add(computeType(a, rlra, v, unknownSolution));
                }
              }
            }
        if (rlra.size() > 0)
          rlra = rlra.pop();
      }
      return $.append(String.join(",", templates)).append(">").toString();
    }
    private void computeConstantTypes() {
      apiTypes.add("public interface ${" + omega + " $();}");
      apiTypeNames.add("$");
      apiTypes.add("public interface " + omega + "{void " + omega + "();}");
      apiTypeNames.add(omega);
      apiTypes.add("private interface " + chi + "{}");
      apiTypeNames.add(chi);
    }
    private String parametersEncoding(ParameterType[] type) {
      List<String> $ = new ArrayList<>();
      for (int i = 0; i < type.length; ++i)
        $.add((type[i] instanceof NestedType ? Object.class.getCanonicalName() : type[i].toParameterString()) + " arg" + (i + 1));
      return String.join(",", $);
    }
  }

  private String computeTemplates() {
    List<String> ts = new ArrayList<>();
    for (int i = 1; i <= bnf.f; ++i)
      for (NonTerminal nt : bnf.nonTerminals)
        if (!Constants.augmentedStartSymbol.equals(nt))
          for (Verb v : verbs)
            ts.add(namer.name(nt, v, i));
    return "<" + String.join(",", ts) + ">";
  }
}
