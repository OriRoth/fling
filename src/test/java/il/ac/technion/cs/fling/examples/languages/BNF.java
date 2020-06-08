package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.BNF.V.*;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;

public class BNF implements FluentLanguageAPI<BNF.Σ, BNF.V> {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone
  }

  public enum V implements Variable {
    PlainBNF, Rule, RuleBody, RuleTail
  }

  @Override public String name() {
    return "BNFAPI";
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  @Override public il.ac.technion.cs.fling.FancyEBNF BNF() {
    // @formatter:off
    return bnf(). //
        start(PlainBNF). //
        derive(PlainBNF).to(bnf, start.with(Variable.class), Symbol.noneOrMore(Rule)). // PlainBNF ::= start(Symbol) Rule*
        derive(Rule).to(derive.with(Variable.class), RuleBody). // Rule ::= derive(Variable) RuleBody
        derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). // Rule ::= specialize(Variable) into(Variable*)
        derive(RuleBody).to(to.many(Symbol.class), Symbol.noneOrMore(RuleTail)).or(toEpsilon). // RuleBody ::= to(Symbol*) RuleTail* | toEpsilon()
        derive(RuleTail).to(or.many(Symbol.class)).or(orNone). // RuleTail ::= or(Symbol*) | orNone()
        build();
    // @formatter:on
  }
}
