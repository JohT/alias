package org.alias.example.multiple;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.TypeAliases;

/**
 * Shows, how to define multiple aliases for one type.
 * <p>
 * One of these aliases needs to be declared as primary, <br>
 * otherwise it would not be possible to generate a bidirectional resource
 * bundle.
 */
@TypeAliases({ @TypeAlias(value = "PrimaryAlias", primary = true), @TypeAlias("SecondaryAlias") })
public class MultipleAliasedFileType {

}
