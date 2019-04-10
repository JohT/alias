package org.alias.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies multiple {@link TypeAlias}es.<br>
 * This only meant to define additional, external {@link TypeAlias}es on any type.
 * <p>
 * <b>Compile-time dependent only:</b><br>
 * Since this annotation is defined with {@link RetentionPolicy#SOURCE}, <br>
 * it will be removed during compilation and is not present during runtime.<br>
 * This assures, that the dependency to this library is only necessary at compile-time.<br>
 * It also means, that is not visible when using runtime reflection methods like {@link Class#getAnnotation(Class)}.
 * 
 * @author JohT
 */
@Documented
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.SOURCE)
public @interface TypeAliases {

	/**
	 * Array of {@link TypeAlias}es.
	 * <p>
	 * The value may not be empty.<br>
	 * 
	 * @return {{@link TypeAlias} array.
	 */
	TypeAlias[] value();
}