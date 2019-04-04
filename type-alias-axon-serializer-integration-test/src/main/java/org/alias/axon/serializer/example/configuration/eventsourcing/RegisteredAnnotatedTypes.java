package org.alias.axon.serializer.example.configuration.eventsourcing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

class RegisteredAnnotatedTypes {
	private final Collection<Class<?>> allTypes = new ArrayList<>();

	public static final RegisteredAnnotatedTypes fromResourceBundleName(String resourceBundleName) {
		return fromResourceBundle(ResourceBundle.getBundle(resourceBundleName));
	}

	public static final RegisteredAnnotatedTypes fromResourceBundle(ResourceBundle resourceBundle) {
		Collection<Class<?>> allRegisteredTypes = new ArrayList<Class<?>>();
		for (String alias : resourceBundle.keySet()) {
			Object value = resourceBundle.getObject(alias);
			if (value instanceof Class) {
				allRegisteredTypes.add((Class<?>) value);
			}
		}
		return new RegisteredAnnotatedTypes(allRegisteredTypes);
	}

	protected RegisteredAnnotatedTypes(Collection<Class<?>> allTypes) {
		this.allTypes.addAll(allTypes);
	}

	public Collection<Class<?>> annotatedWith(Class<? extends Annotation> annotationClass) {
		return allTypes.stream().filter(type -> isAnnotationPresent(annotationClass, type)).collect(Collectors.toList());
	}

	private static boolean isAnnotationPresent(Class<? extends Annotation> annotationClass, Class<?> type) {
		if (type.isAnnotationPresent(annotationClass)) {
			return true;
		}
		for (Method method : type.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		return false;
	}
}