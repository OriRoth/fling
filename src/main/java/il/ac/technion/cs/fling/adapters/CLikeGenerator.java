package il.ac.technion.cs.fling.adapters;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodParameter;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded.Leaf;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Grounded.Leaf.InnerNode;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type.Name.q.α.β;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
public abstract class CLikeGenerator extends APIGenerator {
  @Override public final String toString(Type.Name.q q) {
    return q.q.name();
  }
  @Override public final String toString(Type.Name.q.α α) {
    return toString(α.outer()) + "_" + α.α.stream().map(Named::name).collect(Collectors.joining());
  }
  @Override public String toString(β β) {
    return toString(β.outer()) + "_" + β.β.stream().map(Named::name).collect(Collectors.joining());
  }
  CLikeGenerator(Linker namer) {
    super(namer);
  }
  @Override final String comment(String text) {
    return String.format("/* %s */", text);
  }
  @Override final String render(Stream<MethodParameter> ps) {
    return ps.map(p -> p.type + " " + p.name).collect(joining(", "));
  }
  @Override public String renderInstnatiation(Type.Name name, List<Type.Grounded> arguments) {
    return String.format("%s <%>", render(name), arguments.stream().map(this::render).collect(joining(", ")));
  }
  @Override String fullName(Type t) {
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
  @Override public final String toString(InnerNode i) {
    return toString(i.outer()) + String.format("<%s>", i.arguments().map(this::render).collect(joining(", ")));
  }
  @Override public final String toString(Leaf i) {
    return render(i.name);
  }
}
