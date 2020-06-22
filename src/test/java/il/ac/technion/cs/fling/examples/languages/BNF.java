package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.BNF.Γ.PlainBNF;
import static il.ac.technion.cs.fling.examples.languages.BNF.Γ.Rule;
import static il.ac.technion.cs.fling.examples.languages.BNF.Γ.RuleBody;
import static il.ac.technion.cs.fling.examples.languages.BNF.Γ.RuleTail;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.bnf;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.derive;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.into;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.or;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.orNone;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.specialize;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.start;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.to;
import static il.ac.technion.cs.fling.examples.languages.BNF.Σ.toEpsilon;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class BNF implements FluentLanguageAPI<BNF.Σ, BNF.Γ> {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone
  }
  public enum Γ implements Variable {
    PlainBNF, Rule, RuleBody, RuleTail
  }
  @Override public String name() {
    return "BNFAPI";
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    // @formatter:off
    return bnf(). //
        start(PlainBNF). //
        derive(PlainBNF).to(bnf, start.with(Variable.class), noneOrMore(Rule)). // PlainBNF ::= start(Symbol) Rule*
        derive(Rule).to(derive.with(Variable.class), RuleBody). // Rule ::= derive(Variable) RuleBody
        derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). // Rule ::= specialize(Variable) into(Variable*)
        derive(RuleBody).to(to.many(Component.class), noneOrMore(RuleTail)).or(toEpsilon). // RuleBody ::= to(Symbol*) RuleTail* | toEpsilon()
        derive(RuleTail).to(or.many(Component.class)).or(orNone). // RuleTail ::= or(Symbol*) | orNone()
        build();
    // @formatter:on
  }
}
