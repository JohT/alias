package org.alias.example.customized.external;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.TypeAliases;

/**
 * Shows how to define aliases for other (e.g. external types).<b> It is possible to define more than one alias.<br>
 * It is also possible to include the current annotated type itself by leaving out the type parameter.
 */
@TypeAliases({
		@TypeAlias("AliasForNotAnnotatedResourceBundleFile"),
		@TypeAlias(value = "AliasForExternalAlias", type = "org.alias.example.customized.external.NotAnnotatedExternalResourceBundleFileType")
})
public class ExternalAnnotationResourceBundleFileType {

}
