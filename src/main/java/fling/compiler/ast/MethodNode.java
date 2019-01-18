package fling.compiler.ast;

public class MethodNode<T, D> {
  public final D declaration;
  public final PolymorphicTypeNode<T> returnType;

  public MethodNode(D declaration, PolymorphicTypeNode<T> returnType) {
    this.declaration = declaration;
    this.returnType = returnType;
  }
  public static <D> D specialDeclaration() {
    return null;
  }
  public boolean isSpecial() {
    return declaration == null;
  }
}
