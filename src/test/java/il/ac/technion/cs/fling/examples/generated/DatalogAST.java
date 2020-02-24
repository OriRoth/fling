package il.ac.technion.cs.fling.examples.generated;

import java.util.*;

@SuppressWarnings("all")
public interface DatalogAST {
  public class Program {
    public final java.util.List<Statement> statement;

    public Program(java.util.List<Statement> statement) {
      this.statement = statement;
    }
  }

  interface Statement {}

  public class Fact implements Statement {
    public final java.lang.String fact;
    public final java.lang.String[] of;

    public Fact(java.lang.String fact, java.lang.String[] of) {
      this.fact = fact;
      this.of = of;
    }
  }

  interface Rule extends Statement {}

  public class Query implements Statement {
    public final java.lang.String query;
    public final Term[] of;

    public Query(java.lang.String query, Term[] of) {
      this.query = query;
      this.of = of;
    }
  }

  public class Bodyless implements Rule {
    public final java.lang.String always;
    public final Term[] of;

    public Bodyless(java.lang.String always, Term[] of) {
      this.always = always;
      this.of = of;
    }
  }

  public class WithBody implements Rule {
    public final RuleHead ruleHead;
    public final RuleBody ruleBody;

    public WithBody(RuleHead ruleHead, RuleBody ruleBody) {
      this.ruleHead = ruleHead;
      this.ruleBody = ruleBody;
    }
  }

  public class RuleHead {
    public final java.lang.String infer;
    public final Term[] of;

    public RuleHead(java.lang.String infer, Term[] of) {
      this.infer = infer;
      this.of = of;
    }
  }

  public class RuleBody {
    public final FirstClause firstClause;
    public final java.util.List<AdditionalClause> additionalClause;

    public RuleBody(FirstClause firstClause, java.util.List<AdditionalClause> additionalClause) {
      this.firstClause = firstClause;
      this.additionalClause = additionalClause;
    }
  }

  public class FirstClause {
    public final java.lang.String when;
    public final Term[] of;

    public FirstClause(java.lang.String when, Term[] of) {
      this.when = when;
      this.of = of;
    }
  }

  public class AdditionalClause {
    public final java.lang.String and;
    public final Term[] of;

    public AdditionalClause(java.lang.String and, Term[] of) {
      this.and = and;
      this.of = of;
    }
  }

  interface Term {}

  public class Term1 implements Term {
    public final java.lang.String l;

    public Term1(java.lang.String l) {
      this.l = l;
    }
  }

  public class Term2 implements Term {
    public final java.lang.String v;

    public Term2(java.lang.String v) {
      this.v = v;
    }
  }

  public static class Visitor {
    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Program program) {
      try {
        this.whileVisiting(program);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      program
          .statement
          .stream()
          .forEach(
              _x_ -> visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement) _x_));
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.Statement statement) {
      if (statement instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact) statement);
      else if (statement instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Rule)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Rule) statement);
      else if (statement instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Query)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Query) statement);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact fact) {
      try {
        this.whileVisiting(fact);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Rule rule) {
      if (rule instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless) rule);
      else if (rule instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody) rule);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Query query) {
      try {
        this.whileVisiting(query);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless bodyless) {
      try {
        this.whileVisiting(bodyless);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody withBody) {
      try {
        this.whileVisiting(withBody);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead) withBody.ruleHead);
      visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody) withBody.ruleBody);
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead ruleHead) {
      try {
        this.whileVisiting(ruleHead);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody ruleBody) {
      try {
        this.whileVisiting(ruleBody);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
      visit(
          (il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause) ruleBody.firstClause);
      ruleBody
          .additionalClause
          .stream()
          .forEach(
              _x_ ->
                  visit(
                      (il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause)
                          _x_));
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause firstClause) {
      try {
        this.whileVisiting(firstClause);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause additionalClause) {
      try {
        this.whileVisiting(additionalClause);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Term term) {
      if (term instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1) term);
      else if (term instanceof il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2)
        visit((il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2) term);
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1 term1) {
      try {
        this.whileVisiting(term1);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public final void visit(il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2 term2) {
      try {
        this.whileVisiting(term2);
      } catch (java.lang.Exception __) {
        __.printStackTrace();
      }
    }

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.DatalogAST.Program program)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.DatalogAST.Fact fact)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.DatalogAST.Query query)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.Bodyless bodyless)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.WithBody withBody)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleHead ruleHead)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.RuleBody ruleBody)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.FirstClause firstClause)
        throws java.lang.Exception {}

    public void whileVisiting(
        il.ac.technion.cs.fling.examples.generated.DatalogAST.AdditionalClause additionalClause)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.DatalogAST.Term1 term1)
        throws java.lang.Exception {}

    public void whileVisiting(il.ac.technion.cs.fling.examples.generated.DatalogAST.Term2 term2)
        throws java.lang.Exception {}
  }
}

