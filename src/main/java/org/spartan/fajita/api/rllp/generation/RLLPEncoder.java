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
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.bnf.symbols.type.ClassesType;
import org.spartan.fajita.api.bnf.symbols.type.NestedType;
import org.spartan.fajita.api.rllp.Item;
import org.spartan.fajita.api.rllp.JSM;
import org.spartan.fajita.api.rllp.RLLP;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.Advance;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
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
  private final TypeSpec apiReturnType;

  public RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    this.recursiveTypes = new ArrayList<>();
    this.JSMCache = new HashMap<>();
    Predicate<Item> dotAfterVerb = i -> i.dotIndex != 0 && i.rule.getChildren().get(i.dotIndex - 1).isVerb();
    Predicate<Item> unreachableItem = dotAfterVerb.negate();
    apiReturnType = apiReturnType();
    enclosing = TypeSpec.classBuilder(parser.bnf.getApiName()) //
        .addModifiers(Modifier.PUBLIC) //
        .addType(addErrorType())//
        .addTypes(rllp.items.stream().filter(unreachableItem).map(i -> encodeItem(i)).collect(Collectors.toList())) //
        .addTypes(recursiveTypes) //
        .addType(apiReturnType)//
        .build();
  }
  private TypeSpec apiReturnType() {
    return TypeSpec.classBuilder(rllp.bnf.getApiName() + randomHexString())//
        .addModifiers(Modifier.STATIC, Modifier.PUBLIC) //
        .build();
  }
  private static TypeSpec addErrorType() {
    return TypeSpec.classBuilder(errorClass.name)//
        .addModifiers(Modifier.STATIC).build();
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(i.rule.lhs);
    final String typeName = itemClassName(i).simpleName();
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typeName) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        // Adds push methods
        .addMethods(firstSet.stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    // Adds jump methods
    if (rllp.analyzer.isSuffixNullable(i))
      encoding.addMethods(rllp.analyzer.followSetOf(i.rule.lhs).stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    if (!followSet.isEmpty())
      encoding.addTypeVariables(followSet.stream().map(v -> verbTypeName(v)).collect(Collectors.toList()));
    return encoding.build();
  }
  public Collection<MethodSpec> getStaticMethods() {
    return rllp.getStartItems().stream()//
        .map(i -> generateStaticMethods(i))//
        .flatMap(methods -> methods.stream())//
        .collect(Collectors.toList());//
  }
  private Collection<MethodSpec> generateStaticMethods(Item i) {
    return rllp.analyzer.firstSetOf(i).stream().map(v -> methodOf(i, v))//
        .map(m -> MethodSpec.methodBuilder(m.name)//
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //
            .addCode("return null;\n") //
            .returns(m.returnType) //
            .build()) //
        .collect(Collectors.toList());
  }
  private MethodSpec methodOf(Item i, Verb v) {
    final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(v.name()) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod(i, v));
    augmentMethodwithParameters(v, methodSpec);
    return methodSpec.build();
  }
  private void augmentMethodwithParameters(Verb v, final MethodSpec.Builder methodSpec) {
    if (v.type instanceof ClassesType) {
      List<Class<?>> classes = ((ClassesType) v.type).classes;
      for (int i1 = 0; i1 < classes.size(); i1++) {
        Class<?> clazz = classes.get(i1);
        methodSpec.addParameter(ParameterSpec.builder(clazz, "arg" + i1).build());
      }
    } else if (v.type instanceof NestedType) {
      methodSpec.addParameter(ClassName.get("", getNestedTypeName((NestedType) v.type, rllp.bnf)), "arg0");
    } else
      throw new IllegalArgumentException("Type of verb is unknown");
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
  private TypeName returnTypeOfAccept() {
    return ClassName.get("", apiReturnType.name);
  }
  private TypeName returnTypeOfAdvance(Advance action) {
    final Item next = action.beforeAdvancing.advance();
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(next.rule.lhs);
    final List<TypeName> params = followOfItem.stream().map(v -> (TypeName) verbTypeName(v)).collect(Collectors.toList());
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
    final List<TypeVariableName> recursive_params = followSet.stream() //
        .filter(v -> v == recursiveVerb) //
        .map(v -> verbTypeName(v)) //
        .collect(Collectors.toList());
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(name) //
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT) //
        .addTypeVariables(recursive_params);
    List<TypeName> superclass_params = followSet.stream()
        .map(v -> (v != recursiveVerb) ? verbTypeName(v) : parameterizedType(name, recursive_params))//
        .collect(Collectors.toList());
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
    return followSet.stream().map(v -> typeArguments.get(v)).collect(Collectors.toList());
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return verbTypeName(action.v);
  }
  @Override public String toString() {
    return enclosing.toString();
  }

  private final class FoundRecursiveTypeException extends Exception {
    private static final long serialVersionUID = 8456148424675230710L;
    public final JSM jsm;

    public FoundRecursiveTypeException(JSM jsm) {
      this.jsm = jsm;
    }
  }

  public TypeSpec encode() {
    return enclosing;
  }
}
