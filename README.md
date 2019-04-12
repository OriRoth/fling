The first general and practical solution of the fluent API problem is presented. We give an algorithm that given a deterministic context free language (equivalently, LR(k), k≥0 language) encodes it in an unbounded parametric polymorphism type system employing only a polynomial number of types. The theoretical result is employed in an actual tool Fling—a fluent API compiler-compiler in the style of YACC, tailored for embedding DSLs in polymorphic, object-oriented languages.

# Fling

*Fling* (**fl**uent **in**terfaces **g**enerator) is a parser generator.
Instead of generating the code of a real-time parser, *Fling* generates Java fluent interfaces,
which implement a compile-time parser of the given language.
*Fling* accepts either *LL(1)* grammar or *DPDA* specifications;
If a grammar is given, *Fling* also generates the AST class definitions used to compile a fluent API INVOCATION into an
abstract parse tree at run-time.

[Download jar](https://github.com/OriRoth/fling/releases/download/1.0.0/fling.jar)

[Full release](https://github.com/OriRoth/fling/releases/tag/1.0.0) (includes examples)

[Documentation](https://oriroth.github.io/fling/docs/)

## Example

Let us define the [Datalog](https://en.wikipedia.org/wiki/Datalog) grammar, in Java, using *Fling*'s interface:
```Java
// Datalog grammar defined in BNF
BNF bnf = bnf().
      start(Program).
      derive(Program).to(oneOrMore(Statement)).
      specialize(Statement).into(Fact, Rule, Query).
      derive(Fact).to(fact.with(S), of.many(S)).
      derive(Query).to(query.with(S), of.many(Term)).
      specialize(Rule).into(Bodyless, WithBody).
      derive(Bodyless).to(always.with(S), of.many(Term)).
      derive(WithBody).to(RuleHead, RuleBody).
      derive(RuleHead).to(infer.with(S), of.many(Term)).
      derive(RuleBody).to(FirstClause, noneOrMore(AdditionalClause)).
      derive(FirstClause).to(when.with(S), of.many(Term)).
      derive(AdditionalClause).to(and.with(S), of.many(Term)).
      derive(Term).to(l.with(S)).or(v.with(S)).
      build();
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
Program program =
  fact("parent").of("john", "bob").
  fact("parent").of("bob", "donald").
  always("ancestor").of(l("adam"), v("X")).
  infer("ancestor").of(v("A"), v("B")).
    when("parent").of(v("A"), v("B")).
  infer("ancestor").of(v("A"), v("B")).
    when("parent").of(v("A"), v("C")).
    and("ancestor").of(v("C"), v("B")).
  query("ancestor").of(l("john"), v("X")).$();
```
The produced program is represented in Java as an abstract syntax tree (AST) that can be traversed and analyzed by the
client library.
