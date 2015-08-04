package org.spartan.fajita.api.ast;

import java.util.ArrayList;

public abstract class InheritedNonterminal extends Compound {

	public InheritedNonterminal(final Compound parent) {
		super(parent);
	}

	@Override
	public ArrayList<Compound> getChildren() {
		// at this point we don't know how this NT will be derived
		return new ArrayList<>();
	}
	
	public void deriveTo(final Compound c){
		children.clear();
		children.add(c);
		c.setParent(this);
	}
	
}
