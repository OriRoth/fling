package il.ac.technion.cs.fling.processors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;
import il.ac.technion.cs.fling.annotations.DerivesTo;
import il.ac.technion.cs.fling.annotations.DerivesTos;
import il.ac.technion.cs.fling.annotations.Fling;
import il.ac.technion.cs.fling.annotations.Parameters;
import il.ac.technion.cs.fling.annotations.Start;
import il.ac.technion.cs.fling.annotations.Terminals;
import il.ac.technion.cs.fling.annotations.Variables;
import il.ac.technion.cs.fling.internal.grammar.rules.Body;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.ERule;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.internal.grammar.types.Parameter;

@SupportedAnnotationTypes("il.ac.technion.cs.fling.annotations.Fling")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FlingProcessor extends FancyProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
		for (Element element : roundEnvironment.getElementsAnnotatedWith(Fling.class)) {
			try {
				EBNF bnf = getBNF(element);
				if (bnf == null)
					continue;
				generateAPIs(element, bnf);
			} catch (IOException | FormatterException e) {
				debug(format("ERROR: %s: %s", e.getClass(), e.getMessage()), element);
				throw new RuntimeException(e);
			} catch (Exception e) {
				debug(format("ERROR: %s: %s", e.getClass(), e.getMessage()), element);
				throw e;
			}
		}
		return true;
	}

	private EBNF getBNF(Element element) {
		Map<String, Token> tokens = null;
		Element tokensEnum = null;
		Set<Variable> variables = null;
		Element variablesEnum = null;
		Variable startVariable = null;
		Set<ERule> productions = null;
		for (Element child : element.getEnclosedElements()) {
			if (child.getAnnotation(Terminals.class) != null) {
				tokensEnum = child;
				tokens = getTokens(child);
			}
			if (child.getAnnotation(Variables.class) != null) {
				variablesEnum = child;
				variables = getVariables(child);
				startVariable = getStartVariable(child);
			}
		}
		if (tokens != null && variables != null)
			for (Element child : element.getEnclosedElements())
				if (child.getAnnotation(Variables.class) != null)
					productions = getProductions(child, tokens,
							variables.stream().map(Variable::name).collect(toSet()));
		if (!validateGrammarComponents(element, tokens, tokensEnum, variables, variablesEnum, startVariable,
				productions))
			return null;
		EBNF bnf = new EBNF(new LinkedHashSet<>(tokens.values()), variables, startVariable, productions);
		return !validateGrammarLL1(bnf, variablesEnum) ? null : bnf;
	}

	private void generateAPIs(Element element, EBNF bnf) throws IOException, FormatterException {
		String apiName = element.getSimpleName().toString();
		String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
		boolean defaultPackage = packageName == null || "".equals(packageName);
		String generatedPackageName = defaultPackage ? "generated" : packageName + ".generated";
		String tokensEnumName = null;
		for (Element child : element.getEnclosedElements())
			if (child.getAnnotation(Terminals.class) != null)
				tokensEnumName = child.getSimpleName().toString();
		assert tokensEnumName != null;
		generateAPIFiles(element, new JavaMediator(bnf, generatedPackageName, apiName,
				(defaultPackage ? "" : packageName + ".") + apiName + "." + tokensEnumName));
	}

	private boolean validateGrammarComponents(Element element, Map<String, Token> tokens, Element tokensEnum,
			Set<Variable> variables, Element variablesEnum, Variable startVariable, Set<ERule> productions) {
		boolean error = false;
		if (tokens == null) {
			error = true;
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"no grammar terminals; define enum with @Terminals", element);
		} else {
			if (tokens.isEmpty()) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "no grammar terminals defined",
						tokensEnum);
			}
			if (processingEnv.getTypeUtils().directSupertypes(tokensEnum.asType()).stream().map(Object::toString)
					.noneMatch(Terminal.class.getCanonicalName()::equals)) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						String.format("terminals enum must implements %s", Terminal.class.getCanonicalName()),
						tokensEnum);
			}
		}
		if (variables == null) {
			error = true;
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
					"no grammar variables; define enum with @Variables", element);
		} else {
			if (variables.isEmpty()) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "no grammar variables defined",
						tokensEnum);
			} else {
				if (startVariable == null) {
					error = true;
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"no start variable; use @Start annotation", variablesEnum);
				}
				if (productions == null)
					error = true;
				else if (productions.isEmpty()) {
					error = true;
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							"no grammar productions; use @DerivesTo annotation", variablesEnum);
				}
			}
			if (processingEnv.getTypeUtils().directSupertypes(variablesEnum.asType()).stream().map(Object::toString)
					.noneMatch(Variable.class.getCanonicalName()::equals)) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						String.format("variables enum must implements %s", Variable.class.getCanonicalName()),
						variablesEnum);
			}
		}
		return !error;
	}

	private boolean validateGrammarLL1(EBNF bnf, Element variablesEnum) {
		FancyEBNF fbnf = FancyEBNF.from(bnf);
		boolean error = false;
		for (Variable v : fbnf.Γ) {
			// Verify rule 1
			Set<Token> seenFirsts = new LinkedHashSet<>();
			Set<Token> conflictingTerminals = new LinkedHashSet<>();
			for (ERule r : fbnf.R)
				if (v.equals(r.variable))
					for (Body b : r.bodiesList()) {
						Set<Token> intersection = new LinkedHashSet<>(seenFirsts);
						intersection.retainAll(fbnf.firsts(b));
						conflictingTerminals.addAll(intersection);
						seenFirsts.addAll(fbnf.firsts(b));
					}
			if (!conflictingTerminals.isEmpty()) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String
						.format("variable %s has an LL(1) conflict on terminal(s) %s", v.name(), conflictingTerminals),
						variablesEnum);
			}
			// Verify rule 2
			boolean nullable = false;
			Body nullableRhs = null;
			boolean epsilonConflict = false;
			for (ERule r : fbnf.R)
				if (v.equals(r.variable))
					for (Body b : r.bodiesList()) {
						if (fbnf.isNullable(b)) {
							epsilonConflict |= nullable;
							nullable = true;
							nullableRhs = b;
						}
					}
			if (epsilonConflict) {
				error = true;
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
						String.format("variable %s has an LL(1) conflict on ε", v.name()), variablesEnum);
			}
			// Verify rule 3
			if (nullable && !epsilonConflict) {
				assert nullableRhs != null;
				Set<Token> followsConflicts = new LinkedHashSet<>();
				for (ERule r : fbnf.R)
					if (v.equals(r.variable))
						for (Body b : r.bodiesList())
							if (!nullableRhs.equals(b)) {
								Set<Token> intersection = new LinkedHashSet<>(fbnf.follows.get(v));
								intersection.retainAll(fbnf.firsts(b));
								if (!intersection.isEmpty())
									followsConflicts.addAll(intersection);
							}
				if (!followsConflicts.isEmpty()) {
					error = true;
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
							String.format("variable %s has an LL(1) \"follows\" conflict on terminal(s) %s", v.name(),
									followsConflicts),
							variablesEnum);
				}
			}
		}
		return !error;
	}

	private Map<String, Token> getTokens(Element terminalsEnum) {
		Map<String, Token> tokens = new LinkedHashMap<>();
		for (Element terminal : terminalsEnum.getEnclosedElements())
			if (ElementKind.ENUM_CONSTANT.equals(terminal.getKind())) {
				String name = terminal.getSimpleName().toString();
				List<Parameter> parameters = new ArrayList<>();
				for (AnnotationMirror annotation : terminal.getAnnotationMirrors())
					if (Parameters.class.getCanonicalName().equals(annotation.getAnnotationType().toString()))
						for (AnnotationValue parameterClasses : annotation.getElementValues().values())
							((List<AnnotationValue>) parameterClasses.getValue()).stream()
									.map(AnnotationValue::getValue).map(Object::toString).map(RawClassParameter::new)
									.forEach(parameters::add);
				tokens.put(name, new Token(Terminal.byName(name), parameters.toArray(new Parameter[0])));
			}
		return tokens;
	}

	private Set<Variable> getVariables(Element variablesEnum) {
		Set<Variable> variables = new LinkedHashSet<>();
		for (Element variable : variablesEnum.getEnclosedElements())
			if (ElementKind.ENUM_CONSTANT.equals(variable.getKind()))
				variables.add(Variable.byName(variable.getSimpleName().toString()));
		return variables;
	}

	private Variable getStartVariable(Element variablesEnum) {
		for (Element variable : variablesEnum.getEnclosedElements())
			if (ElementKind.ENUM_CONSTANT.equals(variable.getKind()) && variable.getAnnotation(Start.class) != null)
				return Variable.byName(variable.getSimpleName().toString());
		return null;
	}

	private Set<ERule> getProductions(Element variablesEnum, Map<String, Token> tokens, Set<String> variables) {
		Set<ERule> productions = new LinkedHashSet<>();
		boolean error = false;
		for (Element variableConstant : variablesEnum.getEnclosedElements())
			if (ElementKind.ENUM_CONSTANT.equals(variableConstant.getKind())) {
				Variable variable = Variable.byName(variableConstant.getSimpleName().toString());
				for (AnnotationMirror annotation : variableConstant.getAnnotationMirrors()) {
					String annotationName = annotation.getAnnotationType().toString();
					if (DerivesTos.class.getCanonicalName().equals(annotationName))
						for (AnnotationMirror productAnnotation : getRepeatedAnnotations(annotation)) {
							List<Component> rhs = new ArrayList<>();
							for (AnnotationValue rhsValues : productAnnotation.getElementValues().values())
								for (AnnotationValue symbol : (List<AnnotationValue>) rhsValues.getValue()) {
									String symbolName = symbol.getValue().toString();
									if (tokens.containsKey(symbolName))
										rhs.add(tokens.get(symbolName));
									else if (variables.contains(symbolName))
										rhs.add(Variable.byName(symbolName));
									else {
										// TODO why printing to `variableConstant` does not produce an error?
										processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
												String.format("symbol \"%s\" not defined", symbolName), variablesEnum,
												annotation);
										error = true;
									}
								}
							productions.add(new ERule(variable, new Body(rhs)));
						}
					else if (DerivesTo.class.getCanonicalName().equals(annotationName)) {
						List<Component> rhs = new ArrayList<>();
						for (AnnotationValue rhsValues : annotation.getElementValues().values())
							for (AnnotationValue symbol : (List<AnnotationValue>) rhsValues.getValue()) {
								String symbolName = symbol.getValue().toString();
								if (tokens.containsKey(symbolName))
									rhs.add(tokens.get(symbolName));
								else if (variables.contains(symbolName))
									rhs.add(Variable.byName(symbolName));
								else {
									// TODO why printing to `variableConstant` does not produce an error?
									processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
											String.format("symbol \"%s\" not defined", symbolName), variablesEnum,
											annotation);
									error = true;
								}
							}
						productions.add(new ERule(variable, new Body(rhs)));
					}
				}
			}
		return error ? null : productions;
	}

	private void generateAPIFiles(Element source, JavaMediator mediator) throws IOException, FormatterException {
		String baseName = mediator.packageName.replaceAll("\\.", File.separator) + File.separator + mediator.apiName;
		printToFile(source, baseName, mediator.apiClass);
		printToFile(source, baseName + "AST", mediator.astClass);
		printToFile(source, baseName + "Compiler", mediator.astCompilerClass);
	}

	private void printToFile(Element source, String path, String content) throws IOException, FormatterException {
		JavaFileObject file = processingEnv.getFiler().createSourceFile(path, source);
		try (OutputStream writer = file.openOutputStream()) {
			writer.write(new Formatter().formatSource(content).getBytes());
		}
	}
}
