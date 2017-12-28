package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;
import static org.spartan.fajita.revision.parser.rll.JSM11.JAMMED;
import static org.spartan.fajita.revision.parser.rll.JSM11.UNKNOWN;
import static org.spartan.fajita.revision.parser.rll.JSM11.J.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.spartan.fajita.revision.parser.rll.JSM11.J;
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
    public String name(J j) {
      assert j != JJAMMED && j != JUNKNOWN && j.address.size() <= 1;
      StringBuilder $ = new StringBuilder();
      if (!j.address.isEmpty())
        $.append(name(j.address.peek()));
      $.append("¢").append(names(j.toPush)).append("_");
      if (!j.address.isEmpty() && j.address.peek().isVerb() && j.toPush.isEmpty())
        return $.toString();
      Set<Verb> blj = j.address.baseLegalJumps();
      blj.remove(SpecialSymbols.$);
      if (j.asJSM().peekLegalJumps().contains(SpecialSymbols.$))
        blj.add(SpecialSymbols.$);
      return $.append(names(blj)).toString();
    }
  }

  class MembersComputer {
    private final Set<J> seenTypes = new LinkedHashSet<>();
    private final Set<String> apiTypeNames = new LinkedHashSet<>();

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
      J j = RLLPConcrete3.jnext(new JSM11(bnf, analyzer, startSymbol, blj), v);
      computeType(j, v, x -> namer.name(x), () -> "ε");
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
          .append(computeType(j, v, unknownSolution, emptySolution)) //
          .append(" ").append(v.terminal.name()).append("(").append(parametersEncoding(v.type)) //
          .append("){").append("$$$ $$$ = new $$$();$$$.recordTerminal(" //
              + v.terminal.getClass().getCanonicalName() + "." + v.terminal.name() //
              + (v.type.length == 0 ? "" : ",") + parameterNamesEncoding(v.type) + ");return $$$;}") //
          .toString());
    }
    private String computeType(J j, Verb origin, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      assert j != JJAMMED;
      if (j == JUNKNOWN)
        return unknownSolution.apply(origin);
      if (j.isEmpty())
        return emptySolution.get();
      J jpeek = j.trim();
      String $ = namer.name(jpeek);
      if (seenTypes.contains(jpeek))
        return computeTemplates(j, unknownSolution, emptySolution);
      seenTypes.add(jpeek);
      JSM11 jsm = jpeek.asJSM();
      Symbol top = jsm.peek();
      Set<Verb> allLegalJumps = jsm.allLegalJumps();
      // NOTE an optimization for verb as stack top
      if (top.isVerb())
        allLegalJumps.clear();
      StringBuilder t = new StringBuilder("public interface ").append($);
      StringBuilder template = new StringBuilder("<ε");
      List<String> templates = new ArrayList<>();
      for (Verb v : allLegalJumps)
        // NOTE an optimization for $ jump
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
          t.append(computeMethod(jsm, v, unknownSolution, emptySolution));
      if (!bnf.isSubBNF && jsm.peekLegalJumps().contains(SpecialSymbols.$))
        t.append(packagePath + "." + astTopClass + "." + startSymbol.name()).append(" $();");
      apiTypes.add(t.append("}").toString());
      apiTypeNames.add($);
      return computeTemplates(j, unknownSolution, emptySolution);
    }
    private String computeMethod(JSM11 jsm, Verb v, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      if (jsm == JAMMED)
        return "";
      if (jsm == UNKNOWN)
        return "public " + namer.name(v) + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      if (jsm.isEmpty())
        return "public ε " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
      Symbol top = jsm.peek();
      J jnext = RLLPConcrete3.jnext(jsm, v);
      if (JJAMMED == jnext)
        return "";
      // NOTE contains an optimization for verb as stack top
      return top.isVerb() ? //
          !top.asVerb().equals(v) ? //
              "" //
              : "public E " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");" //
          : "public " + computeType(jnext, v, unknownSolution, emptySolution) //
              + " " + v.terminal.name() + "(" + parametersEncoding(v.type) + ");";
    }
    private String computeTemplates(J j, Function<Verb, String> unknownSolution, Supplier<String> emptySolution) {
      assert JJAMMED != j && JUNKNOWN != j && !j.isEmpty();
      StringBuilder $ = new StringBuilder("<") //
          // NOTE can send null as origin verb as sent J cannot be JUNKNOWN
          .append(computeType(J.of(j.address.pop()), null, unknownSolution, emptySolution));
      JSM11 jsm = j.asJSM();
      List<String> templates = new ArrayList<>();
      // NOTE contains an optimization for verb as stack top
      if (jsm.peek().isNonTerminal())
        for (Verb v : jsm.allLegalJumps())
          if (!SpecialSymbols.$.equals(v))
            templates.add(computeType(jsm.jjump(v), v, unknownSolution, emptySolution));
      if (!templates.isEmpty())
        $.append(",").append(String.join(",", templates));
      return $.append(">").toString();
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
}
