package roth.ori.fling.api.encoding;

import static java.util.stream.Collectors.toList;
import static roth.ori.fling.parser.rll.JSM.JAMMED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import roth.ori.fling.api.Fling;
import roth.ori.fling.bnf.BNF;
import roth.ori.fling.export.ASTNode;
import roth.ori.fling.export.FluentAPIRecorder;
import roth.ori.fling.export.Grammar;
import roth.ori.fling.parser.ll.BNFAnalyzer;
import roth.ori.fling.parser.rll.JSM;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.SpecialSymbols;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.Terminal;
import roth.ori.fling.symbols.Verb;
import roth.ori.fling.symbols.types.NestedType;
import roth.ori.fling.symbols.types.ParameterType;

public class RLLPEncoder {
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

  public RLLPEncoder(Fling fling, NonTerminal start, String astTopClass) {
    topClassName = fling.apiName;
    packagePath = fling.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = start;
    provider = fling.provider;
    bnf = fling.bnf();
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
  public RLLPEncoder(Fling fling, Symbol nested, String astTopClass) {
    assert nested.isNonTerminal() || nested.isExtendible();
    topClassName = nested.name();
    packagePath = fling.packagePath;
    topClassPath = packagePath + "." + topClassName;
    startSymbol = nested.head().asNonTerminal();
    provider = fling.provider;
    bnf = fling.bnf().getSubBNF(startSymbol);
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
    private String name(Symbol s) {
      return s.isVerb() ? name(s.asVerb()) : s.asNonTerminal().name();
    }
    private String names(Collection<? extends Symbol> ss) {
      return String.join("", ss.stream().map(s -> name(s)).collect(toList()));
    }
    public String name(Collection<Symbol> toPush, Set<Verb> legalJumps) {
      assert toPush != null && legalJumps != null;
      return new StringBuilder("π_").append(names(toPush)).append("_").append(names(legalJumps)).toString();
    }
  }

  class MembersComputer {
    private final Set<String> apiTypeNames = new LinkedHashSet<>();

    public void compute() {
      if (!bnf.isSubBNF)
        compute$Type();
      computeStaticMethods();
      computeΛType();
    }
    private void computeStaticMethods() {
      for (Verb v : analyzer.firstSetOf(startSymbol))
        computeStaticMethod(v);
    }
    private void computeStaticMethod(Verb v) {
      List<Symbol> toPush = new ArrayList<>();
      toPush.add(startSymbol);
      Set<Verb> baseLegalJumps = JSM.initialBaseLegalJumps();
      computeType(toPush, baseLegalJumps, v, x -> namer.name(x));
      // NOTE should be applicable only for $ jumps
      Function<Verb, String> unknownSolution = !bnf.isSubBNF ? x -> {
        assert SpecialSymbols.$.equals(x);
        return "$";
      } : x -> {
        assert SpecialSymbols.$.equals(x);
        return "Λ";
      };
      staticMethods.add(new StringBuilder("public static ") //
          .append(computeType(toPush, baseLegalJumps, v, unknownSolution)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("Λ Λ = new Λ();Λ.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return Λ;}") //
          .toString());
    }
    private String computeType(List<Symbol> toPush, Set<Verb> baseLegalJumps, Verb origin, Function<Verb, String> unknownSolution) {
      if (toPush.isEmpty()) {
        assert baseLegalJumps.contains(origin);
        return unknownSolution.apply(origin);
      }
      Symbol firstToPush = toPush.get(toPush.size() - 1);
      List<Symbol> restToPush = new ArrayList<>(toPush);
      restToPush.remove(toPush.size() - 1);
      if (analyzer.firstSetOf(firstToPush).contains(origin)) {
        List<Symbol> newToPush = firstToPush.isTerminal() ? new ArrayList<>()
            : analyzer.llClosure(firstToPush.asNonTerminal(), origin);
        Set<Verb> newBaseLegalJumps = new JSM(bnf, analyzer, baseLegalJumps).push(restToPush).trim().baseLegalJumps();
        String typeName = namer.name(newToPush, newBaseLegalJumps);
        if (!apiTypeNames.contains(typeName)) {
          apiTypeNames.add(typeName);
          createType(typeName, newToPush, newBaseLegalJumps, unknownSolution);
        }
        if (newBaseLegalJumps.isEmpty() || newBaseLegalJumps.size() == 1 && newBaseLegalJumps.contains(SpecialSymbols.$))
          return typeName;
        return typeName + "<" + String.join(",", newBaseLegalJumps.stream().filter(verb -> verb != SpecialSymbols.$)
            .map(verb -> computeType(restToPush, baseLegalJumps, verb, unknownSolution)).collect(toList())) + ">";
      }
      assert analyzer.isNullable(firstToPush);
      return computeType(restToPush, baseLegalJumps, origin, unknownSolution);
    }
    private void createType(String typeName, List<Symbol> toPush, Set<Verb> baseLegalJumps,
        Function<Verb, String> unknownSolution) {
      StringBuilder $ = new StringBuilder("public interface ").append(typeName);
      if (!baseLegalJumps.isEmpty() && (baseLegalJumps.size() != 1 || !baseLegalJumps.contains(SpecialSymbols.$)))
        $.append("<").append(
            String.join(",", baseLegalJumps.stream().filter(verb -> verb != SpecialSymbols.$).map(namer::name).collect(toList())))
            .append(">");
      if (bnf.isSubBNF && baseLegalJumps.contains(SpecialSymbols.$))
        $.append(" extends ").append(ASTNode.class.getCanonicalName());
      $.append("{");
      JSM pushedJSM = new JSM(bnf, analyzer, baseLegalJumps).push(toPush);
      for (Verb verb : bnf.verbs)
        // NOTE $ method is built below
        if (!SpecialSymbols.$.equals(verb))
          if (pushedJSM.jump(verb) != JAMMED)
            $.append("public " + computeType(toPush, baseLegalJumps, verb, unknownSolution) //
                + " " + verb.terminal.name() + "(" + parametersEncoding(verb.type) + ");");
      if (!bnf.isSubBNF && pushedJSM.jump(SpecialSymbols.$) != JAMMED)
        $.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add($.append("}").toString());
    }
    private void compute$Type() {
      apiTypes.add(new StringBuilder("public interface ${") //
          .append(packagePath.toLowerCase() + "." + astTopClass + "." + startSymbol.name()).append(" $();}").toString());
      apiTypeNames.add("$");
    }
    private void computeΛType() {
      List<String> superInterfaces = new ArrayList<>(apiTypeNames);
      superInterfaces.add(ASTNode.class.getCanonicalName());
      StringBuilder $ = new StringBuilder("private static class Λ extends ") //
          .append(FluentAPIRecorder.class.getCanonicalName()).append(" implements ") //
          .append(String.join(",", superInterfaces)).append("{").append(String.join("", //
              bnf.verbs.stream().filter(v -> v != SpecialSymbols.$) //
                  .map(v -> "public Λ " + v.terminal.name() + "(" //
                      + parametersEncoding(v.type) + "){recordTerminal(" //
                      + v.terminal.getClass().getCanonicalName() //
                      + "." + v.terminal.name() + (v.type.length == 0 ? "" : ",") //
                      + parameterNamesEncoding(v.type) + ");return this;}")
                  .collect(toList())));
      if (!bnf.isSubBNF)
        $.append("public ").append(packagePath + "." + astTopClass + "." + startSymbol.name()) //
            .append(" $(){return ast(" + packagePath + "." + astTopClass + ".class.getSimpleName());}");
      $.append("Λ(){super(new " + provider.getCanonicalName() + "().bnf().ebnf()");
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
}
