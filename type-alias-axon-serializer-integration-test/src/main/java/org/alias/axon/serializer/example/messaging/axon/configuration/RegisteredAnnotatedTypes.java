package org.alias.axon.serializer.example.messaging.axon.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.inject.spi.Bean;

class RegisteredAnnotatedTypes {
	private final Collection<Class<?>> allTypes = new ArrayList<>();

	public static final RegisteredAnnotatedTypes forBeans(Collection<? extends Bean<?>> beans) {
		return forClasses(beans.stream().map(bean -> bean.getBeanClass()).collect(Collectors.toList()));
	}

	public static final RegisteredAnnotatedTypes forClasses(Collection<Class<?>> allTypes) {
		return new RegisteredAnnotatedTypes(allTypes);
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