package automaton;
import static automaton.Prefix_A.A.build;
//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) 
class Prefix_A { 
//begin{full}
  static class A{ // Encode automaton ¢$A$¢
  // Configuration of the automaton
  //begin{configuration}
  static class L { /*Encodes set ¢$L\subseteq \Sigma^*$¢, type of accept*/ } 
  public interface E { /* Empty stack configuration */ }
  interface ¤ { /* Error configuration. */ }
  //end{configuration}
  //begin{many}
  interface Cγ1< // Configuration when ¢$\gamma1$¢ is at top
    Rest , JRγ1 , JRγ2 
  > 
  //end{many} 
  extends 
    γ1σ1_Push_γ1γ1<Rest,JRγ1,JRγ2>
    ,γ1σ2_Push_γ2γ2<Rest,JRγ1,JRγ2>
  //begin{many}
  {
    Cγ1<Cγ1<Rest, JRγ1, JRγ2>, Rest, JRγ2> σ1();
    Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> σ2();
  }
  //end{many}
  //begin{many}
  interface Cγ2< // Configuration when ¢$\gamma2$¢ is at top
    Rest , JRγ1 , JRγ2 
  > 
  { 
    L $();
    JRγ1 σ2();
  }
  //end{many}
//begin{many}
  static Cγ1<E,¤,¤> build = null;
//end{many}
  interface γ1σ1_Push_γ1γ1<Rest ,JRγ1 ,JRγ2 >{
    // Sidekick of ¢$\delta(\gamma_1,\sigma_1)=\textsf{push}(\gamma_1,\gamma_1)$¢
    Cγ1<Cγ1<Rest, JRγ1, JRγ2>, Rest, JRγ2> σ1();
  }
  interface γ1σ2_Push_γ2γ2<Rest ,JRγ1 ,JRγ2 >{
    // Sidekick of ¢$\delta(\gamma_1,\sigma_2)=\textsf{push}(\gamma_2,\gamma_2)$¢
    Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> σ2();
  }
  //end{full}
  //begin{cases}
  static void accepts() {
    A.build.σ2().$();
    A.build.σ1().σ2().$(); 
    A.build.σ1().σ1().σ2().σ2().σ1().σ2().$(); 
  }
  static void rejects() {
    A.build.σ2().σ1();
    A.build.σ2().σ2().σ1();
    A.build.σ1().σ2().σ1();
    A.build.σ1().σ2().σ2().σ1();
  }
  //end{cases}
  }
//begin{full}
}
//end{full}
