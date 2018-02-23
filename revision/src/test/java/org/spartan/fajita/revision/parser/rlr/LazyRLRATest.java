package org.spartan.fajita.revision.parser.rlr;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Terminal;

@SuppressWarnings("static-method") public class LazyRLRATest {
  @Test public void test1() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    Set<DerivationRule> rules = new LinkedHashSet<>();
    terminals.add(t("a"));
    terminals.add(t("b"));
    variables.add(v("S"));
    rules.add(DerivationRule.of(v("S"), t("a"), v("S")));
    rules.add(DerivationRule.of(v("S"), t("b")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("b"));
    assert r.initialize().accept(ts("aaab"));
    assert r.initialize().reject(ts("aaabaaab"));
    assert r.initialize().reject(ts("aaa"));
  }
  // Not ll nor slr
  @Test public void test2() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("="));
    terminals.add(t("*"));
    terminals.add(t("x"));
    variables.add(v("S"));
    variables.add(v("L"));
    variables.add(v("R"));
    rules.add(DerivationRule.of(v("S"), v("L"), t("="), v("R")));
    rules.add(DerivationRule.of(v("S"), v("R")));
    rules.add(DerivationRule.of(v("L"), t("*"), v("R")));
    rules.add(DerivationRule.of(v("L"), t("x")));
    rules.add(DerivationRule.of(v("R"), v("L")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("*****x=***x"));
    assert r.initialize().reject(ts("*****x=**x=*x"));
  }
  @Test public void test3() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("s"));
    variables.add(v("S"));
    rules.add(DerivationRule.of(v("S"), v("S"), t("s")));
    rules.add(DerivationRule.of(v("S"), t("s")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("s"));
    assert r.initialize().accept(ts("sssss"));
  }
  @Test public void test4() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("a"));
    terminals.add(t("b"));
    terminals.add(t("d"));
    terminals.add(t("e"));
    terminals.add(t("f"));
    variables.add(v("S"));
    variables.add(v("A"));
    variables.add(v("B"));
    variables.add(v("C"));
    variables.add(v("D"));
    variables.add(v("E"));
    variables.add(v("F"));
    rules.add(DerivationRule.of(v("S"), v("A"), v("B"), v("C")));
    rules.add(DerivationRule.of(v("A"), t("a")));
    rules.add(DerivationRule.of(v("A")));
    rules.add(DerivationRule.of(v("B"), t("b")));
    rules.add(DerivationRule.of(v("B")));
    rules.add(DerivationRule.of(v("C"), v("D"), v("E"), v("F")));
    rules.add(DerivationRule.of(v("D"), t("d")));
    rules.add(DerivationRule.of(v("D")));
    rules.add(DerivationRule.of(v("E"), t("e")));
    rules.add(DerivationRule.of(v("E")));
    rules.add(DerivationRule.of(v("F"), t("f")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("f"));
    assert r.initialize().accept(ts("bf"));
    assert r.initialize().accept(ts("abdef"));
    assert r.initialize().accept(ts("bdf"));
    assert r.initialize().accept(ts("aef"));
  }
  @Test public void test5() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("+"));
    terminals.add(t("i"));
    terminals.add(t("("));
    terminals.add(t(")"));
    terminals.add(t("["));
    terminals.add(t("]"));
    variables.add(v("S"));
    variables.add(v("T"));
    variables.add(v("E"));
    rules.add(DerivationRule.of(v("S"), v("E")));
    rules.add(DerivationRule.of(v("E"), v("T")));
    rules.add(DerivationRule.of(v("E"), v("E"), t("+"), v("T")));
    rules.add(DerivationRule.of(v("T"), t("i")));
    rules.add(DerivationRule.of(v("T"), t("("), v("E"), t(")")));
    rules.add(DerivationRule.of(v("T"), t("i"), t("["), v("E"), t("]")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("i"));
    assert r.initialize().accept(ts("(i[i+(i)])"));
    assert r.initialize().reject(ts("([i+(i)])"));
    assert r.initialize().reject(ts("i(i[i+(i)])"));
  }
  @Test public void test6() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("+"));
    terminals.add(t("i"));
    terminals.add(t("("));
    terminals.add(t(")"));
    terminals.add(t("*"));
    variables.add(v("S"));
    variables.add(v("T"));
    rules.add(DerivationRule.of(v("S"), v("T")));
    rules.add(DerivationRule.of(v("S"), v("T"), t("+"), v("S")));
    rules.add(DerivationRule.of(v("T"), t("i")));
    rules.add(DerivationRule.of(v("T"), t("i"), t("*"), v("T")));
    rules.add(DerivationRule.of(v("T"), t("("), v("S"), t(")")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("i"));
    assert r.initialize().accept(ts("i+i"));
    assert r.initialize().accept(ts("(i*(i+i))"));
    assert r.initialize().reject(ts("(i+*(i))"));
    assert r.initialize().reject(ts("i+"));
  }
  @Test public void test7() {
    Set<Terminal> terminals = new LinkedHashSet<>();
    Set<NonTerminal> variables = new LinkedHashSet<>();
    Set<DerivationRule> rules = new LinkedHashSet<>();
    Set<NonTerminal> startVariables = new LinkedHashSet<>();
    startVariables.add(v("S"));
    terminals.add(t("("));
    terminals.add(t(")"));
    variables.add(v("S"));
    rules.add(DerivationRule.of(v("S"), t("("), v("S"), t(")"), v("S")));
    rules.add(DerivationRule.of(v("S")));
    LazyRLRA r = new LazyRLRA(terminals, variables, rules, startVariables);
    assert r.initialize().accept(ts("()"));
    assert r.initialize().accept(ts("(()())"));
    assert r.initialize().accept(ts("(()((())))"));
    assert r.initialize().reject(ts("(()"));
    assert r.initialize().reject(ts("(()))"));
    assert r.initialize().reject(ts("(()(())))"));
  }
  private static NonTerminal v(String name) {
    return NonTerminal.of(name);
  }
  private static Terminal t(String name) {
    return new Terminal() {
      @Override public String name() {
        return name;
      }
      @Override public int hashCode() {
        return Objects.hash(name);
      }
      @Override public boolean equals(Object obj) {
        return obj instanceof Terminal && name.equals(((Terminal) obj).name());
      }
      @Override public String toString() {
        return name;
      }
    };
  }
  private static List<Terminal> ts(String word) {
    return word.chars().mapToObj(c -> t(((char) c) + "")).collect(Collectors.toList());
  }
}
