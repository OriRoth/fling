package automaton.oopsla;

public abstract class ToiletteSeat {
  // begin{types}
  public static interface Q0 {
    Q1 female();
    Q6 male();
    void $();
  }
  public static interface Q1 {
    Q0 urinate();
    Q0 defecate();
  }
  public static interface Q2 {
    Q1 lower();
  }
  public static interface Q3 {
    Q2 female();
    Q4 male();
    void $();
  }
  public static interface Q4 {
    Q6 lower();
    Q3 urinate();
  }
  public static interface Q5 {
    Q3 urinate();
  }
  public static interface Q6 {
    Q5 raise();
    Q0 defecate();
  }
  public static class Seat implements Q0 {
    @Override public Q1 female() { /* ... */ }
    // ... 
  }
  // end{types}
  public void usage_example(){
    // begin{legal}
    new Seat().male().raise().urinate();
    new Seat().female().urinate();
    // end{legal}
    // begin{illegal}
    new Seat().female().raise();
    new Seat().male().raise().defecate();
    new Seat().male().male();
    new Seat().male().raise().urinate().female().urinate();
    // end{illegal}
     
  }
  // end{usage}
  
}
