package org.spartan.fajita.api.bnf;

import static il.org.spartan.tables.TableRenderer.builtin.TEX;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

import static java.util.stream.Collectors.toList;
import static java.util.Collections.emptyList;

import org.spartan.fajita.api.EFajita.*;

@SuppressWarnings("unused") public interface BNFRenderer {
  // @formatter:off
  default String bodyAnte() {return ε();}
  default String bodyPost() {return ε();}
  default String clauseAnte() {return ε();}
  default String clauseBetween() {return ε();}
  default String clausePost() {return ε();}
  default String epsilonAnte() {return ε();}
  default String epsilonPost() {return ε();}
  default String grammarAnte() {return ε();}
  default String grammarPost() {return ε();}
  default String headAnte() {return ε();}
  default String headPost() {return ε();}
  default String ruleAnte() {return ε();}
  default String rulePost() {return ε();}
  default String symbolAnte() {return termAnte();}
  default String symbolPost() {return termPost();}
  default String termAnte() {return ε();}
  default String termBetween() {return ε();}
  default String terminalAnte() {return termAnte();}
  default String terminalPost() {return termPost();}
  default String termPost() {return ε();}
  /***/
  default String bodyAnte(List<List<Symbol>> rhs) {return bodyAnte();}
  default String bodyPost(List<List<Symbol>> rhs) {return bodyPost();}
  default String clauseAnte(List<Symbol> clause) {return clauseAnte();}
  default String clausePost(List<Symbol> clause) {return clausePost();}
  default String grammarAnte(BNF bnf) {return grammarAnte();}
  default String grammarPost(BNF bnf) {return grammarPost();}
  default String headAnte(NonTerminal lhs) {return headAnte();}
  default String headPost(NonTerminal lhs) {return headPost();}
  default String ruleAnte(NonTerminal lhs, List<List<Symbol>> rhs) {return ruleAnte();}
  default String rulePost(NonTerminal lhs, List<List<Symbol>> rhs) {return rulePost();}
  default String special(Symbol s) {return termAnte(s) + s.name() + termPost(s);}
  default String symbolAnte(NonTerminal nt) {return termAnte(nt);}
  default String symbolPost(NonTerminal nt) {return termPost(nt);}
  default String termAnte(Symbol s) {return termAnte();}
  default String terminalAnte(Terminal t) {return termAnte(t);}
  default String terminalPost(Terminal t) {return termPost(t);}
  default String termPost(Symbol s) {return termPost();}
  /***/
  default boolean normalizedForm() {return false;}
  default Map<NonTerminal, List<List<Symbol>>> sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {return orig;}
  default boolean visitBody(NonTerminal lhs, List<List<Symbol>> rhs) {return true;}
  default boolean visitTerminal(Verb s) {return true;}
  default boolean classRules() {return false;}
  default String ε() {return "";}
  // @formatter:on
  String NL = System.getProperty("line.separator");

