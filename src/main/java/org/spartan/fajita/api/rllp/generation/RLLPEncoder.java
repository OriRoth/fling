package org.spartan.fajita.api.rllp.generation;

import static org.spartan.fajita.api.rllp.generation.Utilities.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

@SuppressWarnings("restriction") public class RLLPEncoder {
  private final RLLP rllp;
  private final TypeSpec enclosing;
  private final Collection<TypeSpec> recursiveTypes;
  private final Map<List<Item>, TypeName> JSMCache;
  private static final ClassName placeholder = ClassName.get("", "placeholder");

  private RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    this.recursiveTypes = new ArrayList<>();
    this.JSMCache = new HashMap<>();
    Predicate<Item> startItem = i -> i.rule.lhs.equals(SpecialSymbols.augmentedStartSymbol) && i.dotIndex == 0;
    Predicate<Item> dotAfterVerb = i -> i.dotIndex != 0 && i.rule.getChildren().get(i.dotIndex - 1).isVerb();
    Predicate<Item> reachableItem = startItem.or(dotAfterVerb);
    Predicate<Item> unreachableItem = reachableItem.negate();
    enclosing = TypeSpec.classBuilder(enclosingClass) //
        .addType(addErrorType())//
        .addTypes(map(rllp.items).with(i -> encodeItem(i)).filter(unreachableItem)) //
        .addTypes(recursiveTypes) //
        .build();
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(errorClass.name)//
        .addModifiers(Modifier.STATIC).build();
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(i.rule.lhs);
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    final String typeName = itemClassName(i).simpleName();
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typeName) //
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
    final List<TypeName> params = map(followOfItem).with(v -> (TypeName) verbTypeName(v)).asList();
    return parameterizedType(itemClassName(next), params);
  }
  private TypeName returnTypeOfPush(Push action) {
    JSM jsm = new JSM(rllp);
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
    if (JSMCache.containsKey(jsm.getS0()))
      return JSMCache.get(jsm.getS0());
    if (jsm == JSM.JAMMED)
      return errorClass;
    if (alreadySeen.indexOf(jsm) != -1)
      throw new FoundRecursiveTypeException(jsm);
    alreadySeen.add(jsm);
    TypeName $ = null;
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    for (SimpleEntry<Verb, JSM> e : jsm.legalJumps()) {
      /* In this Try-Catch we invoke the possibly recursive encoding of the JSM.
       * When we identify the recurrence of the JSM we throw, and catch only
       * when we get the original occurrence of the recursive JSM. */
      try {
        TypeName encodedJSM = encodeJSM_recursive_protection(e.getValue(), context, new ArrayList<>(alreadySeen));
        typeArguments.put(e.getKey(), encodedJSM);
      } catch (FoundRecursiveTypeException exc) {
        if (!jsm.equals(exc.jsm))
          throw exc;
        $ = encodeRecursiveJSM(jsm, context, e.getKey());
        break;
      }
    }
    if ($ == null) {
      List<TypeName> params = encodeTypeArguments(jsm.peek(), typeArguments, context);
      $ = parameterizedType(itemClassName(jsm.peek()), params);
    }
    JSMCache.put(jsm.getS0(), $);
    return $;
  }
  private TypeName encodeRecursiveJSM(JSM jsm, Item context, Verb recursiveVerb) {
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    for (SimpleEntry<Verb, JSM> e : jsm.legalJumps()) {
      if (e.getKey().equals(recursiveVerb)) {
        typeArguments.put(e.getKey(), placeholder);
        continue;
      }
      typeArguments.put(e.getKey(), encodeJSM(e.getValue(), context));
    }
    List<TypeName> params = encodeTypeArguments(jsm.peek(), typeArguments, context);
    TypeSpec recursiveType = generateRecursiveType(jsm, recursiveVerb);
    recursiveTypes.add(recursiveType);
    params.remove(placeholder);
    return parameterizedType(recursiveType.name, params);
  }
  private TypeSpec generateRecursiveType(JSM jsm, Verb recursiveVerb) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(jsm.peek().rule.lhs);
    final String name = recursiveTypeName(jsm);
    final List<TypeVariableName> recursive_params = map(followSet).with(v -> verbTypeName(v)).filter(v -> v == recursiveVerb);
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(name) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        .addTypeVariables(recursive_params);
    List<TypeName> superclass_params = map(followSet)
        .with(v -> (v != recursiveVerb) ? verbTypeName(v) : parameterizedType(name, recursive_params)).asList();
    encoding.superclass(parameterizedType(itemClassName(jsm.peek()), superclass_params));
    return encoding.build();
  }
  private List<TypeName> encodeTypeArguments(Item mainType, Map<Verb, TypeName> typeArguments, Item context) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(mainType.rule.lhs);
    final Collection<Verb> contextFollowSet = rllp.analyzer.followSetWO$(context.rule.lhs);
    if (followSet.isEmpty())
      return Collections.emptyList();
    for (Verb v : followSet)
      typeArguments.putIfAbsent(v, (contextFollowSet.contains(v)) ? verbTypeName(v) : errorClass);
    return map(followSet).with(v -> typeArguments.get(v)).asList();
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
