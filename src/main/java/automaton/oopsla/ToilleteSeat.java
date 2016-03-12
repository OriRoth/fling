package automaton.oopsla;

public abstract class ToilleteSeat {
  public static Down downSeat(){return null;}
  
  public static interface Down{
    Q1 female();
    Q6 male();
    void $();
  }
  
  public static interface Q1{
    Down urinate();
    Down defecate();
  }
  public static interface Q2{
    Q1 lower();
  }
  public static interface Up{
    Q2 female();
    Q4 male();
    void $();
  }
  public static interface Q4{
    Q6 lower();
    Up urinate();
  }
  public static interface Q5{
    Up urinate();
  }
  public static interface Q6{
    Q5 raise();
    Down defecate();
  }
  public static void main(String[] args) {
    downSeat() //
        .female().urinate()//
        .male().defecate() //
        .male().raise().urinate() // seat is up
        .female().lower().defecate() // seat is down
        .$();
  }
}
