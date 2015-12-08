package automaton;

import automaton.A2.C.*;


//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) class A2 {
  private static class ΣΣ { /* Reject */ } 
  private static class L extends ΣΣ { /**/ }

  // Configuration of the automaton
  interface C< // Generic parameters:
    Rest extends C,  // The rest of the stack, for pop operations
    Jσ1 extends C,     // Type of jump(1), may be self, rest, or anything in it. 
    Jσ2 extends C,    // Type of jump(2), may be self, rest, or anything in it. 
    JRσ1 extends C,   // Type of pop().jump(1), may be rest, or anything in it. 
    JRσ2 extends C    // Type of pop().jump(2), may be rest, or anything in it.  
  >
  {
    ΣΣ $();                  // delta transition on end of input; invalid language by default 
    C σ1();          // delta transition on σ1; dead end by default
    C σ2();          // delta transition on σ2; dead end by default

    interface U<X extends U<?>> extends C{/**/}
    public interface E extends U<¤> { /* Empty configuration */ }
    interface ¤ extends U<¤> { // Error configuration.
      @Override public ¤ σ1();
      @Override public ¤ σ2();
    }

     interface Cσ1< // Configuration when γ1 is the top
      Rest extends C,
      JRσ1 extends C, 
      JRσ2 extends C
     > extends C<
       Rest, // In Cσ1, Jσ1 must be Rest.
       JRσ2, 
       Rest,
       JRσ1, 
       JRσ2
     >  ,γ1σ1_Push_γ1γ1<Rest,JRσ1,JRσ2>
        ,γ1σ2_Push_γ2γ2<Rest,JRσ1,JRσ2>
     {
//       @Override  $() ; // REJECT

     }

     interface γ1σ1_Push_γ1γ1<Rest extends C,JRσ1 extends C,JRσ2 extends C>{
       Cσ1<
         Cσ1<
           Rest,
           JRσ1,
           JRσ2
         >,
         Rest,
         JRσ2
       > σ1();
     }

     interface γ1σ2_Push_γ2γ2<Rest extends C,JRσ1 extends C,JRσ2 extends C>{
       Cσ2<  
         Cσ2<
           Rest,
           JRσ1,
           JRσ2
         >,
         JRσ1,
         Rest
       >σ2();
     }
     
     interface Cσ2< // Configuration when γ2 is the top
      Rest extends C,   
      JRσ1 extends C, 
      JRσ2 extends C
     > extends C<
     JRσ1, 
     Rest, // In Cσ2, Jσ2 must be Rest. 
     Rest,
     JRσ1, 
     JRσ2>  
     { 
       @Override L $() ;
//     @Override σ1();  // REJECT
       @Override JRσ1 σ2();
        
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
static Cσ1<E,¤,¤> build = null;
}
