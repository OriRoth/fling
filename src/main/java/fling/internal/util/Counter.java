package fling.internal.util;

public class Counter {
  int value = 0;
  public int getAndInc() {
    return value++;
  }
}
