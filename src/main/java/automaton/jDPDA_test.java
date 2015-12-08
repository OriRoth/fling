package automaton;

import automaton.TEST.C.C1;

//@formatter:off
@SuppressWarnings({"rawtypes","unused"}) class TEST {
  private static class SigmaStar { /* Reject */ } 
  private static class L extends SigmaStar { /**/ }

  // Configuration of the automaton
  interface C< // Generic parameters:
    Rest extends C,  // The rest of the stack, for pop operations
    J1 extends C,     // Type of jump(1), may be self, rest, or anything in it. 
    J2 extends C,    // Type of jump(2), may be self, rest, or anything in it. 
    JR1 extends C,   // Type of pop().jump(1), may be rest, or anything in it. 
    JR2 extends C    // Type of pop().jump(2), may be rest, or anything in it.  
  >
  {
    SigmaStar $();                  // delta transition on end of input; invalid language by default 
    C σ1();          // delta transition on σ1; dead end by default
    C σ2();          // delta transition on σ2; dead end by default

    interface U<X extends U<?>> extends C{/**/}
    public interface E extends U<¤> { /* Empty configuration */ }
    interface ¤ extends U<¤> { // Error configuration.
      @Override public ¤ σ1();
      @Override public ¤ σ2();
    }

     interface C1< // Configuration when gamma1 is the top
      Rest extends C, 
      JR1 extends C, 
      JR2 extends C
     > extends C<
       Rest, // In C1, J1 must be Rest.
       JR2, 
       Rest,
       JR1, 
       JR2
     >  , sigma1gamma1_Push_gamma1gamma1gamma2<C1<Rest,JR1,JR2>,Rest,JR1,JR2>
     {
      @Override L $() ;
      @Override ¤ σ2();
     }

     interface sigma1gamma1_Push_gamma1gamma1gamma2<Me extends C1<Rest,JR1,JR2>,Rest extends C ,JR1 extends C,JR2 extends C>{
       C2<
         C1<
           Me,
           Rest,
           JR2
         >,
         Me,
         JR2
       >σ1();       
     }

     interface C2< // Configuration when gamma2 is the top
      Rest extends C,   
      JR1 extends C, 
      JR2 extends C
     > extends C<
     JR1, 
     Rest, // In C2, J2 must be Rest. 
     Rest,
     JR1, 
     JR2>  
     { 
      @Override JR1 σ1();
      @Override C2<
      Rest,
      JR1,
      JR2
      > σ2();
        
     }
    
  }
  
  static void isL( L l) {/**/}
  
  static void accepts() {
    isL(build.$()); //works
    isL(build.σ1().σ1().$()); // works 
    isL(build.σ1().σ2().σ1().$()); // works 
    isL(build.σ1().σ2().σ2().σ1().$()); // works 
    isL(build.σ1().σ2().σ2().σ2().σ1().$()); // works
    isL(build.σ1().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ2().σ1().$()); // works 
  }
  static void rejects() {
    isL(build.σ1().$()); 
    isL(build.σ2().$());
    isL(build.σ1().σ2().$()); 
    isL(build.σ2().σ1().$());
    isL(build.σ2().σ2().$());
  }
static C1<E,¤,¤> build = null;
}
