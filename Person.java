public abstract class Person<Mate> {
  public abstract Person mate(Mate m);

  public class Female extends Person<Male> {
    @Override public Person mate(final Person<Mate>.Male m) {
      // TODO Auto-generated method stub
      return null;
    }
  }

  public class Male extends Person<Male> {
    @Override public Person mate(final Female m) {
      return null;
    }
  }
}
