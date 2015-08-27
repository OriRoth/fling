package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.*;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

class NonterminalsGenerator<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {
    private final DerivationRule<Term, NT> rule;
    private final FieldSource<JavaClassSource>[] children;

    @SuppressWarnings("unchecked")
    public NonterminalsGenerator(final DerivationRule<Term, NT> rule) {
	this.rule = rule;
	children = new FieldSource[rule.expression.size()];
    }

    private void modifyExisting(final JavaClassSource ntClass,final String parent) {
	addFields(ntClass);
	addGetChildren(ntClass);
	addConstructor(ntClass, parent);
    }
    
    void modifyExisting(final JavaClassSource ntClass,final NonTerminal nonTerminal) {
	modifyExisting(ntClass, ntClassname(nonTerminal));
    }

    JavaClassSource createNew() {
	JavaClassSource ntClass = generateNTClass(rule.lhs);
	modifyExisting(ntClass,"Compound");
	return ntClass;
    }

    private void addConstructor(final JavaClassSource ntClass, final String superclass) {
	String body = "super(parent);";
	for (int i = 0; i < children.length; ++i)
	    body += children[i].getName() + " = (" + children[i].getType().getName() + ") getChild(" + i + ");";

	MethodSource<JavaClassSource> constructorWithParams = ntClass.addMethod()//
		.setConstructor(true) //
		.setBody(body); //
	
	constructorWithParams.addParameter(superclass, "parent").setFinal(true);
	constructorWithParams.getJavaDoc().setText("for top-down");
    }

    private void addFields(final JavaClassSource ntClass) {
	for (int i = 0; i < rule.expression.size(); ++i) {
	    Symbol symbol = rule.expression.get(i);
	    String type = NonTerminal.class.isAssignableFrom(symbol.getClass()) ? ntClassname((NonTerminal) symbol)
          : termClassname((Terminal) symbol);
	    children[i] = ntClass.addField() //
		    .setName("child" + i) //
		    .setType(type) //
		    .setFinal(true);
	}
    }

    private void addGetChildren(final JavaClassSource ntClass) {
	String body = "ArrayList<Compound> $ = new ArrayList<>();";
	for (Symbol symbol : rule.expression)
    body += "$.add(new " + (NonTerminal.class.isAssignableFrom(symbol.getClass()) ? ntClassname((NonTerminal) symbol)
        : termClassname((Terminal) symbol)) + "(this));";
	body += "return $;";
	ntClass.addMethod() //
		.setName("getChildren") //
		.setProtected() //
		.setReturnType("ArrayList<Compound>") //
		.setBody(body) //
		.addAnnotation(Override.class);

    }

    private static <NT extends Enum<NT> & NonTerminal> JavaClassSource generateNTClass(final NonTerminal type){
	return generateNTClass(null,type, false);	
    }
    
    static <NT extends Enum<NT> & NonTerminal> JavaClassSource generateNTClass(final NonTerminal lhs,
	    final NonTerminal subtype, final boolean isInheritedNT) {
	JavaClassSource $ = Roaster.create(JavaClassSource.class) //
		.setName(ntClassname(subtype)) //
		.setPublic().setStatic(true) //
		.setPackage("fajita"); //
	
	$.setSuperType(!isInheritedNT ? Compound.class : inheritedNTCompoundClassname(lhs));

	// getName()
	$.addMethod()//
		.setName("getName") //
		.setPublic()//
		.setReturnType(String.class) //
		.setBody("return \"" + ntClassname(subtype).toUpperCase() + "\";") //
		.addAnnotation(Override.class);

	return $;
    }

}
