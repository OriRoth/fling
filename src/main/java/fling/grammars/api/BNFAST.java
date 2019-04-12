package fling.grammars.api;

@SuppressWarnings("all") public interface BNFAST {
  class Specification {
    public final fling.internal.grammar.sententials.Variable start;
    public final java.util.List<Rule> rule;

    public Specification(fling.internal.grammar.sententials.Variable start, java.util.List<Rule> rule) {
      this.start = start;
      this.rule = rule;
    }
  }

  interface Rule {
  }

  interface DerivationTarget {
  }

  class DerivationRule implements Rule {
    public final fling.internal.grammar.sententials.Variable derive;
    public final DerivationTarget derivationTarget;

    public DerivationRule(fling.internal.grammar.sententials.Variable derive, DerivationTarget derivationTarget) {
      this.derive = derive;
      this.derivationTarget = derivationTarget;
    }
  }

  class SpecializationRule implements Rule {
    public final fling.internal.grammar.sententials.Variable specialize;
    public final fling.internal.grammar.sententials.Variable[] into;

    public SpecializationRule(fling.internal.grammar.sententials.Variable specialize,
        fling.internal.grammar.sententials.Variable[] into) {
      this.specialize = specialize;
      this.into = into;
    }
  }

  class ConcreteDerivation implements DerivationTarget {
    public final fling.internal.grammar.sententials.Symbol[] to;

    public ConcreteDerivation(fling.internal.grammar.sententials.Symbol[] to) {
      this.to = to;
    }
  }

  class EpsilonDerivation implements DerivationTarget {
    public EpsilonDerivation() {
    }
  }

  public static class Visitor {
    public final void visit(BNFAST.Specification specification) {
      try {
        this.whileVisiting(specification);
      } catch (Exception __) {
        __.printStackTrace();
      }
      specification.rule.stream().forEach(_x_ -> visit((BNFAST.Rule) _x_));
    }
    public final void visit(BNFAST.Rule rule) {
      if (rule instanceof BNFAST.DerivationRule)
        visit((BNFAST.DerivationRule) rule);
      else if (rule instanceof BNFAST.SpecializationRule)
        visit((BNFAST.SpecializationRule) rule);
    }
    public final void visit(BNFAST.DerivationTarget derivationTarget) {
      if (derivationTarget instanceof BNFAST.ConcreteDerivation)
        visit((BNFAST.ConcreteDerivation) derivationTarget);
      else if (derivationTarget instanceof BNFAST.EpsilonDerivation)
        visit((BNFAST.EpsilonDerivation) derivationTarget);
    }
    public final void visit(BNFAST.DerivationRule rule1) {
      try {
        this.whileVisiting(rule1);
      } catch (Exception __) {
        __.printStackTrace();
      }
      visit((BNFAST.DerivationTarget) rule1.derivationTarget);
    }
    public final void visit(BNFAST.SpecializationRule rule2) {
      try {
        this.whileVisiting(rule2);
      } catch (Exception __) {
        __.printStackTrace();
      }
    }
    public final void visit(BNFAST.ConcreteDerivation derivationTarget1) {
      try {
        this.whileVisiting(derivationTarget1);
      } catch (Exception __) {
        __.printStackTrace();
      }
    }
    public final void visit(BNFAST.EpsilonDerivation derivationTarget2) {
      try {
        this.whileVisiting(derivationTarget2);
      } catch (Exception __) {
        __.printStackTrace();
      }
    }
    public void whileVisiting(BNFAST.Specification specification) throws Exception {
    }
    public void whileVisiting(BNFAST.DerivationRule rule1) throws Exception {
    }
    public void whileVisiting(BNFAST.SpecializationRule rule2) throws Exception {
    }
    public void whileVisiting(BNFAST.ConcreteDerivation derivationTarget1) throws Exception {
    }
    public void whileVisiting(BNFAST.EpsilonDerivation derivationTarget2) throws Exception {
    }
  }
}
