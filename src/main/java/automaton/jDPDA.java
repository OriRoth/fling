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
  // begin{headers}
  private static class ΣΣ   // Encodes set ¢$\Sigma^*$¢, type of reject
    { /*  empty*/ } 
  static class L extends ΣΣ // Encodes set ¢$L\subseteq \Sigma^*$¢, type of accept 
    { /* empty*/ }
  // end{headers}

  // Configuration of the automaton
  /// begin{configuration}
  interface C<      // Generic parameters:
    Rest extends C, // The rest of the stack, for pop operations 
    JRγ1 extends C, // Type of ¢$~\cc{Rest}.\textsf{jump}(\gamma1)$¢, may be rest, or anything in it. 
    JRγ2 extends C  // Type of ¢$~\cc{Rest}.\textsf{jump}(\gamma2)$¢, may be rest, or anything in it.  
  >
  {
    ΣΣ $();        // δ transition on end of input; invalid language by default 
    C a();         // δ transition on ¢a¢; dead end by default
    C b();         // δ transition on ¢b¢; dead end by default
    C c();         // δ transition on ¢c¢; dead end by default
    public interface E extends C<¤,¤,¤> { /* Empty stack configuration */ }
    interface ¤ extends C<¤,¤,¤> { /* Error configuration. */ }
  // end{configuration}
  //begin{many}
     interface Cγ1< // Configuration when ¢$\gamma1$¢ is at top
       Rest extends C, JRγ1 extends C, JRγ2 extends C
     > extends 
       C<Rest, JRγ1, JRγ2>   
     // end{many} 
       ,γ1a_Push_γ1γ1γ2<Rest,JRγ1,JRγ2>
     // begin{many}
     {
     // end{many}
       @Override L $();
     // begin{many}
     }
     // end{many}
  //begin{many}
     interface Cγ2< // Configuration when ¢$\gamma2$¢ is at top
       Rest extends C, JRγ1 extends C, JRγ2 extends C
     > extends 
       C<Rest, JRγ1, JRγ2>
     // end{many}
       ,γ2a_Push_γ2γ2<Rest,JRγ1,JRγ2>
     // begin{many}
    { 
     // end{many}
       @Override Rest b();
       @Override JRγ1 c();
  //begin{many}
    }
  // end{many}
    interface γ1a_Push_γ1γ1γ2<Rest extends C,JRγ1 extends C,JRγ2 extends C>{
      // Sidekick of ¢$\delta(\gamma_1,a)=\textsf{push}(\gamma_1,\gamma_1,\gamma_2)$¢
      Cγ2<Cγ1<Cγ1<Rest, JRγ1, JRγ2 >, Rest, JRγ2>,Cγ1<Rest, JRγ1, JRγ2 >,JRγ2> a();
    }
    interface γ2a_Push_γ2γ2<Rest extends C,JRγ1 extends C,JRγ2 extends C>{
      // Sidekick of ¢$\delta(\gamma_2,a)=\textsf{push}(\gamma_2,\gamma_2)$¢
      Cγ2<Cγ2<Rest, JRγ1, JRγ2>, JRγ1, Rest> a();
    }
  }
  //begin{many}
  static Cγ1<E,¤,¤> build = null;
  // end{many}
  //end{full}
  //begin{cases}
  static void isL( L l) {/**/}
  static void accepts() {
    isL(A.build.$());
    isL(A.build.a().c().$());
    isL(A.build.a().b().$()); 
    isL(A.build.a().a().b().c().a().b().$()); 
  }
  static void rejects() {
    isL(A.build.a().$()); 
    isL(A.build.b().a().$());
    isL(A.build.b().b().$());
    isL(A.build.a().b().a().$());  
    isL(A.build.a().b().b().a().$());  
    //end{cases}
    isL(A.build.a().b().b().b().a().$()); 
    isL(A.build.a().b().b().b().b().b().b().b().b().a().$());  
    //begin{cases}
  }
  //end{cases}
//begin{full}
//begin{configuration}
}
//end{configuration}
//end{full}
