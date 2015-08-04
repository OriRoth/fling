package org.spartan.fajita.api.ast;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Atomic extends Compound {

	public Atomic(final Compound parent, final Object... parameters) {
		super(parent,parameters);
	}

	@Override
	public ArrayList<Compound> getChildren() {
		return new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return super.toString() +" = "+ Arrays.toString(params);
	}
}
