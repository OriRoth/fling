package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class InheritedNonterminal extends Compound {

	public InheritedNonterminal(final Compound parent, final String name) {
		super(parent, name);
	}

	@Override
	public ArrayList<Compound> getChildren() {
		// at this point we don't how this NT will be derived
		return new ArrayList<>();
	}
	
	public void deriveTo(final Compound c){
		children.add(c);
		c.parent = this;
	}
}
