package fling.compilers.ast;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.*;

import fling.grammars.BNF;
import fling.internal.compiler.ast.nodes.*;
import fling.internal.grammar.sententials.*;

public class ASTCompiler {
  public final BNF bnf;

  public ASTCompiler(final BNF bnf) {
    this.bnf = bnf;
  }
  public ASTCompilationUnitNode compileAST() {
    final Map<Variable, List<Variable>> parents = new LinkedHashMap<>();
    final Map<Variable, List<Variable>> children = new LinkedHashMap<>();
    final Map<Variable, List<Symbol>> fields = new LinkedHashMap<>();
    for (final Variable v : bnf.V) {
      if (Constants.S == v)
        continue;
      final List<SententialForm> rhs = bnf.rhs(v);
      if (rhs.size() == 1 && (rhs.get(0).size() != 1 || !rhs.get(0).get(0).isVariable()))
        // Sequence rule.
        fields.put(v, rhs.get(0));
      else {
        // Alteration rule.
        children.put(v, new ArrayList<>());
        for (final SententialForm sf : rhs)
          for (final Symbol symbol : sf) {
            assert symbol.isVariable();
            final Variable child = symbol.asVariable();
            children.get(v).add(child);
            if (!parents.containsKey(child))
              parents.put(child, new ArrayList<>());
            parents.get(child).add(v);
          }
      }
    }
    final Map<Variable, ClassNode> classes = new LinkedHashMap<>();
    for (final Variable v : bnf.V)
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
    for (final Variable v : bnf.V) {
      if (Constants.S == v)
        continue;
      final ClassNode classNode = classes.get(v);
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
