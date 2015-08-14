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

    public void deriveTo(Compound c) {
	if (InheritedNonterminal.class.isAssignableFrom(c.getClass()))
	    // we have a double InheritedNonterminal. deleting one of them
	    c = ((InheritedNonterminal) c).derivedTo();

	children.clear();
	children.add(c);
	c.setParent(this);
    }

    private Compound derivedTo() {
	return children.get(0);
    }
}
