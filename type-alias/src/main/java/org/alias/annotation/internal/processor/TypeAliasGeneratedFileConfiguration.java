package org.alias.annotation.internal.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.alias.annotation.TypeAliasGeneratedFile;
import org.alias.annotation.internal.templates.TypeAliasName;

/**
 * Looks for {@link TypeAliasGeneratedFile}-annotated <code>package-info.java</code> files,<br>
 * containing optional settings like file name, file extension, file type, ... .
 * <p>
 * Each package is represented by {@link TypeAliasPackageInfo} and contains the package name, the file name, and all detected types, that
 * are contained inside the package and its sub packages.<br>
 * <p>
 * Without any {@link TypeAliasGeneratedFile} annotation, <br>
 * the default file <code>TypeAlias.properties</code> located in the default package is generated.
 * 
 * @author JohT
 */
class TypeAliasGeneratedFileConfiguration {

	private final Set<? extends TypeElement> annotations;
	private final RoundEnvironment roundEnvironment;
	private final Messager messager;
	private final Map<String, TypeAliasPackageInfo> packageInfo = new TreeMap<>(MostSpecificPackageFirst.INSTANCE);

	public TypeAliasGeneratedFileConfiguration(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment, Messager messager) {
		this.annotations = annotations;
		this.roundEnvironment = roundEnvironment;
		this.messager = messager;
	}

	/**
	 * Gets a {@link Collection} of all {@link TypeAliasPackage}s.
	 * 
	 * @param typeAliasNames
	 * 
	 * @return {@link Collection} of {@link TypeAliasPackage}s.
	 */
	public Collection<TypeAliasPackage> getAllPackagesFor(Collection<? extends TypeAliasName> typeAliasNames) {
		Map<String, Set<TypeAliasName>> classesPerKeyPackage = getTypeNamesPerPackage(typeAliasNames);
		Collection<TypeAliasPackage> packages = new ArrayList<>();
		for (Entry<String, TypeAliasPackageInfo> settings : getPackageInfo().entrySet()) {
			Set<TypeAliasName> typesInPackage = classesPerKeyPackage.get(settings.getKey());
			if (!typesInPackage.isEmpty()) {
				packages.add(new TypeAliasPackage(settings.getValue(), typesInPackage));
			}
		}
		return packages;
	}

	private Map<String, Set<TypeAliasName>> getTypeNamesPerPackage(Collection<? extends TypeAliasName> typeAliasNames) {
		Map<String, Set<TypeAliasName>> packagesPerKey = new HashMap<>();
		Set<String> alreadyAssociatedClassNames = new HashSet<>();
		for (String packagename : getPackageInfo().keySet()) {
			packagesPerKey.put(packagename, new HashSet<>());
			for (TypeAliasName typename : typeAliasNames) {
				String classname = typename.getFullqualifiedname();
				if (classname.startsWith(packagename) && !alreadyAssociatedClassNames.contains(classname)) {
					packagesPerKey.get(packagename).add(typename);
					alreadyAssociatedClassNames.add(classname);
				}
			}
		}
		return packagesPerKey;
	}

	private Map<String, TypeAliasPackageInfo> getPackageInfo() {
		if (packageInfo.isEmpty()) {
			TypeAliasPackageInfo defaultPackageConfig = TypeAliasPackageInfo.defaultPackageInfo();
			packageInfo.put(defaultPackageConfig.getPackageName(), defaultPackageConfig);
			packageInfo.putAll(readFileGenerationConfigurationPerPackage());
			note("detected package settings %s", packageInfo.toString());
		}
		return packageInfo;
	}

	private Map<String, TypeAliasPackageInfo> readFileGenerationConfigurationPerPackage() {
		return annotations.stream()
				.filter(annotation -> annotation.getQualifiedName().contentEquals(TypeAliasGeneratedFile.class.getName()))
				.flatMap((annotation) -> roundEnvironment.getElementsAnnotatedWith(annotation).stream())
				.filter(QualifiedNameable.class::isInstance)
				.map(QualifiedNameable.class::cast)
				.map(element -> packageInfoFrom(element))
				.collect(Collectors.toMap(info -> info.getPackageName(), info -> info, reportErrorOnDuplicate()));
	}

	/**
	 * Returns a merge function, suitable for use in {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
	 * {@link #toMap(Function, Function, BinaryOperator) toMap()}, which reports duplicate keys as compilation errors.
	 * 
	 * @param <T> the type of input arguments to the merge function
	 * @return a merge function which uses {@link Messager#printMessage(Kind, CharSequence)} to report an
	 *         {@link Kind#ERROR}.
	 */
	private <T> BinaryOperator<T> reportErrorOnDuplicate() {
		return (u, v) -> {
			messager.printMessage(Kind.ERROR, String.format("Duplicate key %s", u));
			return u;
		};
	}

	private static TypeAliasPackageInfo packageInfoFrom(QualifiedNameable element) {
		TypeAliasGeneratedFile annotation = element.getAnnotation(TypeAliasGeneratedFile.class);
		PackageElement packageElement = (element instanceof PackageElement)? (PackageElement)element : (PackageElement) element.getEnclosingElement();
		return TypeAliasPackageInfo.create()
				.packageName(packageElement.getQualifiedName().toString())
				.fileName(annotation.value())
				.template(annotation.template())
				.build();
	}

	private void note(String message, Object... messageParameters) {
		messager.printMessage(Kind.NOTE, String.format(message, messageParameters));
	}

	@Override
	public String toString() {
		return "TypeAliasGeneratedFileConfiguration [annotations=" + annotations + ", roundEnvironment=" + roundEnvironment + ", messager="
				+ messager + ", packageInfo=" + packageInfo + "]";
	}
}