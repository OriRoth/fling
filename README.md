The first general and practical solution of the fluent API problem is presented. We give an algorithm that given a deterministic context free language (equivalently, LR(k), k≥0 language) encodes it in an unbounded parametric polymorphism type system employing only a polynomial number of types. The theoretical result is employed in an actual tool Fling—a fluent API compiler-compiler in the style of YACC, tailored for embedding DSLs in polymorphic, object-oriented languages.

# Fling

*Fling* (**fl**uent **in**terfaces **g**enerator) is a parser generator.
Instead of generating the code of a real-time parser, *Fling* generates Java fluent interfaces,
which implement a compile-time parser of the given language.
*Fling* accepts either *LL(1)* grammar or *DPDA* specifications;
If a grammar is given, *Fling* also generates the AST class definitions used to compile a fluent API INVOCATION into an
abstract parse tree at run-time.

## Example

Let us define the [Datalog](https://en.wikipedia.org/wiki/Datalog) grammar, in Java, using *Fling*'s interface:
```Java
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
After *Fling* has created the fluent interfaces supporting Datalog, a simple Datalog program...
```Datalog
parent(john, bob)
parent(bob, donald)
ancestor(A, B) := parent(A, B)
ancestor(A, B) := parent(A, C), ancestor(C, B)
ancestor(john, X)?
```
...can be written in Java by method-chaining, as follows:
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
The produced program is represented in Java as an abstract syntax tree (AST) that can be traversed and analyzed by the
client library.
