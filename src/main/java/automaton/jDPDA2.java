package automaton;

import automaton.A2.C.Cσ1;
import automaton.A2.C.E;
import automaton.A2.C.¤;


//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) class A2 {
  private static class ΣΣ { /* Reject */ } 
  static class L extends ΣΣ { /**/ }

  // Configuration of the automaton
  interface C< // Generic parameters:
    Rest extends C,  // The rest of the stack, for pop operations
    Jγ1 extends C,     // Type of jump(1), may be self, rest, or anything in it. 
    Jγ2 extends C,    // Type of jump(2), may be self, rest, or anything in it. 
    JRγ1 extends C,   // Type of pop().jump(1), may be rest, or anything in it. 
    JRγ2 extends C    // Type of pop().jump(2), may be rest, or anything in it.  
  >
  {
    ΣΣ $();                  // delta transition on end of input; invalid language by default 
    C σ1();          // delta transition on σ1; dead end by default
    C σ2();          // delta transition on σ2; dead end by default

    public interface E extends C<¤,¤,¤,¤,¤> { /* Empty configuration */ }
    interface ¤ extends C<¤,¤,¤,¤,¤> { /* Error configuration. */  }

     interface Cγ1< // Configuration when γ1 is the top
      Rest extends C,
      JRγ1 extends C, 
      JRγ2 extends C
     > extends C<
       Rest, // In Cγ1, Jγ1 must be Rest.
       JRγ2, 
       Rest,
       JRγ1, 
       JRγ2
     >  ,γ1σ1_Push_γ1γ1<Rest,JRγ1,JRγ2>
        ,γ1σ2_Push_γ2γ2<Rest,JRγ1,JRγ2>
     {
//       @Override  $() ; // REJECT
     }

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
     
     interface Cγ2< // Configuration when γ2 is the top
      Rest extends C,   
      JRγ1 extends C, 
      JRγ2 extends C
     > extends C<
     JRγ1, 
     Rest, // In Cγ2, Jγ2 must be Rest. 
     Rest,
     JRγ1, 
     JRγ2>  
     { 
       @Override L $() ;
//     @Override σ1();  // REJECT
       @Override JRγ1 σ2();
        
     }
    
  }
  
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
static Cγ1<E,¤,¤> build = null;
}
