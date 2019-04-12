package fling.grammars.api;

import fling.Variable;

@SuppressWarnings("all") public interface BNFAPIAST {
  class PlainBNF {
    public final Variable start;
    public final java.util.List<Rule> rule;

    public PlainBNF(Variable start, java.util.List<Rule> rule) {
      this.start = start;
      this.rule = rule;
    }
  }

  interface Rule {
  }

  interface RuleBody {
  }

  interface RuleTail {
  }

  class Derivation implements Rule {
    public final Variable derive;
    public final RuleBody ruleBody;

    public Derivation(Variable derive, RuleBody ruleBody) {
      this.derive = derive;
      this.ruleBody = ruleBody;
    }
  }

  class Specialization implements Rule {
    public final Variable specialize;
    public final Variable[] into;

    public Specialization(Variable specialize, Variable[] into) {
      this.specialize = specialize;
      this.into = into;
    }
  }

  class ConcreteDerivation implements RuleBody {
    public final fling.internal.grammar.sententials.Symbol[] to;
    public final java.util.List<RuleTail> ruleTail;

    public ConcreteDerivation(fling.internal.grammar.sententials.Symbol[] to, java.util.List<RuleTail> ruleTail) {
      this.to = to;
      this.ruleTail = ruleTail;
    }
  }

  class EpsilonDerivation implements RuleBody {
    public EpsilonDerivation() {
    }
  }

  class ConcreteDerivationTail implements RuleTail {
    public final fling.internal.grammar.sententials.Symbol[] or;

    public ConcreteDerivationTail(fling.internal.grammar.sententials.Symbol[] or) {
      this.or = or;
    }
  }

  class EpsilonDerivationTail implements RuleTail {
    public EpsilonDerivationTail() {
    }
  }

  public static class Visitor {
    public final void visit(PlainBNF plainBNF) {
      try {
        this.whileVisiting(plainBNF);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      plainBNF.rule.stream().forEach(_x_ -> visit((Rule) _x_));
    }
    public final void visit(Rule rule) {
      if (rule instanceof Derivation)
        visit((Derivation) rule);
      else if (rule instanceof Specialization)
        visit((Specialization) rule);
    }
    public final void visit(RuleBody ruleBody) {
      if (ruleBody instanceof ConcreteDerivation)
        visit((ConcreteDerivation) ruleBody);
      else if (ruleBody instanceof EpsilonDerivation)
        visit((EpsilonDerivation) ruleBody);
    }
    public final void visit(RuleTail ruleTail) {
      if (ruleTail instanceof ConcreteDerivationTail)
        visit((ConcreteDerivationTail) ruleTail);
      else if (ruleTail instanceof EpsilonDerivationTail)
        visit((EpsilonDerivationTail) ruleTail);
    }
    public final void visit(Derivation rule1) {
      try {
        this.whileVisiting(rule1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((RuleBody) rule1.ruleBody);
    }
    public final void visit(Specialization rule2) {
      try {
        this.whileVisiting(rule2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }
    public final void visit(ConcreteDerivation ruleBody1) {
      try {
        this.whileVisiting(ruleBody1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      ruleBody1.ruleTail.stream().forEach(_x_ -> visit((RuleTail) _x_));
    }
    public final void visit(EpsilonDerivation ruleBody2) {
      try {
        this.whileVisiting(ruleBody2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }
    public final void visit(ConcreteDerivationTail ruleTail1) {
      try {
        this.whileVisiting(ruleTail1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }
    public final void visit(EpsilonDerivationTail ruleTail2) {
      try {
        this.whileVisiting(ruleTail2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }
    public void whileVisiting(PlainBNF plainBNF) throws java.lang.Exception {
    }
    public void whileVisiting(Derivation rule1) throws java.lang.Exception {
    }
    public void whileVisiting(Specialization rule2) throws java.lang.Exception {
    }
    public void whileVisiting(ConcreteDerivation ruleBody1) throws java.lang.Exception {
    }
    public void whileVisiting(EpsilonDerivation ruleBody2) throws java.lang.Exception {
    }
    public void whileVisiting(ConcreteDerivationTail ruleTail1) throws java.lang.Exception {
    }
    public void whileVisiting(EpsilonDerivationTail ruleTail2) throws java.lang.Exception {
    }
  }
}
