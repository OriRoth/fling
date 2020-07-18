package il.ac.technion.cs.fling.compilers.ast;
import java.util.*;
import static java.util.stream.Collectors.toList;
import static java.util.Collections.emptyList;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.*;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
/** Abstract syntax tree compiler. Generates types corresponding to AST nodes.
 *
 * @author Ori Roth */
public class ASTCompiler {
  /** Input BNF. */
  private final FancyEBNF bnf;
  public ASTCompiler(final FancyEBNF bnf) {
    this.bnf = bnf;
  }
  /** Compiles BNF to AST types.
   *
   * @return */
  public ASTCompilationUnitNode compileAST() {
    final Map<Variable, List<Variable>> parents = new LinkedHashMap<>();
    final Map<Variable, List<Variable>> children = new LinkedHashMap<>();
    final Map<Variable, List<Component>> fields = new LinkedHashMap<>();
    for (final Variable v : bnf.Γ) {
      if (Constants.S == v)
        continue;
      final List<Body> rhs = bnf.bodiesList(v);
      if (rhs.size() == 1 && (rhs.get(0).size() != 1 || !rhs.get(0).get(0).isVariable()))
        // Sequence rule.
        fields.put(v, rhs.get(0));
      else {
        // Alteration rule.
        children.put(v, new ArrayList<>());
        for (final Body sf : rhs)
          for (final Component symbol : sf) {
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
    for (final Variable v : bnf.Γ)
      if (Constants.S == v) {
      }
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
    for (final Variable v : bnf.Γ) {
      if (Constants.S == v)
        continue;
      final ClassNode classNode = classes.get(v);
      if (classNode instanceof ConcreteClassNode)
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
