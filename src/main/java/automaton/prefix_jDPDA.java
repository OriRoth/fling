package automaton;
import static automaton.Prefix_A.A.build;

import automaton.Prefix_A.A.L;
import automaton.Prefix_A.A.Cγ1;
import automaton.Prefix_A.A.Cγ2;
import automaton.Prefix_A.A.E;
import automaton.Prefix_A.A.¤;
import automaton.Prefix_A.A.γ1σ1_Push_γ1γ1γ2;
import automaton.Prefix_A.A.γ2σ1_Push_γ2γ2;
//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) 
class Prefix_A { 
//begin{full}
static class A { // Encode automaton ¢$A$¢
  //begin{configuration}
  static class L // Encodes set ¢$L\subseteq \Sigma^*$¢, type of accept 
    { /* empty */  }
  public interface E { /* Empty stack configuration */ }
  interface ¤ { /* Error configuration. */ }
  //end{configuration}
  // Configuration of the automaton
  //begin{many}
  interface Cγ1< // Configuration when ¢$\gamma_1$¢ is at top
    Rest, JRγ1, JRγ2
  > extends 
    γ1σ1_Push_γ1γ1γ2<Rest,JRγ1,JRγ2,Cγ1<Rest, JRγ1, JRγ2>>
  {
  //end{many}
    L $();
  //begin{many}
  }
  //end{many}
  //begin{many}
  interface Cγ2< // Configuration when ¢$\gamma_2$¢ is at top
    Rest, JRγ1, JRγ2
  > extends 
    γ2σ1_Push_γ2γ2<Rest,JRγ1,JRγ2>
  { 
  //end{many}
    Rest σ2();
    JRγ1 σ3();
  //begin{many}
  }
  //end{many}
  interface γ1σ1_Push_γ1γ1γ2<Rest,JRγ1,JRγ2,P extends Cγ1<Rest, JRγ1, JRγ2 >>{
    // Sidekick of ¢$\delta(\gamma_1,\sigma_1)=\textsf{push}(\gamma_1,\gamma_1,\gamma_2)$¢
    Cγ2<Cγ1<P, Rest, JRγ2>,P,JRγ2> σ1();
  }
  interface γ2σ1_Push_γ2γ2<Rest,JRγ1,JRγ2>{
    // Sidekick of ¢$\delta(\gamma_2,\sigma_1)=\textsf{push}(\gamma_2,\gamma_2)$¢
    Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> σ1();
  }
  //begin{many}
  static Cγ1<E,¤,¤> build = null;
  //end{many}
  //end{full}
  //begin{cases}
  static void accepts() {
    A.build.$();
    A.build.σ1().σ3().$();
    A.build.σ1().σ2().$(); 
    A.build.σ1().σ1().σ2().σ3().σ1().σ2().$(); 
  }
  static void rejects() {
    A.build.σ1().$();
    A.build.σ2();
    A.build.σ1().σ2().σ3();
    A.build.σ1().σ1().σ2().σ3().σ1().$();  
    //end{cases}
    A.build.σ1().σ2().σ2().σ2().σ1().$(); 
    A.build.σ1().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ1().$();  
    //begin{cases}
  }
  //end{cases}
//begin{full}
}
  //end{full}
}