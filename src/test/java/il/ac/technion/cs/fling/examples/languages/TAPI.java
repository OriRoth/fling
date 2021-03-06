package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.MySymbol;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.Rule;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.RuleBody;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.RuleItem;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.RuleTail;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Γ.TAPI;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.__;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.bnf;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.derive;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.into;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.noneOrMore;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.oneOrMore;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.optional;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.or;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.orNone;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.specialize;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.start;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.to;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.toEpsilon;
import static il.ac.technion.cs.fling.examples.languages.TAPI.Σ.twoOrMore;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
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
