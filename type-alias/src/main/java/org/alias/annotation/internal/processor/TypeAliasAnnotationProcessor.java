package org.alias.annotation.internal.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardLocation;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.TypeAliasGeneratedFile;
import org.alias.annotation.TypeAliases;
import org.alias.annotation.internal.templates.PlaceholderReplacer;
import org.alias.annotation.internal.templates.TypeAliasName;
import org.alias.annotation.internal.templates.TypeAliasTemplateBasedWriter;

/**
 * This Java Annotation {@link Processor} is called during compilation. <br>
 * It processes all {@link TypeAlias}- and {@link TypeAliasGeneratedFile}-Annotations.<br>
 * These annotation are only present during compile time and are removed afterwards.
 * <p>
 * Every {@link TypeAlias} is written into a generated property file <br>
 * containing the alias name and the assigned full qualified name of the type.
 * <p>
 * The property files names can optionally be configured per package.<br>
 * This is done using the annotation {@link TypeAliasGeneratedFile} inside <code>package-info.java</code>.<br>
 * Each property file contains all type aliases of the package and all contained sub packages<br>
 * and is located in the annotated package. <br>
 * Without any {@link TypeAliasGeneratedFile} annotation, <br>
 * all type aliases are generated into the file <code>TypeAlias.properties</code> located in the default package.
 * <p>
 * The full qualified name of this class is registered in the file<br>
 * <code>META-INF/services/javax.annotation.processing.Processor</code>.<br>
 * This assures, that the property file(s) are automatically generated during compilation.<br>
 * Therefore it is sufficient (and advisable), to only define the build dependency to this module as optional.
 * 
 * @author JohT
 */
public class TypeAliasAnnotationProcessor extends AbstractProcessor {

