package automaton.seminar;

public class DPA {
  interface q0 {
    q0 a();
    q1 b();
  }

  interface q1 {
    q1 b();
    q2 c();
  }

  interface q2 {
    q2 c();
  }
}
