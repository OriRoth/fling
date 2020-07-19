package il.ac.technion.cs.fling.adapters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.*;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded.Leaf;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded.Leaf.InnerNode;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Name.q.α.β;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
public abstract class CLikeGenerator extends APIGenerator {
  @Override public final String toString(final Type.Name.q q) {
    return q.q.name();
  }
  @Override public final String toString(final Type.Name.q.α α) {
    return toString(α.outer()) + "_" + α.α.stream().map(Named::name).collect(Collectors.joining());
  }
  @Override public String toString(final β β) {
    return toString(β.outer()) + "_" + β.β.stream().map(Named::name).collect(Collectors.joining());
  }
  CLikeGenerator(final Linker namer) {
    super(namer);
  }
  @Override final String comment(final String text) {
    return String.format("/* %s */", text);
  }
  @Override final String render(final Stream<MethodParameter> ps) {
    return ps.map(p -> p.type + " " + p.name).collect(commas());
  }
  @Override public String renderInstnatiation(final Type.Name name, final List<? extends Type.Grounded> arguments) {
    return String.format("%s <%s>", render(name), arguments.stream().map(this::render).collect(commas()));
  }
  @Override String fullName(final Type t) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override public String renderBottomTypeName() {
    return bottomName();
  }
  @Override public String renderEndTypeName() {
    return "void";
  }
  @Override public final String renderTopTypeName() {
    return topName();
  }
  @Override public final String toString(final InnerNode i) {
    return toString(i.outer()) + String.format("<%s>", i.arguments().map(this::render).collect(commas()));
  }
  @Override public final String toString(final Leaf i) {
    return render(i.name);
  }
  final String fullMethodSignature(final Method m) {
    return String.format("%s %s(%s)", render(m.type), render(m.name), render(m.parameters()));
  }
  final String methodInvocation(final Method m) {
    return String.format("%s(%s)", render(m.name), m.parameters().map(p -> p.name).collect(commas()));
  }
}
