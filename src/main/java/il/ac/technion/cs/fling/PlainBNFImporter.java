package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.ConcreteDerivation;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.ConcreteDerivationTail;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.Derivation;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.PlainBNF;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.Rule;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.RuleTail;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.Specialization;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** Convert a {@link PlainBNF} into a {@link FancyEBNF}
 * 
 * @author Yossi Gil
 * @since 2020-05-08 */
public class PlainBNFImporter {
  FancyEBNF.Builder  builder = new FancyEBNF.Builder();
  public PlainBNFImporter(PlainBNF bnf) {
    this.bnf = bnf;
    this.ebnf = go();
  }

  private final FancyEBNF ebnf;
  private final PlainBNF bnf;

  private FancyEBNF go() {
    builder.start(bnf.start);
    for (final Rule rule : bnf.rules) {
      if (rule instanceof Derivation) {
        convert((Derivation) rule);
        continue;
      } 
      if (rule instanceof Specialization) {
        convert((Specialization) rule);
        continue;
      }
    }
    try {
      return builder.build();
    } catch (Exception e) {
      throw new RuntimeException(
          bnf + "problem while analyzing BNF, make sure the grammar adheres its class description (LL/LR/etc)", e);
    }
  }

  private void convert(final Specialization specializationRule) {
    builder.specialize(specializationRule.specialize).into(specializationRule.into);
  }

  void convert(final Derivation derivation) {
    Variable variable = derivation.variable;
    if (derivation.ruleBody instanceof ConcreteDerivation) {
      // Concrete derivation rule.
      ConcreteDerivation concrete = (ConcreteDerivation) derivation.ruleBody;
      builder.derive(variable).to((concrete).to);
      for (RuleTail tail : concrete.ruleTail)
        if (tail instanceof ConcreteDerivationTail)
          // Concrete tail.
          builder.derive(variable).to(((ConcreteDerivationTail) tail).or);
        else
          // Epsilon tail.
          builder.derive(variable).toEpsilon();
    } else
      // Epsilon derivation rule.
      builder.derive(variable).toEpsilon();
  }

  public FancyEBNF getEbnf() {
    return ebnf;
  }

}
