package org.spartan.fajita.api.rllp.generation;

import static org.spartan.fajita.api.rllp.generation.Utilities.*;

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
import org.spartan.fajita.api.rllp.RLLP.Action.Advance;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

@SuppressWarnings("restriction") public class RLLPEncoder {
  private RLLP rllp;
  private Map<Item, TypeSpec> itemTypes;
  private TypeSpec enclosing;

  private RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    itemTypes = new HashMap<>();
    encodeItems(rllp.items);
    enclosing = TypeSpec.classBuilder(enclosingClass) //
        .addType(addErrorType()).addTypes(itemTypes.values()) //
        .build();
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(errorClass)//
        .addModifiers(Modifier.STATIC).build();
  }
  private void encodeItems(List<Item> items) {
    for (Item i : filterItems(items))
      itemTypes.put(i, encodeItem(i));
  }
  private static Collection<Item> filterItems(List<Item> items) {
    // TODO: filter unreachable items
    return items;
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(i.rule.lhs);
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    final String typ = itemClassName(i).simpleName();
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typ) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        .addMethods(map(firstSet).with(v -> methodOf(i, v)));
    if (rllp.analyzer.isSuffixNullable(i))
      encoding.addMethods(map(rllp.analyzer.followSetOf(i.rule.lhs)).with(v -> methodOf(i, v)));
    if (!followSet.isEmpty())
      encoding.addTypeVariables(map(followSet).with(v -> verbTypeName(v)));
    return encoding.build();
  }
  private MethodSpec methodOf(Item i, Verb v) {
    final TypeName returnTypeOfMethod = returnTypeOfMethod(i, v);
    final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(v.name()) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod);
    augmentWithParameters(methodSpec, v);
    return methodSpec //
        .build();
  }
  private static void augmentWithParameters(Builder methodSpec, Verb v) {
    List<Class<?>> classes = v.type.classes;
    for (int i = 0; i < classes.size(); i++) {
      Class<?> clazz = classes.get(i);
      methodSpec.addParameter(ParameterSpec.builder(clazz, "arg" + i).build());
    }
  }
  private TypeName returnTypeOfMethod(Item i, Verb v) {
    final Action action = rllp.predict(i, v);
    switch (action.type()) {
      default:
        throw new IllegalStateException("Action type unknown");
      case ACCEPT:
        return returnTypeOfAccept();
      case ADVANCE:
        return returnTypeOfAdvance((Action.Advance) action);
      case JUMP:
        return returnTypeOfJump((Action.Jump) action);
      case PUSH:
        return returnTypeOfPush((Action.Push) action);
    }
  }
  private static TypeName returnTypeOfAccept() {
    // TODO:CHANGE TYPE
    return TypeName.INT;
  }
  private TypeName returnTypeOfAdvance(Advance action) {
    final Item next = action.beforeAdvancing.advance();
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(next.rule.lhs);
    if (followOfItem.isEmpty())
      return itemClassName(next);
    return ParameterizedTypeName.get(itemClassName(next), 
        map(followOfItem).with(v -> verbTypeName(v)).toArray(new TypeName[] {}));
  }
  private TypeName returnTypeOfPush(Push action) {
    JSM jsm = new JSM(rllp.bnf.getVerbs(), rllp.jumpsTable);
    action.itemsToPush.descendingIterator().forEachRemaining(i -> jsm.push(i));
    jsm.makeReadOnly();
    return encodeJSM(jsm,action.i);
  }
  private TypeName encodeJSM(JSM jsm, Item context) {
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    if (!jsm.iterator().hasNext()) // no possible jumps
      return encodeTypeArgumentsMap(jsm.peek(), typeArguments, context);
    for (SimpleEntry<Verb, JSM> e : jsm)
      typeArguments.put(e.getKey(), encodeJSM(e.getValue(),context));
    return encodeTypeArgumentsMap(jsm.peek(), typeArguments, context);
  }
  private TypeName encodeTypeArgumentsMap(Item mainType, Map<Verb, TypeName> typeArguments, Item context) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(mainType.rule.lhs);
    final Collection<Verb> contextFollowSet = rllp.analyzer.followSetWO$(context.rule.lhs);
    if (followSet.isEmpty())
      return itemClassName(mainType);
    for (Verb v : followSet)
      if (contextFollowSet.contains(v))
        typeArguments.putIfAbsent(v, verbTypeName(v));
      else
        typeArguments.putIfAbsent(v, TypeVariableName.get(errorClass));
    TypeName[] arguments = new TypeName[followSet.size()];
    int i = 0;
    for (Verb v : followSet)
      arguments[i++] = typeArguments.get(v);
    return ParameterizedTypeName.get(itemClassName(mainType), arguments);
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return verbTypeName(action.v);
  }
  @Override public String toString() {
    return enclosing.toString();
  }
  public static String generate(RLLP parser) {
    return JavaFile.builder("org.spartan.fajita.api.junk", new RLLPEncoder(parser).enclosing).build().toString();
  }
}
