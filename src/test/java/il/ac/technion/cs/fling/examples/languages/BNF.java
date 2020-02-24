package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.BNF.V.*;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.JavaMediator;

public class BNF {
  @SuppressWarnings("hiding") public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone
  }

  public enum V implements Variable {
    PlainBNF, Rule, RuleBody, RuleTail
  }

  public static final il.ac.technion.cs.fling.BNF bnf = bnf(). //
      start(PlainBNF). //
      derive(PlainBNF).to(Σ.bnf, start.with(Variable.class), Symbol.noneOrMore(Rule)). // PlainBNF ::= start(Symbol) Rule*
      derive(Rule).to(derive.with(Variable.class), RuleBody). // Rule ::= derive(Variable) RuleBody
      derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). // Rule ::= specialize(Variable) into(Variable*)
      derive(RuleBody).to(to.many(Symbol.class), Symbol.noneOrMore(RuleTail)).or(toEpsilon). // RuleBody ::= to(Symbol*) RuleTail* | toEpsilon()
      derive(RuleTail).to(or.many(Symbol.class)).or(orNone). // RuleTail ::= or(Symbol*) | orNone()
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "il.ac.technion.cs.fling.examples.generated", "BNFAPI", Σ.class);
}
