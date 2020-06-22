package il.ac.technion.cs.fling.examples.usecases;
import static il.ac.technion.cs.fling.examples.generated.TableMaker.row;
public class TableMaker {
  public static void main(final String[] args) {
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
