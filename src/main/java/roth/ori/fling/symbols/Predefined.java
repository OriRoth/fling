package roth.ori.fling.symbols;

public class Predefined extends Verb {
  public Predefined(String name, Class<?> type) {
    super(new Terminal() {
      @Override public String name() {
        return name;
      }
    }, type);
  }

  public static Predefined Int = new Predefined("i", int.class);
  public static Predefined Bool = new Predefined("bool", boolean.class);
  public static Predefined Char = new Predefined("c", char.class);
  public static Predefined Real = new Predefined("real", double.class);
  public static Predefined Long = new Predefined("l", long.class);
  public static Predefined Text = new Predefined("text", String.class);
}
