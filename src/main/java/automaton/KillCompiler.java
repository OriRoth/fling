package automaton; 
  public class KillCompiler {  
  static class A<Recurse_1,Recurse_2>{ 
    A<A<Recurse_1,Recurse_2>,A<Recurse_1,Recurse_2>> d2() { return null; } 
  } 
  
  public static void main(String[] args) { 
  A<?,?> s =new A<String,String>().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2().d2(); 
   
  } 
}