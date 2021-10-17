package il.ac.technion.cs.fling.processors;

import static java.util.Objects.requireNonNull;

import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;
import il.ac.technion.cs.fling.namers.NaiveNamer;
import il.ac.technion.cs.fling.internal.grammar.types.StringTypeParameter;

public class RawClassParameter implements StringTypeParameter {
	public final String parameterClass;

	public RawClassParameter(String parameterClass) {
		this.parameterClass = requireNonNull(parameterClass);
	}

	@Override
	public String typeName() {
		return parameterClass;
	}

	@Override
	public String baseParameterName() {
		return ClassParameter.unPrimitiveTypeSimple(NaiveNamer.lowerCamelCase(getSimpleName()));
	}

	@Override
	public int hashCode() {
		return parameterClass.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RawClassParameter))
			return false;
		final RawClassParameter other = (RawClassParameter) obj;
		return parameterClass.equals(other.parameterClass);
	}

	@Override
	public String toString() {
		return getSimpleName();
	}

	private String getSimpleName() {
		String[] split = parameterClass.split("\\.");
		return split[split.length - 1];
	}
}
