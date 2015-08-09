package org.spartan.fajita.api.generators;

import static org.spartan.fajita.api.generators.GeneratorsUtils.ntClassname;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.rules.InheritenceRule;
import org.spartan.fajita.api.bnf.rules.Rule;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class ApiGenerator<Term extends Enum<Term> & Terminal, NT extends Enum<NT> & NonTerminal> {

    private final BNF<Term, NT> bnf;
    private final JavaClassSource containerClass;

    private final List<JavaClassSource> nonterminalClasses;
    private final Map<String,NonTerminal> inheritedNTToParent;
    public ApiGenerator(final BNF<Term, NT> bnf) {
	this.bnf = bnf;
	inheritedNTToParent = new HashMap<>();
	nonterminalClasses = new ArrayList<>();
	containerClass = Roaster.create(JavaClassSource.class) //
		.setName(bnf.getApiName()) //
		.setPublic();
	generateInheritedNonTerminals();
	generateTerminals();
	generateAllNonTerminals();
    }

    private void generateTerminals() {
	bnf.getTerminals().forEach(term -> {
	    containerClass.addNestedType(new TerminalGenerator<>(term).generate());
	});
    }

    private void generateInheritedNonTerminals() {
	for (Rule<Term, NT> rule : bnf.getRules()) {
	    if (!InheritenceRule.class.isAssignableFrom(rule.getClass()))
		continue;

	    InheritenceRule<Term, NT> rule2 = (InheritenceRule<Term, NT>) rule;
	    for ( NonTerminal subtype : rule2.subtypes)
		inheritedNTToParent.put(subtype.name(), rule2.lhs);
	    
	    InheritedNTGenerator<Term, NT> generator = new InheritedNTGenerator<>(rule2);
	    Collection<JavaSource<?>> typesToAdd = new ArrayList<>(generator.generate());
	    typesToAdd.removeAll(Arrays.asList(generator.getSubNTClasses()));
	    typesToAdd.forEach(clss -> containerClass.addNestedType(clss));
	    nonterminalClasses.addAll(Arrays.asList(generator.getSubNTClasses()));
	}

    }

    private void generateAllNonTerminals() {
	for (Rule<Term, NT> rule : bnf.getRules()) {
	    if (InheritenceRule.class.isAssignableFrom(rule.getClass()))
		continue;

	    NonterminalsGenerator<Term, NT> generator = new NonterminalsGenerator<>((DerivationRule<Term, NT>) rule);
	    Optional<JavaClassSource> optionalNTClass = nonterminalClasses.stream()
		    .filter(clss -> clss.getName().equals(ntClassname(rule.lhs))).findAny();
	    JavaClassSource ntClass = optionalNTClass.orElse(null);
	    if (!optionalNTClass.isPresent())
		ntClass = generator.createNew();
	    else
		generator.modifyExisting(optionalNTClass.get(),inheritedNTToParent.get(optionalNTClass.get().getName().toUpperCase()));

	    containerClass.addNestedType(ntClass);
	}
    }

    public String generate() {
	return containerClass.toString();
    }
}
