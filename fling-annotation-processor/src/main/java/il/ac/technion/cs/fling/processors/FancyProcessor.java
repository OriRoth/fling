package il.ac.technion.cs.fling.processors;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

public abstract class FancyProcessor extends AbstractProcessor {
	public Optional<? extends AnnotationMirror> getAnnotationMirror(Element element, Class<?> annotation) {
		return element.getAnnotationMirrors().stream().filter(
				a -> annotation.getSimpleName().equals(a.getAnnotationType().asElement().getSimpleName().toString()))
				.filter(a -> annotation.getPackage().getName().equals(processingEnv.getElementUtils()
						.getPackageOf(a.getAnnotationType().asElement()).getQualifiedName().toString()))
				.findAny();
	}

	public AnnotationValue getAnnotationRawValue(AnnotationMirror annotation, String executableElementName) {
		for (ExecutableElement ee : annotation.getElementValues().keySet())
			if (executableElementName.equals(ee.getSimpleName().toString()))
				return annotation.getElementValues().get(ee);
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAnnotationValue(AnnotationMirror annotation, String executableElementName) {
		AnnotationValue annotationValue = getAnnotationRawValue(annotation, executableElementName);
		return annotationValue == null ? null : (T) annotationValue.getValue();
	}

	public List<AnnotationMirror> getRepeatedAnnotations(AnnotationMirror annotation) {
		List<? extends AnnotationValue> annotations = getAnnotationValue(annotation, "value");
		return annotations.stream().map(AnnotationValue::getValue).map(AnnotationMirror.class::cast).collect(toList());
	}

	protected void debug(Object message, Element element) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, Objects.toString(message), element);
	}
}
