package fling.compiler.ast;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fling.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.compiler.ast.nodes.AbstractClassNode;
import fling.compiler.ast.nodes.ClassNode;
import fling.compiler.ast.nodes.ConcreteClassNode;
import fling.compiler.ast.nodes.FieldNode;
import fling.grammar.BNF;
import fling.grammar.sententials.Constants;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;

public class ASTCompiler {
  public final BNF bnf;

  public ASTCompiler(BNF bnf) {
    this.bnf = bnf;
  }
  public ASTCompilationUnitNode compileAST() {
    Map<Variable, List<Variable>> parents = new LinkedHashMap<>();
    Map<Variable, List<Variable>> children = new LinkedHashMap<>();
    Map<Variable, List<Symbol>> fields = new LinkedHashMap<>();
    for (Variable v : bnf.V) {
      if (Constants.S == v)
        continue;
      List<SententialForm> rhs = bnf.rhs(v);
      if (rhs.size() == 1 && (rhs.get(0).size() != 1 || !rhs.get(0).get(0).isVariable())) {
        // Sequence rule.
        fields.put(v, rhs.get(0));
      } else {
        // Alteration rule.
        children.put(v, new ArrayList<>());
        for (SententialForm sf : rhs)
          for (Symbol symbol : sf) {
            assert symbol.isVariable();
            Variable child = symbol.asVariable();
            children.get(v).add(child);
            if (!parents.containsKey(child))
              parents.put(child, new ArrayList<>());
            parents.get(child).add(v);
          }
      }
    }
    List<ClassNode> classes = new ArrayList<>();
    for (Variable v : bnf.V)
      if (Constants.S == v)
        continue;
      else if (fields.containsKey(v))
        // Concrete class.
        classes.add(new ConcreteClassNode(v, parents.get(v), fields.get(v).stream().map(FieldNode::new).collect(toList())));
      else
        // Abstract class.
        classes.add(new AbstractClassNode(v, parents.get(v), children.get(v)));
    return new ASTCompilationUnitNode(classes, parents.values().stream().anyMatch(ps -> ps.size() > 1));
  }
}
