package il.ac.technion.cs.fling.internal.util;
public class Counter {
  private int value;
  public int getAndInc() {
    return value++;
  }
}
