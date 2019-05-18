package org.alias.axon.serializer.example.messaging.boundary.query.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.axonframework.config.ProcessingGroup;

/**
 * Hint for the Configuration API that the annotated Event Handler object should be assigned to an Event Processor with
 * the specified name.
 * <p>
 * This annotation is based on axon framework's "@ProcessingGroup".
 * 
 * @see ProcessingGroup
 */
//Note: For meta-annotations with value() parameter, 
//the parameter needs to be renamed to the simple class name of the original annotation.
//Thus, "value" cannot be used here and it needs to be defined as processingGroup().
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ProcessingGroup("")
public @interface QueryModelProjection {

	/**
	 * The name of the Event Processor to assign the annotated Event Handler object to.
	 *
	 * @return the name of the Event Processor to assign objects of this type to
	 */
	String processingGroup();
}
