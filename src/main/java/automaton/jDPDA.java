package automaton;

import automaton.A.C.Cγ1;
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
    Jγ1 extends C,  // Type of¢$~\textsf{jump}(\gamma1)$¢, may be rest, or anything in it. 
    Jγ2 extends C,  // Type of¢$~\textsf{jump}(\gamma2)$¢, may be rest, or anything in it. 
    JRγ1 extends C, // Type of¢$~\cc{Rest}.\textsf{jump}(\gamma1)$¢, may be rest, or anything in it. 
    JRγ2 extends C  // Type of¢$~\cc{Rest}.\textsf{jump}(\gamma2)$¢, may be rest, or anything in it.  
  >
  {
    ΣΣ $();         // δ transition on end of input; invalid language by default 
    C σ1();         // δ transition on σ¢1¢; dead end by default
    C σ2();         // δ transition on σ¢2¢; dead end by default

    public interface E extends C<¤,¤,¤,¤,¤> { /* Empty configuration */ }
    interface ¤ extends C<¤,¤,¤,¤,¤> { /* Error configuration. */ }
  // end{configuration}

  //begin{many}
     interface Cγ1< // Configuration when ¢$\gamma1$¢ is the top
      Rest extends C,
      JRγ1 extends C, 
      JRγ2 extends C
     > extends C<
       Rest,        
       Rest,        // ¢$J\gamma1$¢ must be ¢$\textsf{Rest}$¢.
       JRγ2,        
       JRγ1,
       JRγ2
     >   
     // end{many} 
       ,γ1σ1_Push_γ1γ1<Rest,JRγ1,JRγ2>
       ,γ1σ2_Push_γ2γ2<Rest,JRγ1,JRγ2>
     // begin{many}
     {
     // end{many}
        // @Override  $() ; // REJECT
     // begin{many}
     }
     // end{many}

  //begin{many}
     interface Cγ2< // Configuration when ¢$\gamma2$¢ is the top
       Rest extends C,   
       JRγ1 extends C, 
       JRγ2 extends C
     > extends C<
       Rest,  
       JRγ1, 
       Rest, // ¢$J\gamma2$¢ must be ¢$\textsf{Rest}$¢. 
       JRγ1, 
       JRγ2
    >  
    { 
     // end{many}
      @Override L $() ;
//    @Override ¢$\sigma1$¢();  // REJECT
      @Override JRγ1 σ2();
       
  //begin{many}
    }
  // end{many}
     interface γ1σ1_Push_γ1γ1<Rest extends C,JRγ1 extends C,JRγ2 extends C>{
       Cγ1<
         Cγ1<
           Rest,
           JRγ1,
           JRγ2
         >,
         Rest,
         JRγ2
       > σ1();
     }

     interface γ1σ2_Push_γ2γ2<Rest extends C,JRγ1 extends C,JRγ2 extends C>{
       Cγ2<  
         Cγ2<
           Rest,
           JRγ1,
           JRγ2
         >,
         JRγ1,
         Rest
       >σ2();
     }
     
    
  }
  //end{full}
  //begin{cases}
  static void isL( L l) {/**/}
  
  static void accepts() {
    isL(build.σ2().$());
    isL(build.σ1().σ2().$()); 
    isL(build.σ1().σ1().σ2().σ2().σ1().σ2().$()); 
  }
  static void rejects() {
    isL(build.$()); 
    isL(build.σ1().$()); 
    isL(build.σ2().σ1().$());
    isL(build.σ2().σ2().$());
    isL(build.σ1().σ2().σ1().$());  
    isL(build.σ1().σ2().σ2().σ1().$());  
    isL(build.σ1().σ2().σ2().σ2().σ1().$()); 
    isL(build.σ1().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ1().$());  
  }
  //end{cases}
static Cγ1<E,¤,¤> build = null;
//begin{full}
//begin{configuration}
}
//end{configuration}
//end{full}
