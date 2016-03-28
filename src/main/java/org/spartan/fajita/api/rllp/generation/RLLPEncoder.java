package org.spartan.fajita.api.rllp.generation;

import static org.spartan.fajita.api.rllp.generation.GeneratorStrings.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class RLLPEncoder {
  private RLLP rllp;
  private Map<Item,TypeSpec> itemTypes;

  private RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    encodeItems(rllp.items);
  }
  private void encodeItems(List<Item> items) {
    for (Item i : items)
      itemTypes.put(i, encodeItem(i));
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> followOfItem = rllp.analyzer.followSetOf(i.rule.lhs);
    final Collection<Verb> firstOfItem = rllp.analyzer.firstSetOf(i);
    return TypeSpec.classBuilder(itemTypeName(i)) //
        .addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.ABSTRACT) //
        .addTypeVariables(map(followOfItem).with(v->typeArg(v))) //
        .addMethods(map(firstOfItem).with(v->methodOf(i,v))) //
        .build();
  }
  private MethodSpec methodOf(Item i, Verb v) {
    return MethodSpec.methodBuilder(v.name()) //
        .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod(i,v)) //
        .build();
  }
  private TypeName returnTypeOfMethod(Item i, Verb v) {
    final Action action = rllp.predict(i, v);
    if (action.isPush())
      return returnTypeOfPush((Action.Push)action,i,v);
    return returnTypeOfJump((Action.Jump)action);
  }
  private TypeName returnTypeOfPush(Push action, Item i, Verb v) {
    
    return null;
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return typeArg(action.v);
  }
  public static String generate(RLLP parser){
    return new RLLPEncoder(parser).toString();
  }
  
  
  
  
}
