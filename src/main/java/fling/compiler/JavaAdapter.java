package fling.compiler;

import java.util.stream.Collectors;

import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.InterfaceNode;
import fling.compiler.ast.MethodNode;
import fling.compiler.ast.PolymorphicTypeNode;
import fling.sententials.Word;

public class JavaAdapter<Q, Σ, Γ> implements PolymorphicAdapter<Q, Σ, Γ> {
  // TODO Roth: receive these in constructor.
  private static final String JBOT = "B";
  private static final String JTOP = "T";
  private static final String JSTART = "__";
  // TODO Roth: instead get relevant fields in constructor. (?)
  private final Compiler<Q, Σ, Γ> compiler;

  public JavaAdapter(Compiler<Q, Σ, Γ> compiler) {
    this.compiler = compiler;
  }
  public String print(String name) {
    return printFluentAPI(name, compiler.compileFluentAPI());
  }
  @Override public String printType(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> type) {
    return type.isTop() ? JTOP : type.isBot() ? JBOT : type.isLeaf() ? printTypeName(type.name) : //
        String.format("%s<%s>", printTypeName(type.name), //
            type.typeArguments.stream().map(t -> printType(t)).collect(Collectors.joining(",")));
  }
  @Override public String printMethod(MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration> method) {
    return method.isSpecial() ? "void $();" : String.format("%s %s();", printType(method.returnType), method.declaration.name);
  }
  public String printStaticMethod(MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration> method) {
    return String.format("public static %s %s(){return null;}", printType(method.returnType),
        method.isSpecial() ? JSTART : method.declaration.name);
  }
  @Override public String printInterface(
      InterfaceNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> interfaze) {
    return interfaze.isTop() ? "interface T{void $();}" : interfaze.isBot() ? "interface B{}" : String.format("interface %s{%s}", printInterfaceDeclaration(interfaze.declaration), //
        interfaze.methods.stream().map(m -> printMethod(m)).collect(Collectors.joining()));
  }
  @Override public String printFluentAPI(String name,
      FluentAPINode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    return String.format("@SuppressWarnings(\"all\") public interface %s{%s%s}", name,
        fluentAPI.startMethods.stream().map(m -> printStaticMethod(m)).collect(Collectors.joining()),
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(Collectors.joining()));
  }
  public String printTypeName(Compiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name.q, name.α);
  }
  public String printTypeName(Q q, Word<Γ> α) {
    return α == null ? q.toString() : String.format("%s_%s", q, α);
  }
  public String printInterfaceDeclaration(Compiler<Q, Σ, Γ>.InterfaceDeclaration declaration) {
    return String.format("%s<%s>", printTypeName(declaration.q, declaration.α), //
        declaration.typeVariables.stream().map(Object::toString).collect(Collectors.joining(",")));
  }
}
