package org.spartan.fajita.api.rllp.generation;

import static org.spartan.fajita.api.rllp.generation.Utilities.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.Advance;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.ClassName;
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
  private TypeSpec enclosing;

  private RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    Predicate<Item> startItem = i ->i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol) && i.dotIndex == 0 ;
    Predicate<Item> dotAfterVerb = i ->  i.dotIndex != 0 && i.rule.getChildren().get(i.dotIndex - 1).isVerb();
    Predicate<Item> reachableItem = startItem.or(dotAfterVerb);
    Predicate<Item> unreachableItem = reachableItem.negate();
    enclosing = TypeSpec.classBuilder(enclosingClass) //
        .addType(addErrorType())//
        .addTypes(map(rllp.items).with(i -> encodeItem(i)).filter(unreachableItem)) //
        .build();
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(errorClass)//
        .addModifiers(Modifier.STATIC).build();
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(i.rule.lhs);
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    final String typ = itemClassName(i).simpleName();
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typ) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        // Adds push methods
        .addMethods(map(firstSet).with(v -> methodOf(i, v)));
    // Adds jump methods
    if (rllp.analyzer.isSuffixNullable(i))
      encoding.addMethods(map(rllp.analyzer.followSetOf(i.rule.lhs)).with(v -> methodOf(i, v)));
    if (!followSet.isEmpty())
      encoding.addTypeVariables(map(followSet).with(v -> verbTypeName(v)));
    return encoding.build();
  }
  private MethodSpec methodOf(Item i, Verb v) {
    final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(v.name()) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod(i, v));
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
        map(followOfItem).with(v -> verbTypeName(v)).asList().toArray(new TypeName[] {}));
  }
  private TypeName returnTypeOfPush(Push action) {
    JSM jsm = new JSM(rllp.bnf.getVerbs(), rllp.jumpsTable);
    jsm.pushAll(action.itemsToPush);
    return encodeJSM(jsm, action.i);
  }
  private TypeName encodeJSM(JSM jsm, Item context) {
    try {
      return encodeJSM_recursive_protection(jsm, context, new ArrayList<>());
    } catch (FoundRecursiveTypeException e) {
      throw new RuntimeException("Failed to handle recursive JSM: " + e.jsm);
    }
  }
  private TypeName encodeJSM_recursive_protection(JSM jsm, Item context, List<JSM> alreadySeen) throws FoundRecursiveTypeException {
    if (alreadySeen.indexOf(jsm) != -1)
      throw new FoundRecursiveTypeException(jsm);
    alreadySeen.add(jsm);
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    if (jsm.legalJumps().isEmpty()) // no possible jumps
      return encodeTypeArgumentsMap(jsm.peek(), typeArguments, context);
    for (SimpleEntry<Verb, JSM> e : jsm.legalJumps()) {
      TypeName encodedJSM = null;
      try {
        encodedJSM = encodeJSM_recursive_protection(e.getValue(), context, new ArrayList<>(alreadySeen));
      } catch (FoundRecursiveTypeException exc) {
        if (!jsm.equals(exc.jsm))
          throw exc;
        encodedJSM = encodeRecursiveJSM(jsm, context);
      }
      typeArguments.put(e.getKey(), encodedJSM);
    }
    return encodeTypeArgumentsMap(jsm.peek(), typeArguments, context);
  }
  private static TypeName encodeRecursiveJSM(JSM jsm, Item context) {
    System.out.println("Recurtion found!!!");
    return ClassName.get("", "recursive" + errorClass);
  }
  private TypeName encodeTypeArgumentsMap(Item mainType, Map<Verb, TypeName> typeArguments, Item context) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(mainType.rule.lhs);
    final Collection<Verb> contextFollowSet = rllp.analyzer.followSetWO$(context.rule.lhs);
    if (followSet.isEmpty())
      return itemClassName(mainType);
    for (Verb v : followSet)
      if (contextFollowSet.contains(v) && (mainType.readyToReduce() || rllp.analyzer.isSuffixNullable(mainType.advance())))
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

  private final class FoundRecursiveTypeException extends Exception {
    private static final long serialVersionUID = 8456148424675230710L;
    public final JSM jsm;

    public FoundRecursiveTypeException(JSM jsm) {
      this.jsm = jsm;
    }
  }
}
