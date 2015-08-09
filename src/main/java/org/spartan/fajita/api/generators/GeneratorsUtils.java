package org.spartan.fajita.api.generators;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Terminal;

class GeneratorsUtils {
    private static String formatName(final String nt) {
	return nt.substring(0, 1).toUpperCase() + nt.substring(1).toLowerCase();
    }
    
    static String termClassname(final Terminal term){
	return formatName(term.name())+"Term";
    }
    
    static String ntClassname(final NonTerminal nt){
	return formatName(nt.name());
    }
    
    static String inheritedNTCompoundClassname(final NonTerminal nt){
	return "Compound"+ntClassname(nt);
    }
    
    static String inheritedNTInterfaceClassname(final NonTerminal nt){
	return "I"+ntClassname(nt);
    }
    
}
