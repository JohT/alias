package org.alias.axon.serializer.experimental;

import java.util.ResourceBundle;

import org.alias.axon.serializer.TypeDecoratableSerializer;

/**
 * This {@link ClassLoader} is a workaround that is only meant to be used temporary for one specific case: <br>
 * If a Serializer, that supports {@link ClassLoader}s as parameter (or injectable dependency), should load an already
 * registered {@link Class}es from a {@link ResourceBundle}, this implementation can be used. <br>
 * <b>Important Notes:</b> This {@link ClassLoader} is not meant to be stable for general use.
 * 
 * @author JohT
 */
public class AliasableResourceBundleClassloader extends ClassLoader {

	private final ResourceBundle resourceBundle;
	
	/**
	 * Creates a standard {@link AliasableResourceBundleClassloader} using the {@link ClassLoader} of this class
	 * ({@link Class#getClassLoader()}) and the default {@link ResourceBundle}
	 * {@link TypeDecoratableSerializer#DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME}.
	 * 
	 * @return {@link AliasableResourceBundleClassloader}
	 */
	public static final AliasableResourceBundleClassloader standard() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(TypeDecoratableSerializer.DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME);
		return new AliasableResourceBundleClassloader(resourceBundle, AliasableResourceBundleClassloader.class.getClassLoader());
	}

	public AliasableResourceBundleClassloader(ResourceBundle resourceBundle, ClassLoader parent) {
		super(parent);
		this.resourceBundle = resourceBundle;
	}

	/**
	 * Looks up the {@link String} name in the {@link ResourceBundle}.
	 * <p>
	 * If the {@link Class} can be found within the {@link ResourceBundle}, it will be returned.<br>
	 * The name might be an alias or the full qualified name, <br>
	 * as far as the {@link ResourceBundle} contains both mappings.
	 * <p>
	 * If there is no suitable entry in the {@link ResourceBundle}, <code>null</code> will be returned.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public Class findClass(String name) throws ClassNotFoundException {
		if (!resourceBundle.containsKey(name)) {
			return null;
		}
		Object value = resourceBundle.getObject(name);
		if (value instanceof Class) {
			// 'name' was an alias key. The value is the assigned Class, which can be returned directly.
			return (Class) value;
		}
		if (value instanceof String) {
			// 'name' may be a full qualified class name key. The value may be the assigned alias name.
			// If the ResourceBundle contains both assignments (alias->Class, classname->alias),
			// the a second try should lead to the Class.
			String alias = (String) value;
			if (resourceBundle.containsKey(alias)) {
				value = resourceBundle.getObject(alias);
				if (value instanceof Class) {
					return (Class) value;
				}
			}
		}
		return null;
    }

	/**
	 * If there is a suitable entry in the {@link ResourceBundle}, <br>
	 * the returned {@link Class} will be taken from there. <br>
	 * <b>Important note:</b><br>
	 * This implementation does not work as normally specified: It first tries to find the Class in the
	 * {@link ResourceBundle}, and continues then by calling the parents.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> resourceBundleRegisteredClass = findClass(name);
			if (resourceBundleRegisteredClass != null) {
				return resourceBundleRegisteredClass;
			}
			return super.loadClass(name, resolve);
		}
	}

	@Override
	public String toString() {
		return "AliasableResourceBundleClassloader [resourceBundle=" + resourceBundle + ", getParent()=" + getParent() + "]";
	}
}