package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.*;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
public class TAPI implements FluentLanguageAPI<TAPI.Σ, TAPI.Γ> {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone, oneOrMore, twoOrMore, noneOrMore, optional, __
  }
  public enum Γ implements Variable {
    TAPI, Rule, RuleBody, RuleTail, RuleItem, MySymbol
  }
  @Override public String name() {
    return "EBNFAPI";
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public EBNF BNF() {
    // @formatter:off
    return bnf(). //
        start(TAPI). //
        derive(TAPI).to(bnf, start.with(Variable.class), Quantifiers.oneOrMore(Rule)). // PlainBNF ::= start(Symbol) Rule*
        derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). // Rule ::= specialize(Variable) into(Variable*)
        derive(Rule).to(derive.with(Variable.class), RuleBody). // Rule ::= derive(Variable) RuleBody
        derive(RuleBody).to(to.many(RuleItem), Quantifiers.noneOrMore(RuleTail)).or(toEpsilon). // RuleBody ::= to(RuleItem*) RuleTail* | toEpsilon()
        derive(RuleTail).to(or.many(Component.class)).or(orNone). // RuleTail ::= or(Symbol*) | orNone()
        derive(RuleItem)//
        	.to(noneOrMore.with(RuleItem))//
        	.or(oneOrMore.with(RuleItem))//
        	.or(twoOrMore.with(RuleItem))//
        	.or(optional.with(RuleItem))//
        	.or(MySymbol)//
        	.
        derive(MySymbol).to(__.with(Component.class)).//
        build();
    // @formatter:on
  }
}
