package org.spartan.fajita.revision.api.encoding;

import static java.util.stream.Collectors.toList;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.spartan.fajita.revision.api.encoding.EncoderUtils;
import org.spartan.fajita.revision.export.FluentAPIRecorder;
import org.spartan.fajita.revision.export.Grammar;
import org.spartan.fajita.revision.rllp.Item;
import org.spartan.fajita.revision.rllp.JSM;
import org.spartan.fajita.revision.rllp.RLLP;
import org.spartan.fajita.revision.rllp.RLLP.Action;
import org.spartan.fajita.revision.rllp.RLLP.Action.Advance;
import org.spartan.fajita.revision.rllp.RLLP.Action.Jump;
import org.spartan.fajita.revision.rllp.RLLP.Action.Push;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.SpecialSymbols;
import org.spartan.fajita.revision.symbols.Terminal;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ClassType;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.symbols.types.VarArgs;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

@SuppressWarnings("restriction") public class RLLPEncoder {
  public static final String $$$name = "$$$";
  public static final String $$$nameEscaped = "$$$$$$";
  public final RLLP rllp;
  private final boolean isSubBNF;
  private final Set<Terminal> terminals;
  private final List<TypeSpec> mainTypes;
  private final List<TypeSpec> recursiveTypes;
  private final TypeSpec $$$Type;
  private final Collection<MethodSpec> staticMethods;
  private final Map<List<Item>, TypeName> encodedJSMs;
  private final Map<TypeSpec, List<TypeName>> recArgs = new HashMap<>();
  private final Map<TypeSpec, String> recSuper = new HashMap<>();
  private Item mainItem;
  private final EncoderUtils namer;
  private final Class<? extends Grammar> provider;

