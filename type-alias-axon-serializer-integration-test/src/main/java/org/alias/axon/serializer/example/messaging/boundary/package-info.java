/**
 * This package contains the boundary API for messaging. (see "hexagonal architecture")
 * <p>
 * It separates the domain model ("core", "inside") from the messaging infrastructure library axon ("outside").<br>
 * The domain model should only contain business logic and shouldn't depend on infrastructure that may change. <br>
 * Therefore, the domain model should only depend on the abstractions in this package. <br>
 * It shouldn't depend directly on the underlying implementations.<br>
 * It should be possible to update to a new version of a infrastructure library - like axon - without much effort.<br>
 * Such a change shouldn't affect the business logic. <br>
 * It shouldn't matter that much, if library updates contain "breaking changes", <br>
 * since those wouldn't be done if the improvement wasn't worth it.
 * <p>
 * The package "domain.model" should only depend on this API package and the messages API. <br>
 * Ideally, there are no axon dependencies inside the package "domain.model" at all.<br>
 */
package org.alias.axon.serializer.example.messaging.boundary;

;
