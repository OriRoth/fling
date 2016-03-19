(* 
 ** We assume a pascal version in which program labels are first class, 
 ** i.e., they can be stored in varaibles, in arrays, passed as parameters, 
 ** returned by functions, etc. 
 *)
Procedure Program_Definition;
Label TERMINATE, PROCEDE;
Begin
  consume("program");
  Identifier; 
  Parameters(REJECT, PROCEDE);
PROCEDE:
  consume(";");
  Definitions;
  Body;
  goto TERMINATE;
REJECT:
  reject();
  
TERMINATE:
end;

Procedure Parameters(COLON, SEMICOLON: Target);
Begin
  switch (next()) {
    case "()":
      consume();
      return;
    case ":":
      goto COLON;
    case ";"
      goto SEMICOLON;
  }
end;

Procedure Identifier;
Begin
  consume("id");
end;

Procedure Body;
Begin
  consume("begin");
  consume("end");
end;

Procedure Definitions;
Begin
  consume("Labels") 
  Constants 
  Types 
  Variables 
  Nested;
end;

Procedure Labels;
Begin
  | consume("label") 
  Label_Declaration; 
  Optional_Labels;
end;
 
Procedure Constants;
Begin
  | consume("const") 
  Constant; 
  Optional_Constants;
end;

Procedure Types;
Begin
  | consume("type");
  Type_Definition;
  Optional_Types ;
end;

Procedure Variables;
Begin
  |  
  consume("var")   
  Variable;
  Optional_Variables;

end;
  
Procedure Label_Declaration;
Begin
  Semi_Thing;
end;

Procedure Constant;
Begin
  Semi_Thing;
end;

Procedure Type_Definition;
Begin
  Semi_Thing;
end;

Procedure Variable;
Begin
  Semi_Thing;
end;

Procedure Semi_Thing;
Begin
  Thing ;   
end;

Procedure Thing;
Begin
  
end;

Procedure Optional_Labels;
Begin
  |  Label_Declaration;
  Optional_Labels;

end;

Procedure Optional_Constants;
Begin
  |  
  Constant; 
  Optional_Constants;
end;

Procedure Optional_Types;
Begin
  |  
  Type_Definition; 
  Optional_Types;
end;

Procedure Optional_Variables;
Begin
  |  Variable;
  Optional_Variables;

end;

Procedure Nested;
Begin
  | 
  Module;
  Nested;
end;

Procedure Module;
Begin
  Function_Definition | Procedure_Definition;
end;

Procedure Procedure_Definition;
Begin
  procedure; 
  Identifier; 
  Parameters; 
  Definitions; 
  Body;
end;

Procedure Function_Definition;
Begin
  consume("function") 
  Identifier; 
  Parameters(PROCEDE, REJECT); 
PROCEDE
  consume(":") 
  Identifier; 
  Definitions; 
  Body;
  goto TERMINATE
REJECT:
  reject(); 
TERMINATE:
end;


Procedure consume(s: String);
begin;
end;
