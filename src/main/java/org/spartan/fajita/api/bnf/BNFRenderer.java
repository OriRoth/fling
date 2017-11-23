package org.spartan.fajita.api.bnf;

import static il.org.spartan.tables.TableRenderer.builtin.TEX;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;
import org.spartan.fajita.api.bnf.symbols.Verb;

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
  default String symbolAnte(NonTerminal nt) {return termAnte(nt);}
  default String symbolPost(NonTerminal nt) {return termPost(nt);}
  default String termAnte(Symbol s) {return termAnte();}
  default String terminalAnte(Terminal t) {return termAnte(t);}
  default String terminalPost(Terminal t) {return termPost(t);}
  default String termPost(Symbol s) {return termPost();}
  /***/
  default boolean normalizedForm() {return false;}
  default Map<NonTerminal, List<List<Symbol>>> sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {return orig;}
  default String ε() {return "";}
  // @formatter:on
  String NL = System.getProperty("line.separator");

  enum builtin implements BNFRenderer {
    ASCII {
      @Override public String clauseBetween() {
        return " | ";
      }
      @Override public String headPost() {
        return " = ";
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
      @Override public String grammarAnte(BNF bnf) {
        return "" //
            + "package org.spartan.fajita.api.examples;" //
            + "class $" + bnf.getApiName() + "{";
      }
      @Override public String grammarPost() {
        return "}";
      }
      @Override public String headAnte(NonTerminal lhs) {
        return "class ";
      }
      @Override public String headPost() {
        return "{";
      }
      @Override public String rulePost() {
        return "}";
      }
      @Override public String bodyAnte(List<List<Symbol>> rhs) {
        return !isInheritenceRule(rhs) ? ε() : "/*";
      }
      @Override public String bodyPost(List<List<Symbol>> rhs) {
        return !isInheritenceRule(rhs) ? ε() : "*/";
      }
      @Override public String termAnte(Symbol s) {
        return s.isNonTerminal() ? "" : ((s.isVerb() ? ((Verb) s).type.toString() : "Void") + " ");
      }
      @Override public String termPost(Symbol s) {
        return (s.isNonTerminal() ? " " + s.name().toLowerCase() : "") + ";";
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
      @Override public Map<NonTerminal, List<List<Symbol>>> sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {
        // TODO Roth: set error message of the tree
        DAG<NonTerminal> t = new DAG<>();
        for (Entry<NonTerminal, List<List<Symbol>>> e : orig.entrySet())
          if (isInheritenceRule(e.getValue()))
            for (List<Symbol> rhs : e.getValue())
              for (Symbol s : rhs)
                if (s.isNonTerminal()) {
                  t.initialize((NonTerminal) s);
                  t.add((NonTerminal) s, e.getKey());
                }
        Map<NonTerminal, List<List<Symbol>>> $ = new LinkedHashMap<>(), remain = new HashMap<>(orig);
        orig.keySet().stream().filter(x -> !t.containsKey(x)).forEach(x -> {
          $.put(x, orig.get(x));
          remain.remove(x);
        });
        int a = 0;
        while (!remain.isEmpty()) {
          remain.entrySet().stream()
              .filter(
                  e -> e.getValue().stream().allMatch(c -> c.stream().allMatch(s -> (s instanceof Terminal || $.containsKey(s)))))
              .forEach(e -> $.put(e.getKey(), e.getValue()));
          $.keySet().forEach(x -> remain.remove(x));
          break;
        }
        return $;
      }
      private boolean isInheritenceRule(List<List<Symbol>> rhs) {
        return rhs.size() > 1 /* rhs.size() > 1 || rhs.size() == 1 && rhs.get(0) instanceof NonTerminal */;
      }
    }
  }
}
