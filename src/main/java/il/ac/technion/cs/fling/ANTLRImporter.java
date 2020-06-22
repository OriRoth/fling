package il.ac.technion.cs.fling;
import static il.ac.technion.cs.fling.internal.grammar.rules.Constants.intermediateVariableName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.PlusBlockAST;
import org.antlr.v4.tool.ast.RuleRefAST;
import org.antlr.v4.tool.ast.StarBlockAST;
import org.antlr.v4.tool.ast.TerminalAST;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.util.Counter;
/** Class to convert an ANTLR grammar into {@link EBNF}
 *
 * @author Yossi Gil
 * @since 2020-06-08 */
public class ANTLRImporter extends EBNF.Builder {
  private final Grammar grammar;
  private final EBNF ebnf;
  private final Counter nameCounter = new Counter();
  /** @return the ANTLR grammar */
  public Grammar getGrammar() {
    return grammar;
  }
  /** @return the ebnf */
  public EBNF getEbnf() {
    return ebnf;
  }
  public ANTLRImporter(final Grammar grammar) {
    this.grammar = grammar;
    if (grammar.ast.getChildCount() != 2)
      throw new RuntimeException("ANTLR grammar is not simplified");
    ebnf = go();
  }
  private EBNF go() {
    boolean initialized = false;
    final Tree rules = grammar.ast.getChild(1);
    for (int i = 0; i < rules.getChildCount(); ++i) {
      final Tree rule = rules.getChild(i);
      assert rule.getChildCount() == 2;
      final String variableName = rule.getChild(0).getText();
      final Variable variable = Variable.byName(variableName);
      if (!initialized) {
        // Assume first ANTLR variable is start variable.
        start(variable);
        initialized = true;
      }
      final Optional<Component> rhs = convertBody(rule.getChild(1));
      if (rhs.isPresent())
        derive(variable).to(rhs.get());
      else
        derive(variable).toEpsilon();
    }
    return build();
  }
  private Optional<Component> convertBody(final Object element) {
    if (element instanceof List)
      return convertList((List<?>) element);
    if (element instanceof AltAST)
      return convertList(((AltAST) element).getChildren());
    if (element instanceof BlockAST)
      return convertBlock((BlockAST) element);
    if (element instanceof RuleRefAST)
      return Optional.of(Variable.byName(element.toString()));
    if (element instanceof StarBlockAST) {
      final Optional<Component> inner = convertList(((StarBlockAST) element).getChildren());
      return inner.map(Quantifiers::noneOrMore);
    }
    if (element instanceof PlusBlockAST) {
      final Optional<Component> inner = convertList(((PlusBlockAST) element).getChildren());
      return inner.map(Quantifiers::oneOrMore);
    }
    if (element instanceof TerminalAST) {
      String name = ((TerminalAST) element).getText();
      name = name.substring(1, name.length() - 1);
      // Assume simple terminal.
      return Optional.of(Terminal.byName(name).normalize());
    }
    throw new RuntimeException(String.format("Grammar element %s unsupported", element.getClass().getSimpleName()));
  }
  private Optional<Component> convertBlock(final BlockAST block) {
    if (block.getChildCount() <= 1)
      return convertBody(block.getChildren());
    final Variable top = newVariable();
    final List<Component> items = new ArrayList<>();
    for (final Object item : block.getChildren())
      convertBody(item).ifPresent(items::add);
    items.forEach(symbol -> derive(top).to(symbol));
    return Optional.of(top);
  }
  private Variable newVariable() {
    return Variable.byName(intermediateVariableName + nameCounter.getAndInc());
  }
  private Optional<Component> convertList(final List<?> elements) {
    if (elements.isEmpty())
      return Optional.empty();
    if (elements.size() == 1)
      return convertBody(elements.get(0));
    final Variable top = newVariable();
    final List<Component> items = new ArrayList<>();
    for (final Object item : elements)
      convertBody(item).ifPresent(items::add);
    derive(top).to(items.toArray(new Component[items.size()]));
    return Optional.of(top);
  }
}
