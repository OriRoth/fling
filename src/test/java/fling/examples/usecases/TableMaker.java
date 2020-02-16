package fling.examples.usecases;

import static fling.examples.generated.TableMaker.row;

public class TableMaker {
  public static void main(String[] args) {
    // @formatter:off
    row().
      column().
        cell().
        cell().
        cell().
      seal().
      column().
        cell().
        row().
          cell().
          cell().
        seal().
      seal().
    seal().$();
    // @formatter:on
  }
}
