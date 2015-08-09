package org.spartan.fajita.api.generators;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.bnf.symbols.Terminal;

class TerminalGenerator<Term extends Enum<Term> & Terminal> {
    private final Term terminal;

    public TerminalGenerator(final Term terminal) {
	this.terminal = terminal;
    }

    JavaSource<?> generate() {
	JavaClassSource $ = Roaster.create(JavaClassSource.class) //
		.setName(GeneratorsUtils.termClassname(terminal)) //
		.setPublic().setStatic(true) //
		.setSuperType(Atomic.class);

	$.addMethod().setConstructor(true) //
		.setBody("super(parent);")//
		.addParameter(Compound.class, "parent").setFinal(true);

	$.addMethod().setName("getName") //
		.setPublic() //
		.setReturnType(String.class) //
		.setBody("return \"" + terminal.name().toLowerCase() + "\";")//
		.addAnnotation(Override.class);
	
	return $;
    }
}
