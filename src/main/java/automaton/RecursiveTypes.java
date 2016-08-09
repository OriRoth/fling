package automaton;

public class RecursiveTypes {
  class Node{}
  
  class X<SomeNode,Recursive_Param>{}
  
  // wanted type: X<Node,X<Node,X<Node, ... >>>
  
  class Recursive_X extends X<Node, Recursive_X>{
  }
}
