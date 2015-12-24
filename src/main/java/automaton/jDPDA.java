package automaton;
import static automaton.A.build;

import automaton.A.C.Cγ1;
import automaton.A.C.Cγ2;
import automaton.A.C.E;
import automaton.A.C.¤;
//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) 
//begin{full}
class A { // Encode automaton ¢$A$¢
  //begin{headers}
  private static class ΣΣ   // Encodes set ¢$\Sigma^*$¢, type of reject
    { /*  empty*/ } 
  static class L extends ΣΣ // Encodes set ¢$L\subseteq \Sigma^*$¢, type of accept 
    { /* empty*/ }
  //end{headers}
  // Configuration of the automaton
  //begin{configuration}
  interface C<      // Generic parameters:
    Rest extends C, // The rest of the stack, for pop or ¢$\textsf{jump}(\gamma)$¢ operations
    JRγ1 extends C, // Type of ¢$~\cc{Rest}.\textsf{jump}(\gamma_1)$¢, may be rest, or anything in it. 
    JRγ2 extends C  // Type of ¢$~\cc{Rest}.\textsf{jump}(\gamma_2)$¢, may be rest, or anything in it.  
  >
  {
    ΣΣ $();        // δ transition on end of input; invalid language by default 
    C σ1();         // δ transition on ¢$\sigma_1$¢; dead end by default
    C σ2();         // δ transition on ¢$\sigma_2$¢; dead end by default
    C σ3();         // δ transition on ¢$\sigma_3$¢; dead end by default
    public interface E extends C<¤,¤,¤> { /* Empty stack configuration */ }
    interface ¤ extends C<¤,¤,¤> { /* Error configuration. */ }
    //end{configuration}
    //begin{many}
    interface Cγ1< // Configuration when ¢$\gamma_1$¢ is at top
      Rest extends C, JRγ1 extends C, JRγ2 extends C
    > extends 
      C<Rest, JRγ1, JRγ2>   
    //end{many} 
      ,γ1σ1_Push_γ1γ1γ2<Rest,JRγ1,JRγ2,Cγ1<Rest, JRγ1, JRγ2>>
    //begin{many}
    {
    //end{many}
      @Override L $();
    //begin{many}
    }
    //end{many}
    //begin{many}
    interface Cγ2< // Configuration when ¢$\gamma_2$¢ is at top
      Rest extends C, JRγ1 extends C, JRγ2 extends C
    > extends 
      C<Rest, JRγ1, JRγ2>
    //end{many}
      ,γ2σ1_Push_γ2γ2<Rest,JRγ1,JRγ2>
    //begin{many}
    { 
    //end{many}
      @Override Rest σ2();
      @Override JRγ1 σ3();
    //begin{many}
    }
    //end{many}
    interface γ1σ1_Push_γ1γ1γ2<Rest extends C,JRγ1 extends C,JRγ2 extends C,P extends Cγ1<Rest, JRγ1, JRγ2 >>{
      // Sidekick of ¢$\delta(\gamma_1,\sigma_1)=\textsf{push}(\gamma_1,\gamma_1,\gamma_2)$¢
      Cγ2<Cγ1<P, Rest, JRγ2>,P,JRγ2> σ1();
    }
    interface γ2σ1_Push_γ2γ2<Rest extends C,JRγ1 extends C,JRγ2 extends C>{
      // Sidekick of ¢$\delta(\gamma_2,\sigma_1)=\textsf{push}(\gamma_2,\gamma_2)$¢
      Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> σ1();
    }
  }
  //begin{many}
  static Cγ1<E,¤,¤> build = null;
  //end{many}
  //end{full}
  //begin{cases}
  static void isL(L l) {/**/}
  static void accepts() {
    isL(A.build.$());
    isL(A.build.σ1().σ3().$());
    isL(A.build.σ1().σ2().$()); 
    isL(A.build.σ1().σ1().σ2().σ3().σ1().σ2().$()); 
  }
  static void rejects() {
    isL(A.build.σ1().$()); 
    isL(A.build.σ2().σ1().$());
    isL(A.build.σ2().σ2().$());
    isL(A.build.σ1().σ2().σ1().$());  
    isL(A.build.σ1().σ2().σ2().σ1().$());  
    //end{cases}
    isL(A.build.σ1().σ2().σ2().σ2().σ1().$()); 
    isL(A.build.σ1().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ1().$());  
    //begin{cases}
  }
  //end{cases}
//begin{full}
//begin{configuration}
}
//end{configuration}
//end{full}