	private static final Class<TypeAlias> TYPE_ALIAS_ANNOTATION = TypeAlias.class;
	private static final String TYPE_ALIAS_ANNOTATION_PACKAGE = TYPE_ALIAS_ANNOTATION.getPackage().getName() + ".*";
	private static final Set<String> ANNOTATIONS = setOf(TYPE_ALIAS_ANNOTATION_PACKAGE);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ANNOTATIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<TypeAliasName> typeAliasNames = getTypeAliasNames(annotations, roundEnv);
		reportDuplicateAliasNames(typeAliasNames);
		reportDuplicateTypeNames(typeAliasNames);
		reportDuplicatePrimaryAliases(typeAliasNames);
		reportEmptyAliasNames(typeAliasNames);
		typeAliasNames = setAllDistinctAliasesToPrimary(typeAliasNames);
		TypeAliasGeneratedFileConfiguration configuration = new TypeAliasGeneratedFileConfiguration(annotations, roundEnv, getMessager());
		for (TypeAliasPackage packageContent : configuration.getAllPackagesFor(typeAliasNames)) {
			Collection<TypeAliasName> aliases = packageContent.getTypesInPackage();
			TypeAliasPackageInfo info = packageContent.getInfo();
			PlaceholderReplacer placeholder = info.asTemplateContentWith(aliases);
			TypeAliasTemplateBasedWriter writer = TypeAliasTemplateBasedWriter.forTemplateName(info.getTemplateName());
			generateFile(writer.writeContent(placeholder), StandardLocation.SOURCE_OUTPUT, info);
			if (!info.getFileExtension().equals("java")) {
				generateFile(writer.writeContent(placeholder), StandardLocation.CLASS_OUTPUT, info);
			}
		}
		return false;
	}

	/**
	 * Manages the file generation (creation, content writing, error handling, closing).<br>
	 * The given {@link PrintWriter}-{@link Consumer} determines, how the content is written.
	 * 
	 * @param info           {@link String} name of the property file
	 * @param typesInPackage {@link Stream} of {@link TypeElement}s
	 * @param location       {@link Location}
	 */
	private void generateFile(Consumer<PrintWriter> contentWriter, Location location, TypeAliasPackageInfo info) {
		try (PrintWriter writer = createFile(location, info)) {
			contentWriter.accept(writer);
		} catch (FilerException e) {
			note("Already created and opened output file at same location as " + location + ": " + e.getMessage());
		} catch (IOException e) {
			reportException(e, "Location=" + location);
		}
	}

	/**
	 * Creates a new File at the given {@link Location} for the given {@link TypeAliasPackageInfo}, <br>
	 * and opens a {@link PrintWriter} for it.
	 * 
	 * @param location {@link javax.tools.DocumentationTool.Location}
	 * @param info     {@link TypeAliasPackageInfo}
	 * @return {@link PrintWriter}
	 * @throws IOException
	 */
	private PrintWriter createFile(Location location, TypeAliasPackageInfo info) throws IOException {
		FileObject resource = null;
		if (info.isJavaSource()) {
			resource = getFiler().createSourceFile(info.getFullQualifiedJavaSourceType());
		} else {
			resource = getFiler().createResource(location, info.getPackageName(), info.getFileNameWithExtension());
		}
		return new PrintWriter(resource.openOutputStream());
	}

	/**
	 * Collects all type aliases into the {@link CollectionOfDuplicates},<br>
	 * which only collects the duplicates. <br>
	 * If there are any duplicates, an exception is reported.
	 * 
	 * @param typenames - {@link Set} of {@link TypeElement}s
	 */
	private void reportDuplicateAliasNames(Set<TypeAliasName> typenames) {
		Collection<String> duplicates = typenames.stream()
				.map(TypeAliasName::getAliasname)
				.collect(Collectors.toCollection(CollectionOfDuplicates::collectionOfDuplicates));
		if (!duplicates.isEmpty()) {
			reportException(new IllegalStateException("Duplicate alias names detected: " + duplicates), "");
		}
	}

	/**
	 * Collects all type names into the {@link CollectionOfDuplicates},<br>
	 * which only collects the duplicates. <br>
	 * If there are any duplicates, an exception is reported.
	 * 
	 * @param typenames - {@link Set} of {@link TypeElement}s
	 */
	private void reportDuplicateTypeNames(Set<TypeAliasName> typenames) {
		// It does not count as duplicate, when the type is configured to 
		// have multiple aliases, where one of them is defined as primary alias.
		Collection<String> typesWithPrimaryAlias = typenames.stream()
				.filter(TypeAliasName::isPrimary)
				.map(TypeAliasName::getFullqualifiedname)
				.collect(Collectors.toSet());
		Collection<String> duplicates = typenames.stream()
				.map(TypeAliasName::getFullqualifiedname)
				.filter(type -> !typesWithPrimaryAlias.contains(type))
				.collect(Collectors.toCollection(CollectionOfDuplicates::collectionOfDuplicates));
		if (!duplicates.isEmpty()) {
			reportException(new IllegalStateException("Duplicate type names detected: " + duplicates), "");
		}
	}

	private static Set<TypeAliasName> setAllDistinctAliasesToPrimary(Set<TypeAliasName> typenames) {
		Collection<String> typesWithPrimaryAlias = typenames.stream()
				.filter(TypeAliasName::isPrimary)
				.map(TypeAliasName::getFullqualifiedname)
				.collect(Collectors.toSet());
		return typenames.stream()
				.map(type -> typesWithPrimaryAlias.contains(type.getFullqualifiedname())? type : type.asPrimary())
				.collect(Collectors.toSet());
	}

	/**
	 * Collects all type names or primary aliases into the {@link CollectionOfDuplicates},<br>
	 * which only collects the duplicates. <br>
	 * If there are any duplicates, an exception is reported.
	 * 
	 * @param typenames - {@link Set} of {@link TypeElement}s
	 */
	private void reportDuplicatePrimaryAliases(Set<TypeAliasName> typenames) {
		Collection<String> duplicates = typenames.stream()
				.filter(TypeAliasName::isPrimary)
				.map(TypeAliasName::getFullqualifiedname)
				.collect(Collectors.toCollection(CollectionOfDuplicates::collectionOfDuplicates));
		if (!duplicates.isEmpty()) {
			reportException(new IllegalStateException("Duplicate primary aliases detected: " + duplicates), "");
		}
	}
	
	/**
	 * Checks, if there are {@link TypeAlias} annotated elements with an empty name. These elements will be reported,
	 * leading to an compile error.
	 * 
	 * @param typenames - {@link Set} of {@link TypeElement}s
	 */
	private void reportEmptyAliasNames(Set<TypeAliasName> typenames) {
		Collection<String> emptyAliasedTypes = typenames.stream()
				.map(TypeAliasName::getAliasname)
				.filter(alias -> alias.trim().isEmpty())
				.collect(Collectors.toList());
		if (!emptyAliasedTypes.isEmpty()) {
			reportException(new IllegalStateException("Empty alias names detected: " + emptyAliasedTypes), "");
		}
	}

	/**
	 * Gets all {@link TypeAlias}-annotated {@link TypeElement} (e.g. classes).
	 * 
	 * @param annotated        {@link Set} of {@link TypeElement}
	 * @param roundEnvironment {@link RoundEnvironment}
	 * @return Filtered and mapped {@link Set} of {@link TypeElement}
	 */
	private Set<TypeAliasName> getTypeAliasNames(Set<? extends TypeElement> annotated, RoundEnvironment roundEnvironment) {
		return annotated.stream() //
				.filter(TypeAliasAnnotationProcessor::isAnyTypeAliasAnnotationType)
				.flatMap((annotation) -> roundEnvironment.getElementsAnnotatedWith(annotation).stream()) //
				.filter(QualifiedNameable.class::isInstance)
				.map(QualifiedNameable.class::cast)
				.flatMap(TypeAliasAnnotationProcessor::asTypeAliasNames)
				.collect(Collectors.toSet());
	}

	private static Stream<TypeAliasName> asTypeAliasNames(QualifiedNameable element) {
		String annotatedTypeName = element.getQualifiedName().toString();
		Collection<TypeAliasName> aliasnames = new ArrayList<TypeAliasName>();
		TypeAliases typeAliases = element.getAnnotation(TypeAliases.class);
		if (typeAliases != null) {
			for (TypeAlias typeAlias : typeAliases.value()) {
				aliasnames.add(typeAliasNameFor(annotatedTypeName, typeAlias));
			}
		}
		TypeAlias typeAlias = element.getAnnotation(TypeAlias.class);
		if (typeAlias != null) {
			aliasnames.add(typeAliasNameFor(annotatedTypeName, typeAlias));
		}
		return aliasnames.stream();
	}

	private static TypeAliasName typeAliasNameFor(String annotatedTypeName, TypeAlias typeAlias) {
		return TypeAliasName.ofAnnotation(typeAlias).assignedTypeName(annotatedTypeName).build();
	}

	private static boolean isAnyTypeAliasAnnotationType(QualifiedNameable element) {
		return element.getQualifiedName().contentEquals(TypeAlias.class.getName())
				|| element.getQualifiedName().contentEquals(TypeAliases.class.getName());
	}

	private void note(String message) {
		getMessager().printMessage(Kind.NOTE, message);
	}

	private <E extends Exception> E reportException(E e, String details) {
		getMessager().printMessage(Kind.ERROR, e.getMessage() + ": " + details);
		return e;
	}

	private Filer getFiler() {
		return processingEnv.getFiler();
	}

	private Messager getMessager() {
		return processingEnv.getMessager();
	}

	private static Set<String> setOf(String... entries) {
		Set<String> set = new HashSet<>();
		for (String entry : entries) {
			set.add(entry);
		}
		return set;
	}
}