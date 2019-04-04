package org.alias.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Optional customization of the {@link TypeAlias} file generation per package. <br>
 * This annotation is meant to be used inside the file <i>package-info.java</i> on top of the keyword
 * <code>package</code>.
 * <p>
 * If desired, {@link TypeAliasGeneratedFile} can also be placed on top of a type.<br>
 * There is one exceptional case, where this is the only solution:<br>
 * It doesn't seem to be possible to put annotations inside the <i>package-info.java</i>,<br>
 * when it is located in the default (unnamed) package.
 * <p>
 * <b>Important note:</b><br>
 * More than one customization per package will lead to an compile error. <br>
 * If <i>package-info.java</i> is annotated, then no type inside the same package is allowed to be annotated with
 * {@link TypeAliasGeneratedFile}.<br>
 * Two or more types annotated with {@link TypeAliasGeneratedFile} must not be located inside the same package. <br>
 * Sub packages are allowed to have their own customization, even if their outer package has another.
 * <p>
 * <b>Default behavior:</b><br>
 * If this annotation isn't used, the {@link #DEFAULT_TEMPLATE} will be used to generate one file with the name
 * {@value #DEFAULT_FILE_NAME} located in the default/unnamed package. <br>
 * It is recommended, to use this only as a starting point and for very simple projects. If you wan't to use aliases in
 * more than one module an want to be more flexible, it is advisable to use {@link TypeAliasGeneratedFile} early on.
 * <p>
 * <b>Scope:</b><br>
 * The settings refer to every {@link TypeAlias} annotated element contained in that package and all its sub-packages.
 * <p>
 * <b>Compile-time dependent only:</b><br>
 * Since this annotation is defined with {@link RetentionPolicy#SOURCE}, <br>
 * it will be removed during compilation and is not present during runtime.<br>
 * This assures, that the dependency to this library is only necessary at compile-time.<br>
 * It also means, that is not visible when using runtime reflection methods like {@link Class#getAnnotation(Class)}.
 * <p>
 * This annotation can be used...
 * <ul>
 * <li>to give the generated file an individual name, that is independent from the default name.
 * <li>to give the generated file an distinct name, that doesn't interfere with others.
 * <li>to separate different {@link TypeAlias}-annotated element groups from each other.
 * <li>to support modularization, so that parts of modules with {@link TypeAlias}-annotated elements can be moved.
 * <li>to choose another type of generated file (e.g. a property file) beside the default resource bundle.
 * </ul>
 * 
 * @author JohT
 * @see TypeAlias
 */
@Documented
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface TypeAliasGeneratedFile {

	public static final String DEFAULT_FILE_NAME = "TypeAlias";

	/**
	 * Name of the generated file without extension (e.g. without ".properties").
	 * <p>
	 * Defaults to {@value #DEFAULT_FILE_NAME}.
	 * 
	 * @return {@link String} name of file
	 */
	String value() default DEFAULT_FILE_NAME;
	
	/**
	 * Determines the type of file that will be generated based on predefined {@link Template}s.
	 * <p>
	 * Defaults to {@link Template#RESOURCE_BUNDLE}.
	 * 
	 * @return {@link Template}
	 */
	Template template() default Template.RESOURCE_BUNDLE;

	public static enum Template {

		/**
		 * Generates a property file with file extension <i>properties</i>.
		 * <p>
		 * A property file can be loaded like every other file, using {@link Properties} or<br>
		 * as a {@link ResourceBundle} (see example for {@link #RESOURCE_BUNDLE}).
		 * <h3>Example how Properties can be loaded:</h3> <blockquote>
		 * 
		 * <pre>
		 * String propertyFileName = "org" + File.separator + "TypeAlias" + ".properties";
		 * Properties aliases = new Properties();
		 * try (InputStream inputStream = classLoader.getResourceAsStream(propertyFileName)) {
		 * 	aliases.load(inputStream);
		 * }
		 * </pre>
		 * 
		 * </blockquote>
		 */
		PROPERTY_FILE("propertyfile.template", "properties"),
		/**
		 * Generates a {@link ResourceBundle} with the file extension <i>java</i>.<br>
		 * It contains the alias names as keys and their assigned {@link Class} as values,<br>
		 * as well as the full qualified class names as keys and their assigned alias {@link String} (bidirectional).
		 * 
		 * <p>
		 * <h3>Example how a ResourceBundle can be loaded:</h3> <blockquote>
		 * 
		 * <pre>
		 * String resourceBundleName = "org.TypeAlias";
		 * ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName);
		 * </pre>
		 * 
		 * </blockquote>
		 */
		RESOURCE_BUNDLE("resourcebundle.template", "java"),
		/**
		 * Generates a {@link ResourceBundle} with the file extension <i>java</i>.<br>
		 * It simply contains the alias names as keys and their assigned {@link Class} as values.
		 * <p>
		 * <h3>Example how a ResourceBundle can be loaded:</h3> <blockquote>
		 * 
		 * <pre>
		 * String resourceBundleName = "org.TypeAlias";
		 * ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName);
		 * </pre>
		 * 
		 * </blockquote>
		 */
		RESOURCE_BUNDLE_SIMPLE("resourcebundleSimple.template", "java"),

		;

		private final String templatename;
		private final String fileExtension;

		private Template(String templatename, String fileExtension) {
			this.templatename = templatename;
			this.fileExtension = fileExtension;
		}

		public String getTemplatename() {
			return templatename;
		}

		public String getFileExtension() {
			return fileExtension;
		}
	}
}