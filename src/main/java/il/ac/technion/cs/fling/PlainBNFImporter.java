package il.ac.technion.cs.fling;
import il.ac.technion.cs.fling.grammars.api.BNFAPIAST.*;
/** Convert a {@link PlainBNF} into a {@link EBNF}
 *
 * @author Yossi Gil
 * @since 2020-05-08 */
public class PlainBNFImporter {
  private final EBNF.Builder builder = new EBNF.Builder();
  public PlainBNFImporter(final PlainBNF bnf) {
    this.bnf = bnf;
    ebnf = go();
  }
  private final EBNF ebnf;
  private final PlainBNF bnf;
  private EBNF go() {
    builder.start(bnf.start);
    for (final Rule rule : bnf.rules) {
      if (rule instanceof Derivation) {
        convert((Derivation) rule);
        continue;
      }
      if (rule instanceof Specialization)
        convert((Specialization) rule);
    }
    try {
      return builder.build();
    } catch (final Exception e) {
      throw new RuntimeException(
          bnf + "problem while analyzing BNF, make sure the grammar adheres its class description (LL/LR/etc)", e);
    }
  }
  private void convert(final Specialization specializationRule) {
    builder.specialize(specializationRule.specialize).into(specializationRule.into);
  }
  private void convert(final Derivation derivation) {
    final var variable = derivation.variable;
    if (derivation.ruleBody instanceof ConcreteDerivation concrete) {
      // Concrete derivation rule.
      builder.derive(variable).to(concrete.to);
      for (final RuleTail tail : concrete.ruleTail)
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
  public EBNF getEbnf() {
    return ebnf;
  }
}
