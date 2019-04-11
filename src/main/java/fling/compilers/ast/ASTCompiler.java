package fling.compilers.ast;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fling.grammars.BNF;
import fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import fling.internal.compiler.ast.nodes.AbstractClassNode;
import fling.internal.compiler.ast.nodes.ClassNode;
import fling.internal.compiler.ast.nodes.ConcreteClassNode;
import fling.internal.compiler.ast.nodes.FieldNode;
import fling.internal.grammar.sententials.Constants;
import fling.internal.grammar.sententials.SententialForm;
import fling.internal.grammar.sententials.Symbol;
import fling.internal.grammar.sententials.Variable;

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
    Map<Variable, ClassNode> classes = new LinkedHashMap<>();
    for (Variable v : bnf.V)
      if (Constants.S == v)
        continue;
      else if (fields.containsKey(v))
        // Concrete class.
        classes.put(v, new ConcreteClassNode(v, //
            new ArrayList<>(), // To be set later.
            fields.getOrDefault(v, emptyList()).stream() //
                .map(FieldNode::new) //
                .collect(toList())));
      else
        // Abstract class.
        classes.put(v, new AbstractClassNode(v, //
            new ArrayList<>(), // To be set later.
            new ArrayList<>() // To be set later.
        ));
    // Set parents and children:
    for (Variable v : bnf.V) {
      if (Constants.S == v)
        continue;
      ClassNode classNode = classes.get(v);
      if (classNode.isConcrete())
        // Concrete class.
        classNode.asConcrete().parents.addAll(parents.getOrDefault(v, emptyList()).stream() //
            .map(classes::get).map(ClassNode::asAbstract).collect(toList()));
      else {
        // Abstract class.
        classNode.asAbstract().parents.addAll(parents.getOrDefault(v, emptyList()).stream() //
            .map(classes::get).map(ClassNode::asAbstract).collect(toList()));
        classNode.asAbstract().children.addAll(children.getOrDefault(v, emptyList()).stream() //
            .map(classes::get).collect(toList()));
      }
    }
    return new ASTCompilationUnitNode(classes.values(), parents.values().stream().anyMatch(ps -> ps.size() > 1));
  }
}
