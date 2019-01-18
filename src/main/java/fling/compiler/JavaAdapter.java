package fling.compiler;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.stream.Collectors;

import fling.compiler.ast.FluentAPINode;
import fling.compiler.ast.MethodNode;
import fling.compiler.ast.PolymorphicTypeNode;
import fling.sententials.Word;

public class JavaAdapter<Q, Σ, Γ> implements PolymorphicAdapter<Q, Σ, Γ> {
  private final String packageName;
  private final String className;
  private final String startMethodName;
  private final String terminationMethodName;

  public JavaAdapter(String packageName, String className, String startMethodName, String terminationMethodName) {
    this.startMethodName = startMethodName;
    this.terminationMethodName = terminationMethodName;
    this.packageName = packageName;
    this.className = className;
  }
  @Override public String printTopType() {
    return "$";
  }
  @Override public String printBotType() {
    return "ø";
  }
  @Override public String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name) {
    return printTypeName(name);
  }
  @Override public String printIntermediateType(Compiler<Q, Σ, Γ>.TypeName name,
      List<PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName>> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(t -> printType(t)).collect(joining(",")));
  }
  @Override public String printStartMethod(PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("public static %s %s() {return null;}", printType(returnType), startMethodName);
  }
  @Override public String printTerminationMethod() {
    return String.format("void %s();", terminationMethodName);
  }
  @Override public String printIntermediateMethod(Compiler<Q, Σ, Γ>.MethodDeclaration declaration,
      PolymorphicTypeNode<Compiler<Q, Σ, Γ>.TypeName> returnType) {
    return String.format("%s %s();", printType(returnType), declaration.name);
  }
  @Override public String printTopInterface() {
    return String.format("interface ${void %s();}", terminationMethodName);
  }
  @Override public String printBotInterface() {
    return "interface ø {}";
  }
  @Override public String printInterface(Compiler<Q, Σ, Γ>.InterfaceDeclaration declaration,
      List<MethodNode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration>> methods) {
    return String.format("interface %s{%s}", //
        printInterfaceDeclaration(declaration), //
        methods.stream().map(m -> printMethod(m)).collect(joining()));
  }
  @Override public String printFluentAPI(
      FluentAPINode<Compiler<Q, Σ, Γ>.TypeName, Compiler<Q, Σ, Γ>.MethodDeclaration, Compiler<Q, Σ, Γ>.InterfaceDeclaration> fluentAPI) {
    return String.format("package %s;@SuppressWarnings(\"all\")public interface %s{%s%s}", //
        packageName, //
        className, //
        fluentAPI.startMethods.stream().map(m -> printMethod(m)).collect(joining()),
        fluentAPI.interfaces.stream().map(i -> printInterface(i)).collect(joining()));
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
