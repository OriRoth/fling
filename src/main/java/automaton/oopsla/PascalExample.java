package automaton.oopsla;

public class PascalExample {
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
  
   Procedure_Definition -> procedure id Parameters ; Definitions Body .
   Parameters           -> | () .
   * </pre>
   **/
  abstract static class S {
    protected abstract Object semi_t();
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
    @Override public abstract Parameters1<Program_Definition4> pair_t();
    @Override public abstract Program_Definition4 semi_t();
  }

  public static abstract class Program_Definition4 extends S {
    @Override public abstract Labels1<Constants1<Procedure_Definition1_rec<Body1<ERROR, ERROR>>, Body1<ERROR, ERROR>>, Procedure_Definition1_rec<Body1<ERROR, ERROR>>, Body1<ERROR, ERROR>> label_t();
    @Override public abstract Constants1<Procedure_Definition1_rec<Body1<ERROR, ERROR>>, Body1<ERROR, ERROR>> const_t();
    @Override public abstract Procedure_Definition1_rec<Body1<ERROR, ERROR>> procedure_t();
    @Override public abstract Body1<ERROR, ERROR> begin_t();
  }

  // rule #1 of Body
  public static abstract class Body1<jump_begin, jump_procedure> extends S {
    @Override public abstract Body2<jump_begin, jump_procedure> end_t();
  }

  public static abstract class Body2<jump_begin, jump_procedure> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    // TODO: problem. body might end the input, and may not, in some cases $
    // will be illegal
    // We can 1) move the jump dest. parameter in type argument but it will not
    // prevent the invocation.
    // 2) create a Body' nonterminal with duplicant rule Body'->begin end
    //   where one will be endable (has $ method) and one not.
  }

  // rule #1 of Constant
  public static abstract class Constant1<jump_semi, jump_procedure, jump_begin> extends S {
    @Override public abstract jump_semi semi_t();
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
  }

  // rule #1 of Constants
  public static abstract class Constants1<jump_procedure, jump_begin> extends S {
    @Override public abstract Constant1<Optional_Constants1<jump_procedure, jump_begin>, jump_procedure, jump_begin> semi_t();
  }

  // rule #1 of Label_Declaration
  public static abstract class Label_Declaration1<jump_semi, jump_const, jump_procedure, jump_begin> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_const const_t();
    @Override public abstract jump_semi semi_t();
  }

  public static abstract class Label_Declaration1_rec<jump_const, jump_procedure, jump_begin> extends
      Label_Declaration1<Label_Declaration1_rec<jump_const, jump_procedure, jump_begin>, jump_const, jump_procedure, jump_begin> {
    //
  }

  // rule #1 of Labels
  public static abstract class Labels1<jump_const, jump_procedure, jump_begin> extends S {
    @Override public abstract Label_Declaration1_rec<jump_const, jump_procedure, jump_begin> semi_t();
  }

  // rule #1 of Optional_Constants
  public static abstract class Optional_Constants1<jump_procedure, jump_begin> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract Constant1<Optional_Constants1<jump_procedure, jump_begin>, jump_procedure, jump_begin> semi_t();
  }

  // rule #1 of Optional_Labels
  public static abstract class Optional_Labels1<jump_const, jump_procedure, jump_begin> extends S {
    @Override public abstract jump_begin begin_t();
    @Override public abstract jump_procedure procedure_t();
    @Override public abstract jump_const const_t();
    @Override public abstract Label_Declaration1_rec<jump_const, jump_procedure, jump_begin> semi_t();
  }

  // rule #1 of Parameters
  public static abstract class Parameters1<jump_semi> extends S {
    @Override public abstract jump_semi semi_t();
  }

  // rule #1 of Procedure_Definition
  public static abstract class Procedure_Definition1<jump_procedure, jump_begin> extends S {
    @Override public abstract Procedure_Definition2<jump_procedure, jump_begin> id_t();
  }

  public static abstract class Procedure_Definition1_rec<jump_begin>
      extends Procedure_Definition1<Procedure_Definition1_rec<jump_begin>, jump_begin> {
    //
  }

  public static abstract class Procedure_Definition2<jump_procedure, jump_begin> extends S {
    @Override public abstract Procedure_Definition4<jump_procedure, jump_begin> semi_t();
    @Override public abstract Parameters1<Procedure_Definition4<jump_procedure, jump_begin>> pair_t();
  }

  public static abstract class Procedure_Definition3<jump_procedure, jump_begin> extends S {
    @Override public abstract Procedure_Definition4<jump_procedure, jump_begin> semi_t();
  }

  public static abstract class Procedure_Definition4<jump_procedure, jump_begin> extends S {
    @Override public abstract Body1<jump_begin, jump_procedure> begin_t();
    @Override public abstract Procedure_Definition1<ERROR, Body1<jump_begin, jump_procedure>> procedure_t();
    @Override public abstract Constants1<Procedure_Definition1<ERROR, Body1<jump_begin, jump_procedure>>, Body1<jump_begin, jump_procedure>> const_t();
    @Override public abstract Labels1<Constants1<Procedure_Definition1<ERROR, Body1<jump_begin, jump_procedure>>, Body1<jump_begin, jump_procedure>>, Procedure_Definition1<ERROR, Body1<jump_begin, jump_procedure>>, Body1<jump_begin, jump_procedure>> label_t();
  }

  public static abstract class Procedure_Definition5<jump_procedure, jump_begin> extends S {
    @Override public abstract Body1<jump_begin, jump_procedure> begin_t();
  }

  public static class Pascal extends Program_Definition0 {
    @Override public Program_Definition1 program_t() {
      return null;
    }
    @Override protected Object semi_t() {
      return null;
    }
    @Override protected Object pair_t() {
      return null;
    }
    @Override protected Object abstract_t() {
      return null;
    }
    @Override protected Object begin_t() {
      return null;
    }
    @Override protected Object const_t() {
      return null;
    }
    @Override protected Object end_t() {
      return null;
    }
    @Override protected Object extends_t() {
      return null;
    }
    @Override protected Object id_t() {
      return null;
    }
    @Override protected Object label_t() {
      return null;
    }
    @Override protected Object procedure_t() {
      return null;
    }
  }
}
