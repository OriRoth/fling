package automaton;

public class T {
  /**
   * <pre>
   Program_Definition  ->  program id Parameters ; Definitions Body.
   Body                ->  begin end.
  
   Definitions  -> Labels Constants Nested.
  
   Labels      -> | label Label_Declaration Optional_Labels . 
   Constants   -> | const Constant Optional_Constants .
  
   Label_Declaration    -> ; .
   Constant             -> ; .
  
   Optional_Labels     ->  |  Label_Declaration Optional_Labels.
   Optional_Constants  ->  |  Constant Optional_Constants.
  
   Nested -> | Procedure_Definition Nested .
  
   Procedure_Definition -> procedure id Parameters ; Definitions ( Body .
   Parameters           -> | () .
   * </pre>
   **/
  abstract static class S {
    protected abstract Object semi_t();
    protected abstract Object open_t();
    protected abstract Object pair_t();
    protected abstract Object abstract_t();
    protected abstract Object begin_t();
    protected abstract Object const_t();
    protected abstract Object end_t();
    protected abstract Object extends_t();
    protected abstract Object id_t();
    protected abstract Object label_t();
    protected abstract Object procedure_t();
    protected abstract Object program_t();
  }

  public interface ERROR {
    //
  }

  // rule #1 of Program_Definition
  public static abstract class Program_Definition0 extends S {
    @Override public abstract Program_Definition1 program_t();
  }

  public static abstract class Program_Definition1 extends S {
    @Override public abstract Program_Definition2 id_t();
  }

  public static abstract class Program_Definition2 extends S {
    // This is simplified, probably we should return Parameters1
    @Override public abstract Parameters1<Program_Definition4> pair_t();
    // Parameters is derived to epsilon and semi_t is consumed
    @Override public abstract Program_Definition4 semi_t();
  }

  public static abstract class Program_Definition4 extends S {
    @Override public abstract Labels1<Constants1<Procedure_Definition1_rec<Body1<ERROR, ERROR, ERROR>, ERROR>, Body1<ERROR, ERROR, ERROR>, ERROR>, Procedure_Definition1_rec<Body1<ERROR, ERROR, ERROR>, ERROR>, Body1<ERROR, ERROR, ERROR>, ERROR> label_t();
    @Override public abstract Constants1<Procedure_Definition1_rec<Body1<ERROR, ERROR, ERROR>, ERROR>, Body1<ERROR, ERROR, ERROR>, ERROR> const_t();
    @Override public abstract Procedure_Definition1_rec<Body1<ERROR, ERROR, ERROR>, ERROR> procedure_t();
    @Override public abstract Body1<ERROR, ERROR, ERROR> begin_t();
  }

  // rule #1 of Body
  public static abstract class Body1<jump_begin_t, jump_procedure_t, jump_open_t> extends S {
    @Override public abstract Body2<jump_begin_t, jump_procedure_t, jump_open_t> end_t();
  }

  public static abstract class Body2<jump_begin, jump_procedure, jump_open> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_open open_t();
    // TODO: problem. body might end the input, and may not, in some cases $
    // will be illegal
    // We can 1) move the jump dest. parameter in type argument but it will not
    // prevent the invocation.
    // 2) create a duplicant Body nonterminal with duplicant rule Body -> begin
    // end , where one will be endable and one not.
    // public abstract void $();
  }

  // rule #1 of Constant
  public static abstract class Constant1<jump_semi, jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract jump_semi semi_t();
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_open open_t();
  }

  // rule #1 of Constants
  public static abstract class Constants1<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Constant1<Optional_Constants1<jump_procedure, jump_begin, jump_open>, jump_procedure, jump_begin, jump_open> semi_t();
  }

  // rule #1 of Label_Declaration
  public static abstract class Label_Declaration1<jump_semi, jump_const, jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_open open_t();
    @Override public abstract jump_const const_t();
    @Override public abstract jump_semi semi_t();
  }

  public static abstract class Label_Declaration1_rec<jump_const, jump_procedure, jump_begin, jump_open> extends
      Label_Declaration1<Label_Declaration1_rec<jump_const, jump_procedure, jump_begin, jump_open>, jump_const, jump_procedure, jump_begin, jump_open> {
    //
  }

  // rule #1 of Labels
  public static abstract class Labels1<jump_const, jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Label_Declaration1_rec<jump_const, jump_procedure, jump_begin, jump_open> semi_t();
  }

  // rule #1 of Optional_Constants
  public static abstract class Optional_Constants1<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_open open_t();
    @Override public abstract Constant1<Optional_Constants1<jump_procedure, jump_begin, jump_open>, jump_procedure, jump_begin, jump_open> semi_t();
  }

  // rule #1 of Optional_Labels
  public static abstract class Optional_Labels1<jump_const, jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_open open_t();
    @Override public abstract jump_const const_t();
    @Override public abstract Label_Declaration1_rec<jump_const, jump_procedure, jump_begin, jump_open> semi_t();
  }

  // rule #1 of Parameters
  public static abstract class Parameters1<jump_semi_t> extends S {
    @Override public abstract jump_semi_t semi_t();
  }

  // rule #1 of Procedure_Definition
  public static abstract class Procedure_Definition1<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Procedure_Definition2<jump_procedure, jump_begin, jump_open> id_t();
  }

  public static abstract class Procedure_Definition1_rec<jump_begin, jump_open>
      extends Procedure_Definition1<Procedure_Definition1_rec<jump_begin, jump_open>, jump_begin, jump_open> {
    //
  }

  public static abstract class Procedure_Definition2<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Procedure_Definition4<jump_procedure, jump_begin, jump_open> semi_t();
    @Override public abstract Parameters1<Procedure_Definition4<jump_procedure, jump_begin, jump_open>> pair_t();
  }

  public static abstract class Procedure_Definition3<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Procedure_Definition4<jump_procedure, jump_begin, jump_open> semi_t();
  }

  public static abstract class Procedure_Definition4<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Procedure_Definition6<jump_procedure, jump_begin, jump_open> open_t();
    @Override public abstract Procedure_Definition1<ERROR, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>> procedure_t();
    @Override public abstract Constants1<Procedure_Definition1<ERROR, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>>, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>> const_t();
    @Override public abstract Labels1<Constants1<Procedure_Definition1<ERROR, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>>, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>>, Procedure_Definition1<ERROR, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>>, ERROR, Procedure_Definition6<jump_procedure, jump_begin, jump_open>> label_t();
  }

  public static abstract class Procedure_Definition5<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Procedure_Definition6<jump_procedure, jump_begin, jump_open> open_t();
  }

  public static abstract class Procedure_Definition6<jump_procedure, jump_begin, jump_open> extends S {
    @Override public abstract Body1<jump_begin, jump_procedure, jump_open> begin_t();
  }
}