  public RLLPEncoder(RLLP parser, EncoderUtils namer, Set<Terminal> terminals, Class<? extends Grammar> provider,
      boolean isSubBNF) {
    this.provider = provider;
    this.isSubBNF = isSubBNF;
    this.terminals = terminals;
    this.rllp = parser;
    if (isSubBNF)
      assert rllp.bnf.startSymbols.size() == 1;
    this.recursiveTypes = new ArrayList<>();
    this.encodedJSMs = new HashMap<>();
    this.namer = namer;
    this.staticMethods = FajitaEncoder.getStaticMethods(this);
    Predicate<Item> reachableItem = i -> i.dotIndex != 0 && i.beforeDot().isVerb();
    mainTypes = rllp.items.stream().filter(reachableItem).map(i -> encodeItem(i)).collect(Collectors.toList());
    fixRecTypes();
    $$$Type = get$$$Type();
  }
  private void fixRecTypes() {
    List<TypeSpec> old = new LinkedList<>(recursiveTypes);
    recursiveTypes.clear();
    for (TypeSpec t : old)
      recursiveTypes.add(TypeSpec.interfaceBuilder(t.name) //
          .addMethods(mainTypes.stream() //
              .filter(y -> y.name.equals(recSuper.get(t))) //
              .findFirst().map(y -> //
              y.methodSpecs.stream() //
                  .map(x -> MethodSpec.methodBuilder(x.name) //
                      .addParameters(x.parameters) //
                      .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) //
                      .returns(getFixedRecReturnType(x.returnType, y.typeVariables, recArgs.get(t))) //
                      .build()) //
                  .collect(toList()))
              .get()) //
          .addModifiers(Modifier.PUBLIC) //
          .build());
  }
  private static TypeName getFixedRecReturnType(TypeName r, List<TypeVariableName> vs1, List<TypeName> vs2) {
    if (r instanceof TypeVariableName)
      return vs2.get(vs1.indexOf(r));
    else if (r instanceof ParameterizedTypeName) {
      ParameterizedTypeName p = (ParameterizedTypeName) r;
      return parameterizedType(p.rawType.simpleName(), p.typeArguments.stream() //
          .map(x -> getFixedRecReturnType(x, vs1, vs2)) //
          .collect(toList()));
    } else
      throw new RuntimeException("Class " + r.getClass().getSimpleName() + " not supported in getFixedRecReturnType");
  }
  private TypeSpec encodeItem(Item i) {
    final Collection<Verb> firstSet = rllp.analyzer.firstSetOf(i);
    Collection<TypeVariableName> namedFollowSet = mapFollowSetWith(i, v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)));
    final String typeName = namer.getItemName(i);
    final TypeSpec.Builder encoding = TypeSpec.interfaceBuilder(typeName) //
        .addModifiers(Modifier.PUBLIC) //
        // Adds push methods
        .addMethods(firstSet.stream().map(v -> methodOf(i, v)).collect(Collectors.toList()));
    // Adds jump methods
    if (rllp.analyzer.isSuffixNullable(i)) {
      encoding.addMethods(mapFollowSetWith(i, v -> methodOf(i, v)));
      if (rllp.analyzer.followSetOf(i.rule.lhs).contains(SpecialSymbols.$))
        encoding.addSuperinterface(EncoderUtils.returnTypeOf$());
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
    return TypeSpec.interfaceBuilder(EncoderUtils.error)//
        .addModifiers(Modifier.PRIVATE)//
        .build();
  }
  private static void augmentMethodParameters(Builder builder, Verb v) {
    for (int i = 0; i < v.type.length; ++i) {
      ParameterType t = v.type[i];
      if (t instanceof ClassType)
        builder.addParameter(((ClassType) t).clazz, "arg" + i);
      else if (t instanceof NestedType)
        builder.addParameter(EncoderUtils.returnTypeOf$(), "arg" + i).build();
      else if (t instanceof VarArgs) {
        builder.varargs();
        builder.addParameter(ParameterSpec.builder(((VarArgs) t).aclazz, "arg" + i).build());
      } else
        throw new IllegalArgumentException("Type of verb is unknown");
    }
  }
  private TypeName returnTypeOfMethod(Item i, Verb v) {
    final Action action = rllp.predict(i, v);
    switch (action.type()) {
      default:
        throw new IllegalStateException("Action type unknown");
      case ACCEPT:
        return EncoderUtils.returnTypeOf$();
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
    final TypeSpec $ = TypeSpec.interfaceBuilder(namer.getRecursiveTypeName(jsm)) //
        .addModifiers(Modifier.PUBLIC) //
        .addTypeVariables(mapFollowSetWith(mainItem, v -> TypeVariableName.get(EncoderUtils.verbTypeName(v)))) //
        .build();
    if (!recursiveTypes.stream().anyMatch(t -> t.toString().equals($.toString())))
      recursiveTypes.add($);
    recArgs.put($, args);
    recSuper.put($, namer.getItemName(jsm.peek()));
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
    return TypeSpec.classBuilder(rllp.bnf.name) //
        .addModifiers(Modifier.PUBLIC) //
        .addTypes(mainTypes) //
        .addTypes(recursiveTypes) //
        .addType(getErrorType()) //
        .addType($$$Type) //
        .addMethods(staticMethods) //
        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "all").build()) //
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
    return rllp.bnf.name;
  }
  private TypeSpec get$$$Type() {
    List<MethodSpec> ms = an.empty.list();
    for (TypeSpec t : mainTypes)
      ms.addAll(t.methodSpecs.stream() //
          .filter(x -> ms.stream().noneMatch(y -> x.name.equals(y.name) && x.parameters.equals(y.parameters))) //
          .map(x -> MethodSpec.methodBuilder(x.name) //
              .returns(TypeVariableName.get($$$name)) //
              .addParameters(x.parameters) //
              .varargs(x.varargs) //
              .addModifiers(Modifier.PUBLIC) //
              .addCode("recordTerminal(" + getTerminalName(x) + FajitaEncoder.getArguments(x.parameters) + ");") //
              .addCode("return this;") //
              .build())
          .collect(toList()));
    return TypeSpec.classBuilder($$$name) //
        .superclass(FluentAPIRecorder.class) //
        .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
        .addSuperinterfaces(mainTypes.stream().map(x -> TypeVariableName.get(x.name)).collect(toList())) //
        .addSuperinterfaces(recursiveTypes.stream().map(x -> TypeVariableName.get(x.name)).collect(toList())) //
        .addMethod(MethodSpec.constructorBuilder() //
            .addCode("super(new " + provider.getName() + "().bnf().bnf()" + subBNFFix() + ");") //
            .build()) //
        .addMethods(ms) //
        .build();
  }
  private String subBNFFix() {
    if (!isSubBNF)
      return "";
    NonTerminal nt = rllp.bnf.startSymbols.iterator().next();
    if (nt instanceof Enum<?>)
      return ".getSubBNF(" + nt.getClass().getCanonicalName() + "." + nt + ")";
    return ".getSubBNF(org.spartan.fajita.api.bnf.BNF.nonTerminal(\"" + nt + "\"))";
  }
  public String getTerminalName(MethodSpec x) {
    return getTerminalName(x.name);
  }
  public String getTerminalName(String name) {
    Terminal match = terminals.stream().filter(z -> z.name().equals(name)).findFirst().get();
    return (match.getClass().getCanonicalName() + "." + match).replaceAll("\\$", "\\$\\$");
  }
}