  // TODO Roth: code duplication
  enum builtin implements BNFRenderer {
    ASCII {
      @Override public String clauseBetween() {
        return " | ";
      }
      @Override public String headPost() {
        return " ::= ";
      }
      @Override public String rulePost() {
        return NL;
      }
      @Override public String termBetween() {
        return " ";
      }
    },
    LATEX_TABLE {
      @Override public String grammarAnte() {
        return TEX.beforeTable();
      }
      @Override public String grammarPost() {
        return TEX.afterTable();
      }
    },
    JAMOOS_CLASSES {
      // TODO Roth: set error message of the tree
      DAG.Tree<NonTerminal> inheritance = new DAG.Tree<>();
      Map<Object, Integer> counters = new HashMap<>();
      Function<Object, Integer> counter = s -> {
        counters.putIfAbsent(s, Integer.valueOf(1));
        return counters.put(s, Integer.valueOf(counters.get(s).intValue() + 1));
      };
      Function<Object, String> namer = s -> {
        String $ = s instanceof Symbol ? ((Symbol) s).name() : s.toString();
        return $ + counter.apply($);
      };

      @Override public String grammarAnte(BNF bnf) {
        return "" //
            + "package org.spartan.fajita.api.junk;" //
            + "class $" + bnf.getApiName() + "{";
      }
      @Override public String grammarPost() {
        return "}";
      }
      @Override public String headAnte(NonTerminal lhs) {
        return "class ";
      }
      @Override public String headPost(NonTerminal lhs) {
        return (!inheritance.containsKey(lhs) ? "" : " extends " + inheritance.get(lhs).iterator().next()) + "{";
      }
      @Override public String rulePost() {
        return "}";
      }
      @Override public boolean visitBody(NonTerminal lhs, List<List<Symbol>> rhs) {
        return !isInheritanceRule(rhs);
      }
      @Override public String termAnte(Symbol s) {
        String verbType;
        return s.isNonTerminal() ? ""
            : ((s.isVerb() ? ("".equals(verbType = ((Verb) s).type.toString()) ? "Void" : verbType) : "Void") + " ");
      }
      @Override public String termPost(Symbol s) {
        return (s.isNonTerminal() ? " " + namer.apply(s).toLowerCase() : "") + ";";
      }
      @Override public String epsilonAnte() {
        return "/*";
      }
      @Override public String epsilonPost() {
        return "*/";
      }
      @Override public boolean normalizedForm() {
        return true;
      }
      @Override public boolean classRules() {
        return true;
      }
      @Override public String special(Symbol s) {
        StringBuilder $ = new StringBuilder();
        if (s instanceof Optional) {
          Optional o = (Optional) s;
          for (String x : solveSpecialSymbols(o.symbols))
            $.append("java.util.Optional<").append(x).append("> ").append(namer.apply(x).toLowerCase()).append(";");
        } else if (s instanceof Either) {
          Either e = (Either) s;
          Integer c = counter.apply("EitherFieldName");
          $.append(eitherName(e, counter)).append(" e" + c + ";");
        } else if (s instanceof OneOrMore) {
          OneOrMore o = (OneOrMore) s;
          for (String x : solveSpecialSymbols(o.symbols))
            $.append(x).append("[]").append(namer.apply(x).toLowerCase()).append(";");
          for (String x : solveSpecialSymbols(o.separators))
            $.append(x).append("[]").append("s" + namer.apply(x).toLowerCase()).append(";");
        } else if (s instanceof NoneOrMore || s instanceof NoneOrMore.Separator || s instanceof NoneOrMore.IfNone) {
          NoneOrMore n = s instanceof NoneOrMore ? (NoneOrMore) s
              : s instanceof NoneOrMore.Separator ? ((NoneOrMore.Separator) s).parent() : ((NoneOrMore.IfNone) s).parent();
          if (n.ifNone.isEmpty()) {
            for (String x : solveSpecialSymbols(n.symbols))
              $.append(x).append("[]").append(namer.apply(x).toLowerCase()).append(";");
            for (String x : solveSpecialSymbols(n.separators))
              $.append(x).append("[]").append("s" + namer.apply(x).toLowerCase()).append(";");
          } else {
            Integer c = counter.apply("EitherFieldName");
            $.append(eitherName(n, counter)).append(" e" + c + ";");
          }
        } else if (s instanceof EVerb) {
          EVerb e = (EVerb) s;
          $.append(solveSpecialSymbol(e.ent)).append(" ").append(e.name()).append(";");
        } else if (s instanceof Terminal)
          $.append(terminalAnte()).append(((Terminal) s).name()).append(terminalPost());
        else if (s instanceof NonTerminal)
          $.append(symbolAnte()).append(((NonTerminal) s).name()).append(symbolPost());
        return $.toString();
      }
      @Override public Map<NonTerminal, List<List<Symbol>>> sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {
        clearEmptyRules(orig);
        inheritance.clear();
        for (Entry<NonTerminal, List<List<Symbol>>> e : orig.entrySet())
          if (isInheritanceRule(e.getValue()))
            for (List<Symbol> rhs : e.getValue())
              for (Symbol s : rhs)
                if (s.isNonTerminal()) {
                  inheritance.initialize((NonTerminal) s);
                  inheritance.add((NonTerminal) s, e.getKey());
                }
        Map<NonTerminal, List<List<Symbol>>> $ = new LinkedHashMap<>(), remain = new HashMap<>(orig);
        orig.keySet().stream().filter(x -> !inheritance.containsKey(x)).forEach(x -> {
          $.put(x, orig.get(x));
          remain.remove(x);
        });
        int a = 0;
        while (!remain.isEmpty()) {
          remain.entrySet().stream().filter(
              e -> e.getValue().stream().allMatch(c -> c.stream().allMatch(s -> (!(s instanceof NonTerminal) || $.containsKey(s)))))
              .forEach(e -> $.put(e.getKey(), e.getValue()));
          $.keySet().forEach(x -> remain.remove(x));
        }
        return $;
      }
      private boolean isInheritanceRule(List<List<Symbol>> rhs) {
        return rhs.size() > 1 || rhs.size() == 1 && rhs.get(0) instanceof NonTerminal;
      }
      private List<String> solveSpecialSymbols(List<Symbol> ss) {
        return ss.stream().map(x -> solveSpecialSymbol(x)).collect(toList());
      }
      private String solveSpecialSymbol(Symbol s) {
        StringBuilder $ = new StringBuilder();
        if (s instanceof Optional) {
          Optional o = (Optional) s;
          for (String x : solveSpecialSymbols(o.symbols))
            $.append("java.util.Optional<").append(x).append(">");
        } else if (s instanceof Either) {
          Either e = (Either) s;
          Integer c = counter.apply("EitherFieldName");
          $.append(eitherName(e, counter));
        } else if (s instanceof OneOrMore) {
          OneOrMore o = (OneOrMore) s;
          for (String x : solveSpecialSymbols(o.symbols))
            $.append(x).append("[]");
          for (String x : solveSpecialSymbols(o.separators))
            $.append(x).append("[]");
        } else if (s instanceof NoneOrMore || s instanceof NoneOrMore.Separator || s instanceof NoneOrMore.IfNone) {
          NoneOrMore n = s instanceof NoneOrMore ? (NoneOrMore) s
              : s instanceof NoneOrMore.Separator ? ((NoneOrMore.Separator) s).parent() : ((NoneOrMore.IfNone) s).parent();
          if (n.ifNone.isEmpty()) {
            for (String x : solveSpecialSymbols(n.symbols))
              $.append(x).append("[]");
            for (String x : solveSpecialSymbols(n.separators))
              $.append(x).append("[]");
          } else {
            Integer c = counter.apply("EitherFieldName");
            $.append(eitherName(n, counter));
          }
        } else if (s instanceof EVerb) {
          EVerb e = (EVerb) s;
          $.append(solveSpecialSymbol(e.ent));
        } else if (s instanceof Verb) {
          String verbType;
          $.append("".equals(verbType = ((Verb) s).type.toString()) ? "Void" : verbType);
        } else if (s instanceof NonTerminal)
          $.append(((NonTerminal) s).name());
        return $.toString();
      }
    },
    JAMOOS_EITHER {
      // TODO Roth: set proper type errors
      Map<Object, Integer> counters = new HashMap<>();
      Function<Object, Integer> counter = s -> {
        counters.putIfAbsent(s, Integer.valueOf(1));
        return counters.put(s, Integer.valueOf(counters.get(s).intValue() + 1));
      };
      Function<Symbol, String> namer = s -> {
        return s.name() + counter.apply(s);
      };

      @Override public boolean classRules() {
        return true;
      }
      @Override public String grammarAnte() {
        return "/*";
      }
      @Override public String grammarPost() {
        return "*/";
      }
      @Override public String special(Symbol s) {
        StringBuilder $ = new StringBuilder("*/");
        if (s instanceof Either) {
          Either e = (Either) s;
          $.append("static class ").append(eitherName(e, counter)).append("{");
          List<String> enumContent = an.empty.list();
          $.append("public Object $;").append("public Tag tag;");
          for (Symbol x : e.symbols) {
            String verbType, typeName, capitalName;
            $.append("boolean is").append(capitalName = x.name().substring(0, 1).toUpperCase() + x.name().substring(1))
                .append("(){return Tag.").append(capitalName).append(".equals(tag);}");
            $.append(typeName = x.isVerb() ? ("".equals(verbType = ((Verb) x).type.toString()) ? "Void" : verbType) : "Void")
                .append(" get").append(capitalName).append("(){return (") //
                .append(typeName).append(")$;}");
            enumContent.add(capitalName);
          }
          $.append("public enum Tag{");
          for (String x : enumContent)
            $.append(x).append(",");
          $.append("}}");
        } else if (s instanceof NoneOrMore || s instanceof NoneOrMore.Separator || s instanceof NoneOrMore.IfNone) {
          NoneOrMore n = s instanceof NoneOrMore ? (NoneOrMore) s
              : s instanceof NoneOrMore.Separator ? ((NoneOrMore.Separator) s).parent() : ((NoneOrMore.IfNone) s).parent();
          if (!n.ifNone.isEmpty()) {
            $.append("static class ").append(eitherName(n, counter)).append("{public boolean exist;");
            for (Symbol x : n.symbols) {
              String verbType, varName, typeName, capitalName;
              $.append("private ") //
                  .append(typeName = (x.isVerb() ? ("".equals(verbType = ((Verb) x).type.toString()) ? "Void" : verbType) : "Void")
                      + "[]")
                  .append(" ") //
                  .append(varName = namer.apply(x).toLowerCase()).append(";");
              $.append(typeName).append(" get").append(x.name().substring(0, 1).toUpperCase() + x.name().substring(1))
                  .append("(){return ").append(varName).append(";}");
            }
            for (Symbol x : n.separators) {
              String verbType, varName, typeName, capitalName;
              $.append("private ") //
                  .append(typeName = (x.isVerb() ? ("".equals(verbType = ((Verb) x).type.toString()) ? "Void" : verbType) : "Void")
                      + "[]")
                  .append(" ") //
                  .append(varName = namer.apply(x).toLowerCase()).append(";");
              $.append(typeName).append(" get").append(x.name().substring(0, 1).toUpperCase() + x.name().substring(1))
                  .append("(){return ").append(varName).append(";}");
            }
            for (Symbol x : n.ifNone) {
              String verbType, varName, typeName, capitalName;
              $.append("private ") //
                  .append(typeName = x.isVerb() ? ("".equals(verbType = ((Verb) x).type.toString()) ? "Void" : verbType) : "Void")
                  .append(" ") //
                  .append(varName = namer.apply(x).toLowerCase()).append(";");
              $.append(typeName).append(" get").append(x.name().substring(0, 1).toUpperCase() + x.name().substring(1))
                  .append("(){return ").append(varName).append(";}");
            }
            $.append("boolean isList(){return exist;}boolean isNone(){return !exist;}}");
          }
        }
        return $.append("/*").toString();
      }
    },
    JAMOOS_INTERFACES {
      // // TODO Roth: set error message of the tree
      // DAG<NonTerminal> inheritance = new DAG<>();
      // Map<Symbol, Integer> counter = new HashMap<>();
      // Function<Symbol, String> namer = s -> {
      // counter.putIfAbsent(s, Integer.valueOf(1));
      // return s.name() + counter.put(s,
      // Integer.valueOf(counter.get(s).intValue() + 1));
      // };
      //
      // @Override public String grammarAnte(BNF bnf) {
      // return "" //
      // + "package org.spartan.fajita.api.examples;" //
      // + "class $" + bnf.getApiName() + "{";
      // }
      // @Override public String grammarPost() {
      // return "}";
      // }
      // @Override public String headAnte(NonTerminal lhs) {
      // return "interface ";
      // }
      // @Override public String headPost(NonTerminal lhs) {
      // return (!inheritance.containsKey(lhs) ? ""
      // : " extends " + String.join(",", inheritance.get(lhs).stream().map(x ->
      // x.toString()).collect(Collectors.toList())))
      // + "{";
      // }
      // @Override public String rulePost() {
      // return "}";
      // }
      // @Override public boolean visitBody(NonTerminal lhs, List<List<Symbol>>
      // rhs) {
      // return !isInheritanceRule(rhs);
      // }
      // @Override public String termAnte(Symbol s) {
      // String verbType;
      // return s.isNonTerminal() ? ""
      // : ((s.isVerb() ? ("".equals(verbType = ((Verb) s).type.toString()) ?
      // "Void" : verbType) : "Void")) + " ";
      // }
      // @Override public String termPost(Symbol s) {
      // return (s.isNonTerminal() ? " " + namer.apply(s).toLowerCase() : "") +
      // "();";
      // }
      // @Override public String epsilonAnte() {
      // return "/*";
      // }
      // @Override public String epsilonPost() {
      // return "*/";
      // }
      // @Override public boolean normalizedForm() {
      // return true;
      // }
      // @Override public boolean classRules() {
      // return true;
      // }
      // @Override public String special(Symbol s) {
      // StringBuilder $ = new StringBuilder();
      // if (s instanceof Optional) {
      // Optional o = (Optional) s;
      // for (Symbol x : o.symbols)
      // // TODO Roth: add namer
      // $.append("java.util.Optional<").append(x.isVerb() ? ((Verb)
      // x).type.toString() : x.name()).append("> ")
      // .append(namer.apply(x).toLowerCase()).append(";");
      // }
      // return $.toString();
      // }
      // @Override public Map<NonTerminal, List<List<Symbol>>>
      // sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {
      // inheritance.clear();
      // for (Entry<NonTerminal, List<List<Symbol>>> e : orig.entrySet())
      // if (isInheritanceRule(e.getValue()))
      // for (List<Symbol> rhs : e.getValue())
      // for (Symbol s : rhs)
      // if (s.isNonTerminal()) {
      // inheritance.initialize((NonTerminal) s);
      // inheritance.add((NonTerminal) s, e.getKey());
      // }
      // Map<NonTerminal, List<List<Symbol>>> $ = new LinkedHashMap<>(), remain
      // = new HashMap<>(origcollector);
      // orig.keySet().stream().filter(x ->
      // !inheritance.containsKey(x)).forEach(x -> {
      // $.put(x, orig.get(x));
      // remain.remove(x);
      // });
      // int a = 0;
      // while (!remain.isEmpty()) {
      // remain.entrySet().stream()
      // .filter(
      // e -> e.getValue().stream().allMatch(c -> c.stream().allMatch(s -> (s
      // instanceof Terminal || $.containsKey(s)))))
      // .forEach(e -> $.put(e.getKey(), e.getValue()));
      // $.keySet().forEach(x -> remain.remove(x));
      // break;
      // }
      // return $;
      // }
      // private boolean isInheritanceRule(List<List<Symbol>> rhs) {
      // return rhs.size() > 1 || rhs.size() == 1 && rhs.get(0) instanceof
      // NonTerminal;
      // }
    };
    static void clearEmptyRules(Map<NonTerminal, List<List<Symbol>>> rs) {
      List<Symbol> tbr = an.empty.list();
      rs.keySet().stream().forEach(k -> //
      rs.get(k).stream().forEach(c -> //
      c.stream().filter(l -> //
      l.isVerb() && ((Verb) l).type.isEmpty()).forEach(e -> tbr.add(e))));
      rs.values().stream().forEach(r -> r.stream().forEach(c -> c.removeAll(tbr)));
    }
    static String eitherName(Head h, Function<Object, Integer> counter) {
      StringBuilder $ = new StringBuilder("Either");
      h.symbols.stream().forEach(x -> $.append(x.name().substring(0, 1).toUpperCase() + x.name().substring(1)));
      Integer c = counter.apply(h);
      return (c.intValue() == 1 ? $ : $.append(c)).toString();
    }
  }
}
