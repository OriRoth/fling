package org.spartan.fajita.api.rllp;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

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
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

@SuppressWarnings("restriction") public class RLLPEncoder {
  public final RLLP rllp;
  private final List<TypeSpec> mainTypes;
  private final List<TypeSpec> recursiveTypes;
  private final Map<List<Item>, TypeName> JSMCache;
  private static final ClassName placeholder = ClassName.get("", "placeholder");
  private final NamesCache nameCache;

  public RLLPEncoder(RLLP parser) {
    this.rllp = parser;
    this.recursiveTypes = new ArrayList<>();
    this.JSMCache = new HashMap<>();
    this.nameCache = new NamesCache(rllp.bnf);
    Predicate<Item> reachableItem = i -> i.dotIndex != 0 && i.rule.getChildren().get(i.dotIndex - 1).isVerb();
    mainTypes = rllp.items.stream().filter(reachableItem).map(i -> encodeItem(i)).collect(Collectors.toList());
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(i.rule.lhs);
    final String typeName = nameCache.getItemName(i);
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(typeName) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        // Adds push methods
        .addMethods(firstSet.stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    // Adds jump methods
    if (rllp.analyzer.isSuffixNullable(i))
      encoding.addMethods(rllp.analyzer.followSetOf(i.rule.lhs).stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    if (!followSet.isEmpty())
      encoding.addTypeVariables(
          followSet.stream().map(v -> TypeVariableName.get(NamesCache.verbTypeName(v))).collect(Collectors.toList()));
    return encoding.build();
  }
  public MethodSpec methodOf(Item i, Verb v) {
    final MethodSpec.Builder builder = MethodSpec.methodBuilder(v.name());
    final SimpleEntry<Collection<ParameterSpec>, VarArgs> isVarargsWrapper = getMethodParameters(v);
    if (isVarargsWrapper.getValue() != null)
      builder.varargs();
    Collection<ParameterSpec> params = isVarargsWrapper.getKey();
    return builder //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .addParameters(params)//
        .returns(returnTypeOfMethod(i, v))//
        .build();
  }
  private TypeSpec apiReturnType() {
    return TypeSpec.classBuilder(nameCache.returnTypeOf$())//
        .addModifiers(Modifier.PUBLIC) //
        .build();
  }
  private static SimpleEntry<Collection<ParameterSpec>, VarArgs> getMethodParameters(Verb v) {
    Collection<ParameterSpec> $ = new ArrayList<>();
    VarArgs varargs = null;
    if (v.type instanceof ClassesType) {
      List<Class<?>> classes = ((ClassesType) v.type).classes;
      for (int i = 0; i < classes.size(); i++)
        $.add(ParameterSpec.builder(classes.get(i), "arg" + i).build());
    } else if (v.type instanceof NestedType) {
      NestedType type = (NestedType) v.type;
      $.add(ParameterSpec
          .builder(ClassName.get("", NamesCache.getApiName(type.nested), NamesCache.returnTypeOf$(type.nested)), "arg0").build());
    } else if (v.type instanceof VarArgs) {
      final VarArgs type = (VarArgs) v.type;
      varargs = type;
      $.add(ParameterSpec.builder(type.clazz, "arg0").build());
    } else
      throw new IllegalArgumentException("Type of verb is unknown");
    return new SimpleEntry<>($, varargs);
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
    return ClassName.get("", apiReturnType().name);
  }
  private TypeName returnTypeOfAdvance(Advance action) {
    final Item next = action.beforeAdvancing.advance();
    final Collection<Verb> followOfItem = rllp.analyzer.followSetWO$(next.rule.lhs);
    final List<TypeName> params = followOfItem.stream().map(v -> TypeVariableName.get(NamesCache.verbTypeName(v)))
        .collect(Collectors.toList());
    return RLLPEncoder.parameterizedType(nameCache.getItemName(next), params);
  }
  private TypeName returnTypeOfPush(Push action) {
    JSM jsm = new JSM(rllp);
    jsm.pushAll(action.itemsToPush);
    return encodeJSM(jsm, action.i, action.v);
  }
  private TypeName encodeJSM(JSM jsm, Item context, Verb v) {
    return encodeJSM_recursive_protection(jsm, context, v, new RecursiveProtector());
  }
  private TypeName encodeJSM_recursive_protection(JSM jsm, Item context, Verb v, RecursiveProtector prot) {
    if (JSMCache.containsKey(jsm.getS0()))
      return JSMCache.get(jsm.getS0());
    if (jsm == JSM.JAMMED)
      return ClassName.get("", NamesCache.errorTypeName);
    if (prot.detectRecursion(jsm, v))
      return placeholder;
    Map<Verb, TypeName> typeArguments = new TreeMap<>();
    for (SimpleEntry<Verb, JSM> e : jsm.legalJumps())
      typeArguments.put(e.getKey(), encodeJSM_recursive_protection(e.getValue(), context, e.getKey(), prot));
    String name;
    if (typeArguments.containsValue(placeholder)) {
      TypeSpec recursiveType = addRecursiveType(jsm, prot.getRecursiveVerbs());
      name = recursiveType.name;
    } else
      name = nameCache.getItemName(jsm.peek());
    List<TypeName> params = encodeTypeArguments(jsm.peek(), typeArguments, context);
    params.removeIf(tn -> tn == placeholder);
    TypeName $ = RLLPEncoder.parameterizedType(name, params);
    JSMCache.put(jsm.getS0(), $);
    prot.unwind();
    return $;
  }
  private TypeSpec addRecursiveType(JSM jsm, Collection<Verb> recursiveVerbs) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(jsm.peek().rule.lhs);
    final String name = nameCache.getRecursiveTypeName(jsm);
    final List<TypeVariableName> nonrecursive = followSet.stream() //
        .filter(v -> !recursiveVerbs.contains(v)) //
        .map(v -> TypeVariableName.get(NamesCache.verbTypeName(v))) //
        .collect(Collectors.toList());
    final TypeSpec.Builder encoding = TypeSpec.classBuilder(name) //
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
        .addTypeVariables(nonrecursive);
    List<TypeName> superclass_params = followSet.stream()
        .map(v -> (!recursiveVerbs.contains(v)) ? TypeVariableName.get(NamesCache.verbTypeName(v))
            : parameterizedType(name, nonrecursive))//
        .collect(Collectors.toList());
    encoding.superclass(parameterizedType(nameCache.getItemName(jsm.peek()), superclass_params));
    final TypeSpec recursiveType = encoding.build();
    if (!recursiveTypes.stream().anyMatch(t -> t.toString().equals(recursiveType.toString())))
      recursiveTypes.add(recursiveType);
    return recursiveType;
  }
  private List<TypeName> encodeTypeArguments(Item mainType, Map<Verb, TypeName> typeArguments, Item context) {
    final Collection<Verb> followSet = rllp.analyzer.followSetWO$(mainType.rule.lhs);
    final Collection<Verb> contextFollowSet = rllp.analyzer.followSetWO$(context.rule.lhs);
    if (followSet.isEmpty())
      return Collections.emptyList();
    for (Verb v : followSet)
      typeArguments.putIfAbsent(v, (contextFollowSet.contains(v)) ? TypeVariableName.get(NamesCache.verbTypeName(v))
          : TypeVariableName.get(NamesCache.errorTypeName));
    return followSet.stream().map(v -> typeArguments.get(v)).collect(Collectors.toList());
  }
  private static TypeName returnTypeOfJump(Jump action) {
    return TypeVariableName.get(NamesCache.verbTypeName(action.v));
  }
  @Override public String toString() {
    return encode().toString();
  }

  private class RecursiveProtector {
    private final Stack<JSM> alreadySeen;
    private JSM recursive;
    private final Set<Verb> recursiveVerbs;

    public RecursiveProtector() {
      alreadySeen = new Stack<>();
      recursive = null;
      recursiveVerbs = new HashSet<>();
    }
    private void updateRecursiveVerb(JSM jsm, Verb v) {
      if (recursive != null && recursive != jsm)
        throw new IllegalStateException("what?");
      recursive = jsm;
      recursiveVerbs.add(v);
    }
    /**
     * @param jsm - the jsm to check
     * @param v - the last verb seen during parsing
     * @returns true if recursion was identified.
     */
    boolean detectRecursion(JSM jsm, Verb v) {
      if (alreadySeen.contains(jsm)) {
        updateRecursiveVerb(jsm, v);
        return true;
      }
      alreadySeen.push(jsm);
      return false;
    }
    void unwind() {
      alreadySeen.pop();
    }
    Collection<Verb> getRecursiveVerbs() {
      return recursiveVerbs;
    }
  }

  public TypeSpec encode() {
    return TypeSpec.classBuilder(rllp.bnf.getApiName()) //
        .addModifiers(Modifier.PUBLIC) //
        .addTypes(mainTypes) //
        .addTypes(recursiveTypes) //
        .addType(apiReturnType())//
        .build();
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
