package il.ac.technion.cs.fling.adapters;
import java.util.stream.Collectors;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
/** C++ API adapter.
 *
 * @author Ori Roth */
public class CPPGenerator extends CLikeGenerator {
  public CPPGenerator(final Linker namer) {
    super(namer);
  }
  @Override public void visit(final Model m) {
    ____();
    commentf("Forward declaration of %d types:", m.types.size());
    lines(m.types().map(this::declaration));
    ____();
    commentf("Forward declaration of %d start methods:", m.starts.size());
    lines(m.starts().map(this::declaration));
    ____();
    commentf("Full definition of %d types:", m.types.size());
    m.types().forEach(t -> visit(t));
    ____();
    commentf("Full definition of %d start methods:", m.starts.size());
    m.starts().forEach(this::visit);
    ____();
  }
  private String declaration(Method m) {
    return fullMethodSignature(m) + ";";
  }
  private String declaration(Type t) {
    return fullName(t) + ";";
  }
  private String fullMethodSignature(Method m) {
    return String.format("%s %s(%s)", //
        render(m.type), //
        render(m.name), //
        render(m.parameters())//
    );
  }
  @Override String fullName(Type t) {
    String $ = String.format("struct %s", render(t.name));
    return t.parameters.isEmpty() ? $
        : String.format("template <%s> %s",
            t.parameters().map(q -> "typename " + q.name()).collect(Collectors.joining(", ")), //
            $);
  }
  @Override void visit(Method m) {
    line(fullMethodSignature(m) + " {").indent();
    linef("return %s();", render(m.type));
    unindent().line("}");
  }
  @Override void visit(Type t) {
    line(fullName(t) + " {").indent();
    t.methods().forEach(this::visit);
    unindent().line("};");
  }
}
