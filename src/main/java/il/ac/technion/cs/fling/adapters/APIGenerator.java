package il.ac.technion.cs.fling.adapters;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Abstract base of all code generators.
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public abstract class APIGenerator extends Indenter {
  private String bottomName = "BOTTOM";
  private String endName = "$";
  private final Linker namer;
  private String topName = "TOP";
  APIGenerator(final Linker namer) {
    this.namer = namer;
  }
  public String bottomName() {
    return bottomName;
  }
  public final String go(final Model m) {
    namer.link(m);
    commentf("BEGIN  ----- following code was automatically generated by Fling(c) on %s", new Date());
    visit(m);
    commentf("END ------------ above code was automatically generated by Fling(c) on %s", new Date());
    assert level() == 0 : contents();
    return contents();
  }
  void commentf(final String format, final Object... os) {
    line(comment(String.format(format, os)));
  }
  void commentf(final String format, final int i) {
    commentf(format, (Object) Integer.valueOf(i));
  }
  void commentf(final String format, final long l) {
    commentf(format, (Object) Long.valueOf(l));
  }
  void commentf(final String format, final long l1, final long l2) {
    commentf(format, (Object) Long.valueOf(l1), (Object) Long.valueOf(l2));
  }
  public abstract String renderEndTypeName();
  public abstract void visit(Model m);
  public abstract String renderBottomTypeName();
  public abstract String renderTopTypeName();
  abstract void visit(Method m);
  abstract void visit(Type t);
  abstract String comment(String text);
  abstract String render(Stream<MethodParameter> ps);
  public String render(final Type.Name n) {
    return n.render(this);
  }
  protected final String render(final Type.Grounded i) {
    return i.render(this);
  }
  public String topName() {
    return topName;
  }
  final void endName(@SuppressWarnings("hiding") final String endName) {
    this.endName = endName;
  }
  void bottomName(@SuppressWarnings("hiding") final String bottomName) {
    this.bottomName = bottomName;
  }
  String endName() {
    return endName;
  }
  void topName(@SuppressWarnings("hiding") final String topName) {
    this.topName = topName;
  }
  public abstract String renderInstnatiation(Type.Name name, List<Type.Grounded> arguments);
  abstract String fullName(Type t);
  public abstract String toString(Type.Name.q q);
  public abstract String toString(Type.Name.q.α α);
  public abstract String toString(Type.Name.q.α.β β);
  public abstract String toString(Type.Grounded.Leaf.InnerNode i);
  public abstract String toString(Type.Grounded.Leaf i);
  protected final String render(final Token t) {
    return t.name();
  }
}