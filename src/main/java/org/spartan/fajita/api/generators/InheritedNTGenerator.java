package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.ntClassname;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.ast.InheritedNonterminal;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;
class InheritedNTGenerator<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {

    private final String baseNT;

    private final JavaInterfaceSource baseInterface;
    private final JavaClassSource baseNTClass;
    private final JavaClassSource baseCompoundClass;
    private final JavaClassSource[] subNTClasses;

    public InheritedNTGenerator(final InheritenceRule<Term, NT> rule) {
	baseNT = ntClassname(rule.lhs);
	baseInterface = generateInterface();

	baseNTClass = generateInheritedNonterminalClass();

	baseCompoundClass = generateCompoundSubclass();

	subNTClasses = new JavaClassSource[rule.subtypes.size()];
	for (int i = 0; i < rule.subtypes.size(); ++i){
	    subNTClasses[i] = NonterminalsGenerator.generateNTClass(rule.lhs,rule.subtypes.get(i),true);
	    addEmptyConsrtuctor(subNTClasses[i]);
	}

    }

    private void addEmptyConsrtuctor(final JavaClassSource ntClass) {
	ntClass.addMethod().setConstructor(true) //
		.setBody("this(new " + baseNT + "());")
	.getJavaDoc().setText("for bottom-up");
    }

    private JavaClassSource generateInheritedNonterminalClass() {
	JavaClassSource $ = Roaster.create(JavaClassSource.class) //
		.setName(baseNT) //
		.setPackage("fajita") //
		.setPackagePrivate() //
		.setStatic(true) //
		.setSuperType(InheritedNonterminal.class) //
		.addInterface(baseInterface);

	MethodSource<JavaClassSource> noParamsConstructor = $.addMethod().setConstructor(true) //
		.setPackagePrivate()//
		.setBody("super(null);");

	noParamsConstructor.getJavaDoc() //
		.setText("for bottom-up");

	MethodSource<JavaClassSource> paramsConstructor = $.addMethod().setConstructor(true) //
		.setPackagePrivate()//
		.setBody("super(parent);");//

	paramsConstructor.addParameter(Compound.class, "parent").setFinal(true);

	paramsConstructor.getJavaDoc() //
		.setText("for top-down");

	$.addMethod() //
		.setName("getName")//
		.setPublic()//
		.setReturnType(String.class) //
		.setBody("return \"" + baseNT.toUpperCase() + "\";");

	return $;
    }

    private JavaInterfaceSource generateInterface() {
	return Roaster.create(JavaInterfaceSource.class) //
		.setName("I" + baseNT) //
		.setPackage("fajita");
    }

    private JavaClassSource generateCompoundSubclass() {
	JavaClassSource $ = Roaster.create(JavaClassSource.class)//
		.setName("Compound" + baseNT) //
		.setPackage("fajita") //
		.setPublic().setStatic(true).setAbstract(true) //
		.setSuperType(Compound.class) //
		.addInterface(baseInterface);

	$.addMethod().setConstructor(true) //
		.setPackagePrivate() //
		.setBody("super(parent);" + "parent.deriveTo(this);").addParameter(baseNTClass, "parent")
		.setFinal(true); //

	MethodSource<JavaClassSource> getParent = $.addMethod() //
		.setName("getParent").setPublic() //
		.setReturnType(baseNTClass) //
		.setBody("return (" + baseNTClass.getName() + ")" + "super.getParent();");

	getParent.addAnnotation(Override.class);

	return $;
    }

    public Collection<JavaSource<?>> generate() {
	ArrayList<JavaSource<?>> $ = new ArrayList<>();
	$.add(baseCompoundClass);
	$.add(baseInterface);
	$.add(baseNTClass);
	$.addAll(Arrays.asList(getSubNTClasses()));
	return $;
    }

    public JavaClassSource[] getSubNTClasses() {
	return subNTClasses;
    }

}