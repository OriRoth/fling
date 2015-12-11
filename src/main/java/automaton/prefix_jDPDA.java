package automaton;
import static automaton.A.C.build;
//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) 
//begin{full}
class Prefix_A { // Encode automaton ¢$A$¢
  // begin{headers}
  static class L // Encodes set ¢$L\subseteq \Sigma^*$¢, type of accept 
    { /* empty*/ }
  // end{headers}

  // Configuration of the automaton
  
    public interface E { /* Empty configuration */ }
    interface ¤ { /* Error configuration. */ }
  //begin{many}
     interface Cγ1< // Configuration when ¢$\gamma1$¢ is at top
       Rest , JRγ1 , JRγ2 
     > extends 
       // ¢$J\gamma1$¢ must be ¢$\textsf{Rest}$¢.
     // end{many} 
       γ1σ1_Push_γ1γ1<Rest,JRγ1,JRγ2>
       ,γ1σ2_Push_γ2γ2<Rest,JRγ1,JRγ2>
     // begin{many}
     {
     // end{many}
     // begin{many}
     }
     // end{many}
     //begin{many}
     interface Cγ2< // Configuration when ¢$\gamma2$¢ is at top
       Rest , JRγ1 , JRγ2 
     > 
       // ¢$J\gamma2$¢ must be ¢$\textsf{Rest}$¢. 
    { 
     // end{many}
      L $();
      JRγ1 σ2();
  //begin{many}
    }
  // end{many}
  //begin{many}
    static Cγ1<E,¤,¤> build = null;
  // end{many}
    interface γ1σ1_Push_γ1γ1<Rest ,JRγ1 ,JRγ2 >{
      // Sidekick of ¢$\delta(\gamma_1,\sigma_1)=\textsf{push}(\gamma_1,\gamma_1)$¢
      Cγ1<Cγ1<Rest, JRγ1, JRγ2 >, Rest, JRγ2> σ1();
    }
    interface γ1σ2_Push_γ2γ2<Rest ,JRγ1 ,JRγ2 >{
      // Sidekick of ¢$\delta(\gamma_1,\sigma_2)=\textsf{push}(\gamma_2,\gamma_2)$¢
      Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> σ2();
    }
  //end{full}
  //begin{cases}
  static void accepts() {
    build.σ2().$();
    build.σ1().σ2().$(); 
    build.σ1().σ1().σ2().σ2().σ1().σ2().$(); 
  }
  static void rejects() {
    build.$(); 
    build.σ1().$(); 
    build.σ2().σ1().$();
    build.σ2().σ2().$();
    build.σ1().σ2().σ1().$();  
    build.σ1().σ2().σ2().σ1().$();  
    //end{cases}
    build.σ1().σ2().σ2().σ2().σ1().$(); 
    build.σ1().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ1().$();  
    //begin{cases}
  }
  //end{cases}
//begin{full}
}
//end{full}
