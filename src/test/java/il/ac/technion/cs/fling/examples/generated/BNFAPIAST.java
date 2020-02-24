package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface BNFAPIAST {
  public class PlainBNF {
    public final il.ac.technion.cs.fling.Variable start;
    public final java.util.List<Rule> rule;

    public PlainBNF(il.ac.technion.cs.fling.Variable start, java.util.List<Rule> rule) {
      this.start = start;
      this.rule = rule;
    }
  }

  interface Rule {}

  interface RuleBody {}

  interface RuleTail {}

  public class Rule1 implements Rule {
    public final il.ac.technion.cs.fling.Variable derive;
    public final RuleBody ruleBody;

    public Rule1(il.ac.technion.cs.fling.Variable derive, RuleBody ruleBody) {
      this.derive = derive;
      this.ruleBody = ruleBody;
    }
  }

  public class Rule2 implements Rule {
    public final il.ac.technion.cs.fling.Variable specialize;
    public final il.ac.technion.cs.fling.Variable[] into;

    public Rule2(
        il.ac.technion.cs.fling.Variable specialize, il.ac.technion.cs.fling.Variable[] into) {
      this.specialize = specialize;
      this.into = into;
    }
  }

  public class RuleBody1 implements RuleBody {
    public final il.ac.technion.cs.fling.Symbol[] to;
    public final java.util.List<RuleTail> ruleTail;

    public RuleBody1(il.ac.technion.cs.fling.Symbol[] to, java.util.List<RuleTail> ruleTail) {
      this.to = to;
      this.ruleTail = ruleTail;
    }
  }

  public class RuleBody2 implements RuleBody {
    public RuleBody2() {}
  }

  public class RuleTail1 implements RuleTail {
    public final il.ac.technion.cs.fling.Symbol[] or;

    public RuleTail1(il.ac.technion.cs.fling.Symbol[] or) {
      this.or = or;
    }
  }

  public class RuleTail2 implements RuleTail {
    public RuleTail2() {}
  }

  public static class Visitor {
    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.PlainBNF plainBNF) {
      try {
        this.whileVisiting(plainBNF);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      plainBNF
          .rule
          .stream()
          .forEach(_x_ -> visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule) _x_));
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule rule) {
      if (rule instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1) rule);
      else if (rule instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2) rule);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody ruleBody) {
      if (ruleBody instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1) ruleBody);
      else if (ruleBody instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2) ruleBody);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail ruleTail) {
      if (ruleTail instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1) ruleTail);
      else if (ruleTail instanceof il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2)
        visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2) ruleTail);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1 rule1) {
      try {
        this.whileVisiting(rule1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody) rule1.ruleBody);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2 rule2) {
      try {
        this.whileVisiting(rule2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1 ruleBody1) {
      try {
        this.whileVisiting(ruleBody1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      ruleBody1
          .ruleTail
          .stream()
          .forEach(
              _x_ -> visit((il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail) _x_));
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2 ruleBody2) {
      try {
        this.whileVisiting(ruleBody2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1 ruleTail1) {
      try {
        this.whileVisiting(ruleTail1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2 ruleTail2) {
      try {
        this.whileVisiting(ruleTail2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.PlainBNF plainBNF)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule1 rule1)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.BNFAPIAST.Rule2 rule2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody1 ruleBody1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleBody2 ruleBody2)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail1 ruleTail1)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.BNFAPIAST.RuleTail2 ruleTail2)
        throws java.lang.Exception {}
  }
}

