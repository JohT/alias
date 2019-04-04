package org.alias.annotation.internal.templates;

/**
 * Implementations are able to replace placeholders inside template files.
 * 
 * @author JohT
 */
public interface PlaceholderReplacer {

	/**
	 * Replaces any placeholders in the given {@link String} and returns the result.
	 * <p>
	 * If one template line leads to multiple result lines, <br>
	 * then the returned {@link String} contains all the lines separated by new line characters.
	 * 
	 * @param templateLine {@link String}
	 * @return {@link String}
	 */
	String replacePlaceholderInLine(String templateLine);
}
