package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.EBNF.V.*;
import static il.ac.technion.cs.fling.examples.languages.EBNF.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;

public class EBNF implements FluentLanguageAPI<EBNF.Σ, EBNF.V> {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone, oneOrMore, twoOrMore, noneOrMore, optional, __
  }

  public enum V implements Variable {
    EBNF, Rule, RuleBody, RuleTail, RuleItem, MySymbol
  }

  @Override public String name() {
    return "EBNFAPI";
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
        start(EBNF). //
        derive(EBNF).to(bnf, start.with(Variable.class), Symbol.oneOrMore(Rule)). // PlainBNF ::= start(Symbol) Rule*
        derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). // Rule ::= specialize(Variable) into(Variable*)
        derive(Rule).to(derive.with(Variable.class), RuleBody). // Rule ::= derive(Variable) RuleBody
        derive(RuleBody).to(to.many(RuleItem), Symbol.noneOrMore(RuleTail)).or(toEpsilon). // RuleBody ::= to(RuleItem*) RuleTail* | toEpsilon()
        derive(RuleTail).to(or.many(Symbol.class)).or(orNone). // RuleTail ::= or(Symbol*) | orNone()
        derive(RuleItem)//
        	.to(noneOrMore.with(RuleItem))//
        	.or(oneOrMore.with(RuleItem))//
        	.or(twoOrMore.with(RuleItem))//
        	.or(optional.with(RuleItem))//
        	.or(MySymbol)//
        	.
        derive(MySymbol).to(__.with(Symbol.class)).//
        build();
    // @formatter:on
  }
}
