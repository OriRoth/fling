package fling.compiler.ast.nodes;

public interface ClassNode {
  default boolean isConcrete() {
    return this instanceof ConcreteClassNode;
  }
  default boolean isAbstract() {
    return this instanceof AbstractClassNode;
  }
  default ConcreteClassNode asConcrete() {
    return (ConcreteClassNode) this;
  }
  default AbstractClassNode asAbstract() {
    return (AbstractClassNode) this;
  }
}
