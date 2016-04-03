package org.spartan.fajita.api.rllp.generation;

import static org.spartan.fajita.api.rllp.generation.GeneratorStrings.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.squareup.javapoet.TypeVariableName;

public class RLLPEncoder {
  private RLLP rllp;
  private Map<Item, TypeSpec> itemTypes;
  private TypeSpec enclosing;

  private RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    itemTypes = new HashMap<>();
    encodeItems(rllp.items);
    enclosing = TypeSpec.classBuilder(enclosingClass) //
        .addModifiers(Modifier.PUBLIC) //
        .addType(addErrorType())
        .addTypes(itemTypes.values()) //
        .build();
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(errorClass)//
        .addModifiers(Modifier.STATIC)
        .build();
  }
  private void encodeItems(List<Item> items) {
    for (Item i : filterItems(items))
      itemTypes.put(i, encodeItem(i));
  }
  private Collection<Item> filterItems(List<Item> items) {
    return items;
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(i.rule.lhs);
    final Collection<Verb> firstOfItem = rllp.analyzer.firstSetOf(i);
    final Builder itemType = TypeSpec.classBuilder(itemTypeName(i)) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        .addMethods(map(firstOfItem).with(v -> methodOf(i, v)));
    if (rllp.analyzer.isNullable(i))
      itemType.addMethods(map(rllp.analyzer.followSetOf(i.rule.lhs)).with(v -> methodOf(i, v)));
    if (!followOfItem.isEmpty())
      itemType.addTypeVariables(map(followOfItem).with(v -> typeArg(v)));
    return itemType.build();
  }
  private MethodSpec methodOf(Item i, Verb v) {
    return MethodSpec.methodBuilder(v.name()) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod(i, v)) //
        .build();
  }
  private TypeName returnTypeOfMethod(Item i, Verb v) {
    if ((!i.readyToReduce()) && i.afterDot().isVerb())
      return instanciateNextItem(i);
    final Action action = rllp.predict(i, v);
    switch (action.type()) {
      case ACCEPT:
        return returnTypeOfAccept();
      case JUMP:
        return returnTypeOfJump((Action.Jump) action);
      case PUSH:
        return returnTypeOfPush((Action.Push) action);
      default:
        throw new IllegalStateException();
    }
  }
  private static TypeName returnTypeOfAccept() {
    // TODO:CHANGE TYPE
    return TypeName.INT;
  }
  private TypeName instanciateNextItem(Item i) {
    final Item next = i.advance();
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(next.rule.lhs);
    if (followOfItem.isEmpty())
      return itemClass(next);
    return ParameterizedTypeName.get(itemClass(next), map(followOfItem).with(v -> typeArg(v)).toArray(new TypeName[] {}));
  }
  private TypeName returnTypeOfPush(Push action) {
    JSM jsm = new JSM(rllp.bnf.getVerbs(), rllp.jumpsTable);
    action.itemsToPush.descendingIterator().forEachRemaining(i -> jsm.push(i));
    return encodeJSM(jsm);
  }
  private TypeName encodeJSM(JSM jsm) {
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    if (!jsm.iterator().hasNext()) // no possible jumps
      return encodeTypeArgumentsMap(jsm.peek(), typeArguments);
    for (SimpleEntry<Verb, JSM> e : jsm)
      typeArguments.put(e.getKey(), encodeJSM(e.getValue()));
    return encodeTypeArgumentsMap(jsm.peek(), typeArguments);
  }
  private TypeName encodeTypeArgumentsMap(Item mainType, Map<Verb, TypeName> typeArguments) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(mainType.rule.lhs);
    if (followSet.isEmpty())
      return itemClass(mainType);
    for (Verb v : followSet)
      typeArguments.putIfAbsent(v, TypeVariableName.get(errorClass));
    TypeName[] arguments = new TypeName[followSet.size()];
    int i = 0;
    for (Verb v : followSet)
      arguments[i++] = typeArguments.get(v);
    return ParameterizedTypeName.get(itemClass(mainType), arguments);
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return typeArg(action.v);
  }
  @Override public String toString() {
    return enclosing.toString();
  }
  public static String generate(RLLP parser) {
    return JavaFile.builder("org.spartan.fajita.api.junk", new RLLPEncoder(parser).enclosing).build().toString();
  }
}
