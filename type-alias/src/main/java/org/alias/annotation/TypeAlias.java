package org.alias.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assigns a distinct alias name to the annotated type. Optional, an external type may also be specified.
 * <p>
 * For every {@link TypeAlias} annotated type, a entry is written into a generated file. <br>
 * The generated file can be customized by the annotation {@link TypeAliasGeneratedFile}.
 * <p>
 * The annotation @{@link TypeAliases} can be used to define more than on (external) alias. <br>
 * This enables defining aliases for types (by their name) on any type, <br>
 * where it is not possible to place additional annotations (e.g.external libraries).
 * <p>
 * <b>Compile-time dependent only:</b><br>
 * Since this annotation is defined with {@link RetentionPolicy#SOURCE}, <br>
 * it will be removed during compilation and is not present during runtime.<br>
 * This assures, that the dependency to this library is only necessary at compile-time.<br>
 * It also means, that is not visible when using runtime reflection methods like {@link Class#getAnnotation(Class)}.
 * 
 * @author JohT
 * @see TypeAliases
 * @see TypeAliasGeneratedFile
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TypeAlias {

	/**
	 * The alias name of the type.
	 * <p>
	 * The value may not be empty.<br>
	 * The value needs to be distinct. Two types may not have the same alias name.
	 * 
	 * @return alias name of the type as {@link String}
	 */
	String value();

	/**
	 * (Optional) The type name, that is assigned to the alias.
	 * <p>
	 * If not specified, the annotated type (where this annotation is places) is taken as default.<br>
	 * For details, why this parameter is a {@link String} ans not a {@link Class}, see
	 * {@link AnnotatedConstruct#getAnnotation(Class).}
	 * 
	 * @return full qualified type name {@link String} or an empty {@link String} representing the annotated type.
	 */
	String type() default "";
}