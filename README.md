# fling

*fling* (**fl**uent **in**terfaces **g**enerator) is a parser generator.
Instead of generating the code of a real-time parser, *fling* generates Java fluent interfaces,
which implement a compile-time parser of the given language.

*fling* accepts, currently, *LL(1)* (left-to-right, leftmost, using 1 lookahead token) grammars.

## Example

Let us define a Datalog grammar, in Java, using *fling*'s interface:
```Java
// Datalog grammar terminals
public static enum Term implements Terminal {
  rule, is, fact, by, query
}
// Datalog grammar non-terminals
public static enum NT implements NonTerminal {
  Program, Statement, Rule, Query, Fact, RuleExpression
}
// Datalog grammar defined in BNF
public FlingBNF bnf() {
  return Fling.build(getClass(), Term.class, NT.class, "Datalog",
        "desired.package.path", "system/project/path") //
      .start(Program) //
      .derive(Program).to(oneOrMore(Statement)) //
      .specialize(Statement).into(Rule, Query, Fact) //
      .derive(Fact).to(attribute(fact, String.class), attribute(by, new VarArgs(String.class))) //
      .derive(Rule).to( //
          attribute(rule, String.class), //
          attribute(by, new VarArgs(String.class)), //
          oneOrMore(RuleExpression)) //
      .derive(RuleExpression).to(attribute(is, String.class), attribute(by, new VarArgs(String.class))) //
      .derive(Query).to(attribute(query, String.class), attribute(by, new VarArgs(String.class))) //
  ;
}
```
After *fling* has created the fluent interfaces supporting Datalog, a simple Datalog program can be written in Java by
method-chaining:
```Java
public class Ancestor {
  public static Program program() {
    return fact("parent").by("john", "bob") //
        .fact("parent").by("bob", "donald") //
        .rule("ancestor").by("A", "B") //
        /**/.is("parent").by("A", "B") //
        .rule("ancestor").by("A", "B") //
        /**/.is("parent").by("A", "C") //
        /**/.is("ancestor").by("C", "B") //
        .query("ancestor").by("john", "X") //
        .$();
  }
}
```
The produced program is represented in Java as an abstract syntax tree (AST), that can be traversed and analyzed by the
client library.
