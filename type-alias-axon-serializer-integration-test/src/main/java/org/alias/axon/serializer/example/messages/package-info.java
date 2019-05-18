/**
 * This package contains the main API of the application, represented by messages.
 * <p>
 * Ideally, this package doesn't depend on anything but plain java. <br>
 * The only exception here is a dependency to the module "type-alias", <br>
 * which is a compile-time-only dependency.
 */
@TypeAliases({
		// Define aliases for types that can't be annotated directly
		@TypeAlias(value = "MessagingMetaData", type = "org.axonframework.messaging.MetaData"),
		@TypeAlias(value = "MessagingGapAwareTrackingToken", type = "org.axonframework.eventhandling.GapAwareTrackingToken"),
})
@TypeAliasGeneratedFile(MessageAliases.MESSAGE_ALIASES_RESOURCE_BUNDLE_NAME)
package org.alias.axon.serializer.example.messages;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.TypeAliasGeneratedFile;
import org.alias.annotation.TypeAliases;;
;
