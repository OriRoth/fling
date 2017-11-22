package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.api.FajitaEncoder;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.bnf.symbols.type.ClassesType;
import org.spartan.fajita.api.bnf.symbols.type.NestedType;
import org.spartan.fajita.api.bnf.symbols.type.VarArgs;
import org.spartan.fajita.api.rllp.RLLP.Action;
import org.spartan.fajita.api.rllp.RLLP.Action.Advance;
import org.spartan.fajita.api.rllp.RLLP.Action.Jump;
import org.spartan.fajita.api.rllp.RLLP.Action.Push;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

public class RLLPEncoder {
  public final RLLP rllp;
  private final List<TypeSpec> mainTypes;
  private final List<TypeSpec> recursiveTypes;
  private final Collection<MethodSpec> staticMethods;
  private final Map<List<Item>, TypeName> encodedJSMs;
  private Item mainItem;
  private final EncoderUtils namer;
  // Used for Debugging
  private final boolean visualize = true;

  public RLLPEncoder(RLLP parser, EncoderUtils namer) {
    this.rllp = parser;
    this.recursiveTypes = new ArrayList<>();
    this.encodedJSMs = new HashMap<>();
    this.namer = namer;
    this.staticMethods = FajitaEncoder.getStaticMethods(this);
    Predicate<Item> reachableItem = i -> i.dotIndex != 0 && i.beforeDot().isVerb();
    mainTypes = rllp.items.stream().filter(reachableItem).map(i -> encodeItem(i)).collect(Collectors.toList());
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    Collection<TypeVariableName> namedFollowSet = mapFollowSetWith(i, v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)));
    final String typeName = namer.getItemName(i);
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typeName) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        // Adds push methods
        .addMethods(firstSet.stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    // Adds jump methods
    if (rllp.analyzer.isSuffixNullable(i)) {
      encoding.addMethods(mapFollowSetWith(i, v -> methodOf(i, v)));
      if (rllp.analyzer.followSetOf(i.rule.lhs).contains(SpecialSymbols.$))
        encoding.addSuperinterface(namer.returnTypeOf$());
    }
    if (!namedFollowSet.isEmpty())
      encoding.addTypeVariables(namedFollowSet);
    return encoding.build();
  }
  public MethodSpec methodOf(Item i, Verb v) {
    final MethodSpec.Builder builder = MethodSpec.methodBuilder(v.name());
    augmentMethodParameters(builder, v);
    return builder //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .returns(returnTypeOfMethod(i, v))//
        .build();
  }
  private static TypeSpec getErrorType() {
    return TypeSpec.classBuilder(EncoderUtils.error)//
        .addModifiers(Modifier.PRIVATE)//
        .build();
  }
  private void augmentMethodParameters(Builder builder, Verb v) {
    if (v.type instanceof ClassesType) {
      List<Class<?>> classes = ((ClassesType) v.type).classes;
      for (int i = 0; i < classes.size(); i++)
        builder.addParameter(classes.get(i), "arg" + i);
    } else if (v.type instanceof NestedType) {
      builder.addParameter(namer.returnTypeOf$(), "arg0").build();
    } else if (v.type instanceof VarArgs) {
      builder.varargs();
      builder.addParameter(ParameterSpec.builder(((VarArgs) v.type).clazz, "arg0").build());
    } else
      throw new IllegalArgumentException("Type of verb is unknown");
  }
  private TypeName returnTypeOfMethod(Item i, Verb v) {
    final Action action = rllp.predict(i, v);
    switch (action.type()) {
      default:
        throw new IllegalStateException("Action type unknown");
      case ACCEPT:
        return namer.returnTypeOf$();
      case ADVANCE:
        return returnTypeOfAdvance((Action.Advance) action);
      case JUMP:
        return returnTypeOfJump((Action.Jump) action);
      case PUSH:
        return returnTypeOfPush((Action.Push) action);
    }
  }
  private TypeName returnTypeOfAdvance(Advance action) {
    final Item next = action.beforeAdvancing.advance();
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(next.rule.lhs);
    final List<TypeName> params = followOfItem.stream().map(v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)))
        .collect(Collectors.toList());
    return RLLPEncoder.parameterizedType(namer.getItemName(next), params);
  }
  private TypeName returnTypeOfPush(Push action) {
    mainItem = action.i;
    JSM jsm = new JSM(rllp);
    jsm.pushAll(action.itemsToPush);
    return encodeJSM(jsm);
  }
  private TypeName encodeJSM(JSM jsm) {
    if (visualize) {
      String label = "encoding " + jsm;
      JSMGraph jsmGraph = new JSMGraph();
      jsmGraph.calcAndVisualize(jsm, label);
    }
    return encodeJSM_recursive_protection(jsm, new ArrayList<>());
  }
  private TypeName encodeJSM_recursive_protection(JSM jsm, ArrayList<JSM> alreadySeen) {
    if (encodedJSMs.containsKey(jsm.getS0()))
      return encodedJSMs.get(jsm.getS0());
    if (jsm == JSM.JAMMED)
      return EncoderUtils.errorType();
    final List<TypeVariableName> namedFollow = mapFollowSetWith(mainItem, v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)));
    if (jsm.isRecursive()) {
      // Second or more times seen
      if (alreadySeen.contains(jsm))
        return parameterizedType(namer.getRecursiveTypeName(jsm), namedFollow);
      alreadySeen.add(jsm);// First time seeing
    }
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    for (SimpleEntry<Verb, JSM> e : jsm.legalJumps())
      typeArguments.put(e.getKey(), encodeJSM_recursive_protection(e.getValue(), alreadySeen));
    TypeName $;
    final List<TypeName> encodedArguments = encodeTypeArguments(jsm.peek(), typeArguments);
    if (jsm.isRecursive()) {
      TypeSpec recursiveType = addRecursiveType(jsm, encodedArguments);
      $ = parameterizedType(recursiveType.name, namedFollow);
    } else {
      $ = parameterizedType(namer.getItemName(jsm.peek()), encodedArguments);
    }
    encodedJSMs.put(jsm.getS0(), $);
    return $;
  }
  private TypeSpec addRecursiveType(JSM jsm, List<TypeName> args) {
    final TypeSpec $ = TypeSpec.classBuilder(namer.getRecursiveTypeName(jsm)) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .addTypeVariables(mapFollowSetWith(mainItem, v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)))) //
        .superclass(parameterizedType(namer.getItemName(jsm.peek()), args))//
        .build();
    if (!recursiveTypes.stream().anyMatch(t -> t.toString().equals($.toString())))
      recursiveTypes.add($);
    return $;
  }
  private List<TypeName> encodeTypeArguments(Item current, Map<Verb, TypeName> typeArguments) {
    final Collection<Verb> followSet = mapFollowSetWith(current, v -> v);
    final Collection<Verb> mainFollowSet = mapFollowSetWith(mainItem, v -> v);
    if (followSet.isEmpty())
      return Collections.emptyList();
    for (Verb v : followSet)
      typeArguments.putIfAbsent(v,
          (mainFollowSet.contains(v)) ? TypeVariableName.get(EncoderUtils.verbTypeName(v)) : EncoderUtils.errorType());
    return mapFollowSetWith(current, v -> typeArguments.get(v));
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return TypeVariableName.get(EncoderUtils.verbTypeName(action.v));
  }
  @Override public String toString() {
    return encode().toString();
  }
  public TypeSpec encode() {
    return TypeSpec.classBuilder(rllp.bnf.getApiName()) //
        .addModifiers(Modifier.PUBLIC) //
        .addTypes(mainTypes) //
        .addTypes(recursiveTypes) //
        .addType(getErrorType()) //
        .addMethods(staticMethods)//
        .build();
  }
  public <T> List<T> mapFollowSetWith(Item i, Function<Verb, T> mapper) {
    return mapFollowSetWith(i, mapper, false);
  }
  public <T> List<T> mapFollowSetWith(Item i, Function<Verb, T> mapper, boolean with$) {
    Collection<Verb> follow;
    if (with$)
      follow = rllp.analyzer.followSetOf(i.rule.lhs);
    else
      follow = rllp.analyzer.followSetWO$(i.rule.lhs);
    return follow.stream().map(mapper).collect(Collectors.toList());
  }
  public static TypeName parameterizedType(final String typename, Iterable<? extends TypeName> params) {
    final ClassName type = ClassName.get("", typename);
    List<TypeName> l = new ArrayList<>();
    for (TypeName param : params)
      l.add(param);
    if (l.isEmpty())
      return type;
    return ParameterizedTypeName.get(type, l.toArray(new TypeName[] {}));
  }
  public String getApiName() {
    return rllp.bnf.getApiName();
  }
}
