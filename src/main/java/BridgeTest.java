import org.junit.jupiter.api.Test;
class Foo<T> {
  T inner;
  void set(T inner) {
    this.inner = inner;
  }
}
class MyFoo extends Foo<Integer> {
  @Override void set(Integer inner) {
    System.out.println("MyFoo.set");
    super.set(inner);
  }
}
public class BridgeTest {
  public @Test void main() {
    System.err.println("Starting");
    MyFoo my = new MyFoo(); // No raw type warning
    my.set(Integer.valueOf(5)); // OK
    Foo f = my; // Raw type warning
    f.set("Hello"); // No compile/runtime error
    Integer x = my.inner; // Runtime exception:
    System.out.printf("x = %d\n", x);
  }
}